package de.joonko.loan.partner.solaris;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.partner.solaris.auth.AccessToken;
import de.joonko.loan.common.partner.solaris.auth.SolarisAuthService;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.email.MailClientGateway;
import de.joonko.loan.email.model.Email;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.api.SolarisResponses;
import de.joonko.loan.partner.consors.ConsorsFixtures;
import de.joonko.loan.partner.solaris.model.SolarisSignedDocTrail;
import de.joonko.loan.user.service.UserPersonalInformationStore;
import de.joonko.loan.user.states.ReactiveUserStatesStoreService;
import de.joonko.loan.user.states.UserStateServiceImpl;
import io.github.glytching.junit.extension.random.Random;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static de.joonko.loan.email.util.ConsorsDacPDFHelper.addCoverLetterToPdf;
import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailVerifiedJwt;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(initializers = WireMockInitializer.class)
@ActiveProfiles("integration")
class CustomerDocumentServiceTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WireMockServer mockServer;

    @Autowired
    private CustomerDocumentService customerDocumentService;

    private MailClientGateway mailClientGateway;

    @SpyBean
    private SolarisAuthService solarisAuthService;

    @Captor
    ArgumentCaptor<Email> emailCaptor;

    private final String SOLARIS_EMAIL_TEMPLATE = "solaris/SolarisSignedContractEmailTemplate.html";

    @BeforeEach
    void setUp() {
        mailClientGateway = mock(MailClientGateway.class);
        ReflectionTestUtils.setField(customerDocumentService, "mailClientGateway", mailClientGateway);
    }

    @Test
    void getCustomerDocs() {
        final String applicationId = randomUUID().toString();
        mongoTemplate.insert(getSolarisSignedDoc(applicationId));
        mongoTemplate.insert(getSolarisAcceptOfferResponse(applicationId));
        mongoTemplate.insert(getLoanDemand(applicationId));
        mongoTemplate.insert(getUserPersonalInformation());

        mockGetSignings(mockServer, "person1", "signing1");
        mockGetDocument(mockServer, "person1", "document_1");
        mockGetDocument(mockServer, "person1", "document_2");

        when(solarisAuthService.getToken(anyString())).thenReturn(Mono.just(new AccessToken("accessToken", "bearer", 30000)));

        customerDocumentService.getCustomerDocs();

        verify(mailClientGateway, timeout(2000)).sendEmailWithAttachment(emailCaptor.capture());

        Resource resource = new ClassPathResource(SOLARIS_EMAIL_TEMPLATE);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String emailTemplate = br.lines().collect(Collectors.joining("\n"));
            emailTemplate = emailTemplate.replace("__firstName__", "fake_firstName")
                    .replace("__loanSelectedBank__", Bank.DEUTSCHE_FINANZ_SOZIETÄT.label);
            assertEquals(emailCaptor.getValue().getHtml(), emailTemplate);
        } catch (IOException e) {
            throw new RuntimeException("Fail to load email template");
        }
    }

    private SolarisSignedDocTrail getSolarisSignedDoc(String applicationId) {
        return SolarisSignedDocTrail.builder()
                .applicationId(applicationId)
                .emailSent(false)
                .build();
    }

    private SolarisAcceptOfferResponseStore getSolarisAcceptOfferResponse(String applicationId) {
        return SolarisAcceptOfferResponseStore.builder()
                .applicationId(applicationId)
                .personId("person1")
                .signingId("signing1")
                .build();
    }

    private LoanDemandStore getLoanDemand(String applicationId) {
        return LoanDemandStore.builder()
                .applicationId(applicationId)
                .userUUID("fakeUUID1")
                .build();
    }

    private UserPersonalInformationStore getUserPersonalInformation() {
        var userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setUserUUID("fakeUUID1");
        userPersonalInformationStore.setEmail("dummy@fake.com");
        userPersonalInformationStore.setFirstName("fake_firstName");
        return userPersonalInformationStore;
    }

    private void mockGetSignings(WireMockServer mockServer, String personId, String signingId) {
        mockServer.stubFor(
                WireMock.get("/solaris/v1/persons/" + personId + "/signings/" + signingId)
                        .withHeader("Authorization", equalTo("Bearer accessToken"))
                        .willReturn(aResponse()
                                .withBody(de.joonko.loan.offer.api.SolarisResponses.GET_SIGNINGS_RESPONSE)
                                .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    private void mockGetDocument(WireMockServer mockServer, String personId, String documentId) {
        mockServer.stubFor(
                WireMock.get("/solaris/v1/persons/" + personId + "/documents/" + documentId + "/file")
                        .withHeader("Authorization", equalTo("Bearer accessToken"))
                        .willReturn(aResponse()
                                .withBody("This is test pdf dummy contract byte stream".getBytes())
                                .withStatus(HttpStatus.OK.value())
                        )
        );
    }
}