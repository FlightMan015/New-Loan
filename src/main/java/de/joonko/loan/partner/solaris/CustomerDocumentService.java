package de.joonko.loan.partner.solaris;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.partner.solaris.auth.AccessToken;
import de.joonko.loan.common.partner.solaris.auth.SolarisAuthService;
import de.joonko.loan.config.MailConfig;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.email.MailClientGateway;
import de.joonko.loan.email.model.Email;
import de.joonko.loan.email.model.EmailAttachments;
import de.joonko.loan.user.service.UserPersonalInformationRepository;
import de.joonko.loan.user.service.UserPersonalInformationStore;
import de.joonko.loan.partner.solaris.model.Documents;
import de.joonko.loan.partner.solaris.model.SolarisGetSignedDocumentResponse;
import de.joonko.loan.partner.solaris.model.SolarisSignedDocTrail;
import de.joonko.loan.util.EncrDecrService;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerDocumentService {

    private final SolarisSignedDocService solarisSignedDocService;
    private final SolarisStoreService solarisStoreService;
    private final SolarisAuthService solarisAuthService;
    private final SolarisPropertiesConfig solarisPropertiesConfig;
    private static final int REQUIRED_SIGNED_DOCUMENTS = 2;
    private static final String SSD_MANDATE = "ldoc";
    private static final String CONTRACT = "cdoc";
    private final LoanDemandStoreService loanDemandStoreService;
    private final MailClientGateway mailClientGateway;
    private final MailConfig mailConfig;
    private final EncrDecrService encrDecrService;
    private final UserPersonalInformationRepository userPersonalInformationRepository;

    @Qualifier("solarisWebClientBuilder")
    private final WebClient.Builder solarisWebClientBuilder;

    public void getCustomerDocs() {
        List<SolarisSignedDocTrail> applicationsNotSent = solarisSignedDocService.getApplicationsNotSent();
        log.info("Email needs to be sent to customers {} ", applicationsNotSent.size());
        applicationsNotSent.stream().forEach(applications -> {
            String applicaitonId = applications.getApplicationId();
            SolarisAcceptOfferResponseStore solarisAcceptOfferResponseStore = solarisStoreService.getAcceptOfferResponseStoreByApplicationId(applicaitonId)
                    .get(0);
            AtomicReference<AccessToken> accessTokenAtomicReference = new AtomicReference<>();
            solarisAuthService.getToken(applicaitonId)
                    .flatMap(accessToken -> {
                        accessTokenAtomicReference.set(accessToken);
                        return getSignings(solarisAcceptOfferResponseStore, accessToken);
                    })
                    .filter(solarisGetSignedDocumentResponse -> solarisGetSignedDocumentResponse.getSignedDocuments().size() == REQUIRED_SIGNED_DOCUMENTS)
                    .doOnSuccess(solarisGetSignedDocumentResponse -> uploadDocsoS3(solarisAcceptOfferResponseStore, accessTokenAtomicReference.get(), solarisGetSignedDocumentResponse.getSignedDocuments()))
                    .subscribe();
        });
    }

    private Mono<SolarisGetSignedDocumentResponse> getSignings(SolarisAcceptOfferResponseStore solarisAcceptOfferResponseStore, AccessToken accessToken) {
        WebClient client = solarisWebClientBuilder
                .defaultHeaders(accessToken.bearer())
                .build();
        return client.get()
                .uri(uriBuilder -> buildUriToGetSignings(solarisPropertiesConfig.getPersonsEndpoint(), solarisPropertiesConfig.getSigningEndPoint(), solarisAcceptOfferResponseStore.getPersonId(), solarisAcceptOfferResponseStore.getSigningId(), uriBuilder))
                .retrieve()
                .bodyToMono(SolarisGetSignedDocumentResponse.class);
    }

    private void uploadDocsoS3(SolarisAcceptOfferResponseStore solarisAcceptOfferResponseStore, AccessToken accessToken, List<Documents> documents) {
        log.info("Documents to be uploaded {} ", documents);
        WebClient client = solarisWebClientBuilder
                .defaultHeaders(accessToken.bearer())
                .build();
        String applicationId = solarisAcceptOfferResponseStore.getApplicationId();
        String userUUID = loanDemandStoreService.findById(applicationId).orElseThrow().getUserUUID();
        UserPersonalInformationStore userPersonalInformationStore = userPersonalInformationRepository.findById(userUUID).orElseThrow();
        String emailId = userPersonalInformationStore.getEmail();
        Map<String, byte[]> docsInByte = new HashMap();
        documents.stream().forEach(docs -> {
            client.get()
                    .uri(uriBuilder -> buildUriToGetDocuments(solarisPropertiesConfig.getPersonsEndpoint(), "/documents", solarisAcceptOfferResponseStore.getPersonId(), docs.getDocumentId(), uriBuilder))
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .doOnSuccess(doc -> {
                        String docType = docs.getName().contains(SSD_MANDATE) ? SSD_MANDATE : CONTRACT;
                        try {
                            docsInByte.put(docType, IOUtils.toByteArray(new ByteArrayInputStream(doc)));
                            sendEmailToCustomer(docsInByte, emailId, applicationId);
                        } catch (IOException e) {
                            log.error("Error while converting documents to byteArray", e);
                        }
                    })
                    .subscribe();
        });


    }

    private void sendEmailToCustomer(Map<String, byte[]> docsInByte, String emailId, String applicationId) {
        if (docsInByte.size() == 2) {
            mailClientGateway.sendEmailWithAttachment(getEmailModel(docsInByte, emailId, applicationId));
            solarisSignedDocService.updateEmailSentStatusToTrue(applicationId);
        }
    }

    private Email getEmailModel(Map<String, byte[]> docsInByte, String emailId, String applicationId) {
        LoanDemandStore loanDemandStore = loanDemandStoreService.findById(applicationId).orElseThrow();
        UserPersonalInformationStore userPersonalInformationStore = userPersonalInformationRepository.findById(loanDemandStore.getUserUUID()).orElseThrow();
        String firstName = userPersonalInformationStore.getFirstName();

        List<EmailAttachments> emailAttachments = new ArrayList();
        docsInByte.forEach((fileName, doc) -> emailAttachments.add(EmailAttachments.builder()
                .content(Base64.getEncoder().encodeToString(doc))
                .fileName(applicationId + "_" + fileName + ".pdf")
                .encoding("base64")
                .build()));
        emailAttachments.add(EmailAttachments.builder()
                .content("iVBORw0KGgoAAAANSUhEUgAAAKUAAAAfCAYAAACGeg3JAAAABHNCSVQICAgIfAhkiAAAE0pJREFUeJztnHt0VfWVxz/7d869NwnhERDIG9FUkZdWrEVti+3YsdXadrWms1YfWtsSgRhAKo74mJRaH4gFlUclVVuGOu2YtrNaa0sZ28po66OUUUS0VEAggQYC8kxy7z3nt+ePG/I659zcUDrtLOe7FuuunN9j7/N77N/+ffc+CN0Qaja4NFyQ5lSjqi5BkkeBzyIiWesqHqrX0LTsB4Cecl3eKaisKUHj3wH5x15jrpoEeyO7V3zrb6hdVhgAqp9wWNo+lXETb2fJsUlA9oUzUBxPOogO6ndBAggujhZA/anV4Z0GawxKyJirARn+t1EqNxhAuORj54N5CJFbkfgDLD067pRKse2CNTbn+l7nZvl/nDysRI2hgp/7XPwNYLi/vRKRxQhTEFzQS5H4Iyxtv4jqJ5xTIyVfEXLvy+XvetD+T8BoxBiKgPN3vekNMVOPmvdx4sgWDHAR4jzG1I9deUqk2HYBvJzrq56azfBORqSlVAX8/1VdBggXy9UhVkxAx+E4D/JgKo+Dv/0xCz+Y+6Lqi5Y1bVTOXofawi65Qh4qFyISC9QXGeCg1RtG7i8gkYwD0FbgMZwkby5LnrTOoaiJMcLLI19i2Dyhw6T/OnK6IFTVxUl5BXjG0GHSHEy2Q0P/l9GslvKvvemr44wYnCC/c24HOB8uwhpgOhBcHHA6ovdTNDUPWMPJ34aVXQ81AA1dTypnngHO0yBjg7VzGjShdGY5jrkIOXAO1jkdzRuKI0K+tNGmrZTP3oqxLxOXDSe/cOoNFXvHQuw9WJmCxEehOhhXhELaSWorZTe8CrxEc2pL/wum3lDaWoUxJThW8I3iWEGdFOmOl9nb0AZA+YwyjHMlbTIeJ1aOax3ypY3yxC782pdwOtbT9OjBSDFWTLhn3o+lHHNtHl7hubjk45vgfFu7lz0rtoa0FCpnjsW6FyEyGbQU0UJ8VfKlnTb2Uz57M3iv0OS9nG2cXNpSXyM/1gEyHWFwUAkqELOIB5LDSB79Dv982tHIFxowQqwk9G8pi2vH4MinMHoNyFjUDMZgOGHwHcgYe02isp+UbqCi7lskWJ/74qw3lO6fjBz4CiQuAy3GMUNDdAWjbaAtVCY24M1aRYHzXKScyr2j0by7gfdhjY+IgzU+aBLXvZHyLz+LFFwDci2qZ+OSn2nY+XoqFtccQQpeoKx2Gc2b18H64Cl2MpZy5KxCfGcuItOx6iLigPqAQURR9THyCEy7q4dMoXTOWYidjco/IFoCFCJiQOh1BivHwbRSkfg9tnYFzenfhi1Ow62D96P7b0d1Duj+UGWVYpS7SRR+g0WtwYV70tDw3ZLNUhbP/ggxeRIjixHnPMQM7fSDgxBJIFIO5pMIP6GN+6isKelXraq6BCWHrsNxfoyRWYicjYQsyG45BYgZC1KNY75Hh95CeXV+aF0nFkdsMcJoREq7ftHRYM7BFNwJ3IdwHiLBPgSDyDCQj2BooHzSx6AmuLkH7FNOc0k4czHchqGyl24ixUAJKq+jHY92L8hpLpW1n8exP8EwE+FsRIZEzweDEDMGkasx5keUJeYzclZh32qZxvMq2jm87XGU+siFKQwCuZ68wgXcc6go/IUHDDdcVoSlLJt9BTFdiphJmV08EEgejtwAiUWUzyiLrlcTI8lMYvYeRMbmxK32EmOKEbkZRt/EiC8FN3DSWjSsT5MH1KByXaifHSrLKcOwiPLYRcHuslnKvneIekPp5M8jzAXJC2/nv4jYW2l+pCnz9zSXisnTwXwzs2kHOk4yHMMC4u7NfRdm94peOCHFoW2PYvUGlN0RXSVQmUd+/hLu21c8ICXCNQtffGE8ZXndNBxdisjJc6iCAfkCGlsI1fHQOpXxT4J8A2TkycuRAozcSl7BfKjv/S4JE2VFDGLGIoRb2EiYs8C5PWBxslrKPjxl5YFP48rdiIyIaLMJ38xl9/INXY8qJn4K9Gt/4TgVYvQWEs7cno97K75wQornn/wR4n8V2BLRVQL4Aokh97Oko+qkFQJAw3dXX56ytHZExvrIWeHdqIJ2oBxH9UjmNwsFZfgnykZ/gr6Rq+K6kWC+mjkVssmxR1GOdsqL8H8lD8N0Kg/0tmJJmxsHq+qjtKP2cEZehKsDIEwlLhN6PYu0lPTkKYXKustQuRMId2tUN2P1ZpqXvdD1rOKGUmABYkZFtOkcJz3SqXsbGsE9i8QQZlJe+4Eu1QOVGj/jM7fgh3j+F0Gfi3gxB5XP4chqlh0/N6LOqUNMLge9JLRMSaH8DI+ZqPdBbOoC1FYDS1D+FNpGpBBhNmOuH9P9sCZGnOtR++5wOdqG0ojaGRj7XvAvysjRlaBvhysuo7Eyj8qZ3e6O+v0fc2p3Y1lFyrsSV89F9FKQaNcK8nHMx3v3EYuS032jHjP7oygrETk7omYzwo00Lf9l98NqB7gGzDkRbY4C/4av03G8C/GTFyB8BqUB1ZYInUpQ53rOqBkKUT4dKDcV/J7FbTfhOisRzo+odTGe8y/cu+1L3HLm4Yi+Bg7tYUErZxahehXIkJB6ivAw6CL2LN/To+RPVNX9mg79NSJLQMYH2opMxDrnAjsBZYyUYfUKxASPdaUdo0txUovZ3tDzPV9jfPWvOFyyBaPfCBx/IgJ2Cp4ZBzyf47u3oMzGPbKW5tUdnU93Mr56M0dGuwgLA/5bJhI3nqq6RE7sghWlYsZErH8X4rwrXA+OYriHncue7vV8zPAKrFyFSCKkzXFI38NxdwUHlx/pUbKVqrqnSeuzWL0XMRW99RfBcBlpdxLwXPZw0/yCF/HS12WxmCAyFbd0UtZ+ohDq8AMiPfgxMxY17wtMhKoCP4fkvezutSAzeHNZkqblv8TTr6N6KETGMHAuh5rMxrTuu0CC76FYRL9LKnV3nwWZwZbGFM17v42wCLQjUI5U4pr30BUxc6K5XsXD6CPkyS/Yubp3X1saUxj/cdDNEY1LOJ4c3S02HSFHBMN4NL4ETPgppxzE+AsYtPfbgbJkYhwwMdhGfcR/nJi/koPLjgTK31yWZGfLvyNyP0p7UC1GIXIJTHP7j4HGU4c6TXI4FM35thhUJNzP6L7oCEoV4f7OEbD/yq6GvVlldMgvEDaElqlezAgvD+oNKlMRCdATwEHErukitUPR6OPYJ4AgqSwi+EylpCZzgYm66GQUasaXn0Zau7T/Z+CF0DJMPm6s+7YfnZDhgnwc4YOhN2YlhdpFxLY/wpbGVO/CagfXTkZCTi1hHz6PhW7cLjT6uMnViO6IeIcplJwdz74olx49BxnUAHJ5ZB3hVY60/3fWfiIRwUd2XXSqDdZURdA/7Vjzu35FZHbtutAykdOJuwl4xoCeGaHjbjjW//vtWLELeCW0zHAmXqcByHrRsa9B25uRxXsb2rDyVucp0UdNjWNt9zhFXXREBGFw5sgPdNKB1fvwU8t58xchG6PIgAlG4ACUVprTGyN1P4HtDUfwI1wZZTKDElks5UNHx0Psm1gujyRDkTfw7F0sLAoej7kh/Ibckzw3GkU9tdK8rDknKZam8AJxcdxhMEoxZnRoFWUHO1fnEgVS0G0RRSXka+Y0SRiDhC0qLMg2msqzj6VjjgCpwHPFR3tYx2hLGQ3VFoQnI0+FMUkHY6PmY3tOMXlQnIgLKAzFJkMUr683LG2/FI2tQuQjEd1aRJ7GetdyU360v9kv+gsz7hNslN+JhWm5Eei+7Qi1LgDiKVOKDDaKjNc0VOc2wWq8cDnGx+3kBiPJc1UsSViYA2UU0l5wkB7WMZoSIpKewVSAzmPsV8I3KICNMFCZy2kuBLpgo2g0YnhOH/+m/jcuRQs+jHFWodKdztYbaVSfpO34dOYVvJSDElnQX5jxUguEWw4ln/JRufmyrimOjDio6eAPJT4SQVeIlEJjbtyi1XA5ao/jJzPPoyxlRlaOmVihljY3S6l4qH0VpTUoH4OYT5OOz2HMtcHIzqA2G0l/iRmWo5FQlIhFL63EXb9b8ZoNMYre+wlUVqIaTlJDGvge2lbHgqK3clAgGlZM/5ZyoeLI1ggrNxI78uL+BdUbhPeHl+lukuYwbBGQ4A0eQBhHRW2QUuqLyplFOATDfZlOtpNKZI7cSEsJqEZRdH37+wsspSroU4g2ZL7XCfTjIs4s7JBrA9GoLfssYt6K6PZ0Sib1H0wpqSnAyHvD+2AXRw91Lsr6HXmcM+Ez4DyAcEZEg3aE1bQl5/PV4VFhyNwhTglRPKlqChYqoKi3Awi50ckQxPksVXXBm2BPlO+fgiWceBf7Mon9PjRafLsxNDqjDEHlasZHhCUBqHbwzYeAytBiRzdR9HbGCv61LKWIzc2nVEXkKGIWo/JUhB5DQW+jsvWK3gXrfZA30BCfVrQYI5+jqi7IX3ah3iDxq0KpNwC1rzB4mGdY1DqYEaV14CwByiN6O4TqYo6nbuXWIQeiheYEoeKGUoTpQDBuqnoYz9nDiciDMc0ob4T0YjD6aTq4mpKaglA5lTPPQGVBJlOorxwsmOdpGp8EFEc3A9uDvUgC5Eu8PerK8AGf5lJWegHG3IZyWuj7pHmpi17JZilz/mQkzEVQk5ul7EzI2PngIcTeg2o4syCmAjVfp6K2Z/hSgc1A8EInJo7hi6S8y8LHqd5QevBcjN4IGsy4Uj2Go+t5c1nSJX/ILfg6B9GIeK/sQ/z7Obz9QRZOCO6QSNQbKg6cj9BNIagR0CKwH0flo+F+nt1BTLuP0p0tu6gs/jGq7w5EEcQMBV2KmxhHWe3PUbsN1/dAitDERKydjZGLkTDf2G7E07VdFwsvvQM3vhbljAAFZaQSYRlJHU/5rHUYrwnfOrj5I1Hej/VrEDMhICLjdjyHlW4KRGPSM9LXGxJNrPfpOOjuq4LT41PamIR31yMhY/fyDVTU3QL6vYjEivPALGH0DTNoWZ7hFnXvNsyoZ1Hn7AArI1KOOitIsprS2rW46bcyBU4pevACVGdinIiwtN2IZ18BcPG1NjIBAWkBeye29bGBLUig6mAhHbIA6L7BixpU4kjEh0uKRZ0X4HAPcrXRx6l9At98EQgLFw5BuRFjPgemCZw0KkNByzFmWLhy2oGV/2BPS/eO39vQRmnt93H4JEhFoIk4ZajegTjXoU4LgkF1OCoVGBOV2XMc5fv8eVl3zFrSmiW6+xd+Vux3r8JoOb0/HNu96deUnfcAjr0jkLYmIigfIiY3M3LWfPavPEZTYztj6x7F5yrCghpixqDcimOuhXhT5lTQUYiWIyY8LU45ijiPs2ffHgCD6HqCW1dRdmP9eby+uYF5FcGwUH+wScEwIpMA2/kPyYvmPAF0G8b/diDEtuO03XgsRglP/xfczmTUC8FcgsjETBgxSjd5FqPfhcbePuSefS8BK8PDhZxIGj4TkYsxMhXMWaGJuNB5y9WnSPo/6f08h4SMU4FcEjIAWO8hxx5GdU1oZpXgIvp5EmZWl5u0Y9NG1D6Eavi6yMzHmM65uBgxVZ35omHaWNCfYveuOTEfBvFuQ9jUq5rKG6hfy+HtPzzp/zHDTwpqc58A1TawK9m1IsTHWWjxDzWi9gFUTz7xQ1VR/3XS3j2h8XIafWLeYyg/ypoq1q8cLNhfoek72L/y2En3M2A4uY237XOuNz16EJO+G7XhIUyRQoyZSyx+ZSZLaL2Hl3oEkR+E3uBzhaqP6DpI1tPU2LXADXMKX0N1PiJbO0nVjeB/mcOLnhrwkd0TsSEWCfnwKFQ5moHb0H2riHK4WtYcxzm6GKsLUW2KJoCjYDtQXYvlOlpWPhNZbfvD+4il52FZFcrl9Qe1bahtxNXraV4VjFxkS8jIGVFZ3n2P7/DGmBA3Yeeqt3D0DtRGhTlLULmL0SM/APWGvQ2teP58hJWRJ1g2ZKzsD3DsDHY39Lo4GUB5+/nf4KfnYHU5+LUcuvdFFuYSWciCtDEBS3mCb+z6tW34uhbR69m9aXnP3RKKnas7yJeVCDOAtZlE2wh6pacc7FYsd4M3neYVLxJ908hg+8P7cI/MB+pQXZdJuM1BjrWvYrmduDebHSt2RtYX0l2bSrGZ9uKD6d9NEr8dSGasfmfbTIiyHVd6W3eRoBzBRzScehq073cg96N6sFebbh3PJOYuoLg143PvWXGA5KE7sHYO1j6TqdvPOKkqVv8A9naMf1PYOHUvmuonHC78QB7zi9vob9JyQVVdgg79MoYPZ0J4qhhjOrnANOjL+PYFXN3Irm9FJMlGod5Q3jQM8iaC+Sii7wZKyXwm7CKSQjmGsJO0/hJS/8mgRMvAP7OtN5S2FuFwfibkKlNQHQ4SR1RAPNS24vMGxjwD6f+iye7LHgOe5lI5+VKUidDJJRoVlCSeXRfx+Wo3zpgxCt+9Cnr4zFYUvG0knLXd71jtUDryUhxnUkCOpH7GzlVvhfaf+cT2SowzJtSiWtuK2J/2nrN6Q8ne4ah7ATFzBcokRE4DdUFiqHYgHEFlE6rPId5vaHq9JfQrTHKLVf79Y3x1nEMjxyNSippReN5WBqW3saNsf26x5BwxclYhCc5BZSSGQpK2iUL/tezpWu9AVNUlOGQnEKcUhxFY+0cKk6/xx8dy+jz7fwDpKyMlV2qv8QAAAABJRU5ErkJggg==")
                .fileName("joonko.png")
                .encoding("base64")
                .cid("joonko-logo")
                .build());
        return Email.builder()
                .attachments(emailAttachments)
                .subject(EMAIL_SUBJECT)
                .fromAddress(mailConfig.getFromAddress())
                .ccAddress(mailConfig.getCcAddress())
                .toAddress(userPersonalInformationStore.getEmail())
                .html(getEmailBody(firstName))
                .build();
    }

    private URI buildUriToGetSignings(String endpoint1, String endpoint2, String personId, String signingId, UriBuilder uriBuilder) {

        return uriBuilder.path(endpoint1)
                .pathSegment(personId)
                .path(endpoint2)
                .pathSegment(signingId)
                .build();
    }

    private URI buildUriToGetDocuments(String endpoint1, String endpoint2, String personId, String documendId, UriBuilder uriBuilder) {

        return uriBuilder.path(endpoint1)
                .pathSegment(personId)
                .path(endpoint2)
                .pathSegment(documendId)
                .path("file")
                .build();
    }

    private String getEmailBody(String firstName) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            Resource resource = new ClassPathResource("solaris/SolarisSignedContractEmailTemplate.html");

            try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String emailTemplate = br.lines().collect(Collectors.joining("\n"));
                emailTemplate = emailTemplate.replaceAll("__firstName__", firstName)
                        .replaceAll("__loanSelectedBank__", Bank.DEUTSCHE_FINANZ_SOZIETÄT.label);
                return emailTemplate;
            }
        } catch (IOException e) {
            log.error("Error while reading the email template html file ", e);
        }
        return "";
    }

    private static final String EMAIL_SUBJECT = "Dein  " + Bank.DEUTSCHE_FINANZ_SOZIETÄT.label + " ist auf dem Weg";

}
