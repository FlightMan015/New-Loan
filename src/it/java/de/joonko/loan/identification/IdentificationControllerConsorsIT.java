package de.joonko.loan.identification;

import com.github.tomakehurst.wiremock.WireMockServer;

import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.identification.model.CreateIdentResponse;
import de.joonko.loan.identification.model.IdentificationAuditTrail;
import de.joonko.loan.identification.model.InitiateIdentificationRequest;
import de.joonko.loan.identification.model.KycAuditStatus;
import de.joonko.loan.identification.service.IdentificationAuditTrailRepository;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.partner.consors.ConsorsAcceptedOfferStore;
import de.joonko.loan.partner.consors.ConsorsPropertiesConfig;
import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferResponse;
import de.joonko.loan.partner.consors.model.Link;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;

import static de.joonko.loan.identification.IdentificationFixture.getInitiateIdentificationRequest;
import static de.joonko.loan.identification.IdentificationFixture.getUserPersonalInformationStore;
import static de.joonko.loan.identification.model.KycAuditStatus.CONTRACT_UPLOADED;
import static de.joonko.loan.identification.model.KycAuditStatus.CONTRACT_UPLOAD_ERROR;
import static de.joonko.loan.identification.model.KycAuditStatus.IDENT_CREATED;
import static de.joonko.loan.identification.model.KycAuditStatus.IDENT_CREATION_ERROR;
import static de.joonko.loan.identification.model.KycAuditStatus.KYC_INITIATED;
import static de.joonko.loan.identification.model.KycAuditStatus.KYC_LINK_CREATED;
import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailVerifiedJwt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@AutoConfigureWebTestClient(timeout = "36000")
@SpringBootTest
@ContextConfiguration(initializers = WireMockInitializer.class)
@ActiveProfiles("integration")
@ExtendWith(RandomBeansExtension.class)
public class IdentificationControllerConsorsIT {

    @Autowired
    private WebTestClient webClient;
    @Autowired
    private WireMockServer mockServer;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IdentificationPropConfig identificationPropConfig;
    @Autowired
    private IdentificationAuditTrailRepository identificationAuditTrailRepository;

    @Autowired
    private ConsorsPropertiesConfig consorsPropertiesConfig;

    @Random
    LoanDemandStore loanDemandStore;
    @Random
    LoanOfferStore loanOfferStore;

    private IDNowMockServerClient idNowowMockServerClient;
    private ConsorsMockServerClient consorsMockServerClient;
    private static final String USER_UUID_1 = "35004d2f-ee8a-45fe-97e9-0542e1a0160b";
    private static final String USER_UUID_2 = "b16024cc-23a7-45c9-8aa8-57b5fc45bfcc";
    private static final String USER_UUID_3 = "33e9fbff-42d9-46fb-95d0-4a7c72635a81";

    @BeforeEach
    void clearMockServerExpectations() {
        mockServer.resetAll();
        idNowowMockServerClient = new IDNowMockServerClient(mockServer);
        consorsMockServerClient = new ConsorsMockServerClient(mockServer);
        ReflectionTestUtils.setField(consorsPropertiesConfig, "webidEnabled", false);
    }

    @Disabled
    @Test
    @DisplayName("Should create identification url and store status in database: Happy Flow")
    void createIdentificationUrl() throws InterruptedException {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_1);
        InitiateIdentificationRequest requestBody = getInitiateIdentificationRequest();
        String applicationId = requestBody.getApplicationId();
        String loanOfferId = requestBody.getLoanOfferId();
        String contractIdentifier = "59915313";
        saveFakeApplication(applicationId, contractIdentifier);
        idNowowMockServerClient.fakeGetToken("joonkocfgesign");
        idNowowMockServerClient.fakeCreateIdent(contractIdentifier, "joonkocfgesign");
        consorsMockServerClient.fakeGetContract();
        consorsMockServerClient.fakeGetToken();
        idNowowMockServerClient.fakeUploadContractConsors(contractIdentifier);
        mongoTemplate.insert(getLoanOfferStore(applicationId, loanOfferId, USER_UUID_1));
        mongoTemplate.insert(getUserPersonalInformationStore(USER_UUID_1));

        FluxExchangeResult<CreateIdentResponse> result = webClient
                .mutateWith(mockJwt().jwt(jwt))
                .post()
                .uri(URI.create("/api/v1/loan/identification"))
                .bodyValue(requestBody)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CreateIdentResponse.class);

        CreateIdentResponse createIdentResponse = result.getResponseBody()
                .blockFirst();
        Thread.sleep(2000);
        assertThat(createIdentResponse.getKycUrl()).isEqualTo(identificationPropConfig.getIdentificationHost() + "TST-ZSHVT");
        List<IdentificationAuditTrail> identificationAuditTrails =
                identificationAuditTrailRepository.findByApplicationId(requestBody.getApplicationId());
        makeAssertionsForIdentificationAuditTrail(identificationAuditTrails);
    }

    @Test
    @DisplayName("Ident Creation fail")
    void identificationFailure() throws InterruptedException {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_2);
        InitiateIdentificationRequest requestBody = getInitiateIdentificationRequest();
        String applicationId = requestBody.getApplicationId();
        String loanOfferId = requestBody.getLoanOfferId();
        String contractIdentifier = "59915313";
        saveFakeApplication(applicationId, contractIdentifier);
        mongoTemplate.insert(getLoanOfferStore(applicationId, loanOfferId, USER_UUID_2));
        mongoTemplate.insert(getUserPersonalInformationStore(USER_UUID_2));

        idNowowMockServerClient.fakeGetToken("joonkocfgesign");
        idNowowMockServerClient.fakeCreateIdentFailure(applicationId, "joonkocfgesign");

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .post()
                .uri(URI.create("/api/v1/loan/identification"))
                .bodyValue(requestBody)
                .exchange()
                .expectStatus()
                .is5xxServerError();

        Thread.sleep(2000);
        List<IdentificationAuditTrail> identificationAuditTrails =
                identificationAuditTrailRepository.findByApplicationId(requestBody.getApplicationId());
        makeAssertionsForIdentCreationFailFail(identificationAuditTrails);
    }

    @Test
    @DisplayName("Contract upload fail")
    void contractUploadFailure() throws InterruptedException {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_3);
        InitiateIdentificationRequest requestBody = getInitiateIdentificationRequest();
        String applicationId = requestBody.getApplicationId();
        String loanOfferId = requestBody.getLoanOfferId();
        mongoTemplate.insert(getLoanOfferStore(applicationId, loanOfferId, USER_UUID_3));
        mongoTemplate.insert(getUserPersonalInformationStore(USER_UUID_3));

        String contractIdentifier = "59915313";
        saveFakeApplication(applicationId, contractIdentifier);
        idNowowMockServerClient.fakeGetToken("joonkocfgesign");
        idNowowMockServerClient.fakeCreateIdent(contractIdentifier, "joonkocfgesign");
        consorsMockServerClient.fakeGetToken();
        consorsMockServerClient.fakeGetContract();
        consorsMockServerClient.fakeUploadContractFailureConsors(contractIdentifier);

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .post()
                .uri(URI.create("/api/v1/loan/identification"))
                .bodyValue(requestBody)
                .exchange()
                .expectStatus()
                .is5xxServerError();

        Thread.sleep(2000);
        List<IdentificationAuditTrail> identificationAuditTrails =
                identificationAuditTrailRepository.findByApplicationId(requestBody.getApplicationId());
        makeAssertionsForContractUploadFail(identificationAuditTrails);
    }

    private LoanOfferStore getLoanOfferStore(String applicationId, String offerId, String userUuid) {
        loanOfferStore.setApplicationId(applicationId);
        loanOfferStore.setUserUUID(userUuid);
        loanOfferStore.setLoanOfferId(offerId);
        loanOfferStore.setIsAccepted(true);
        loanOfferStore.setKycStatus(null);
        loanOfferStore.setKycProvider(null);
        loanOfferStore.setKycUrl(null);
        loanOfferStore.setContracts(null);
        loanOfferStore.setOffer(LoanOffer.builder()
                .loanProvider(new LoanProvider("Consors Finanz"))
                .build());

        return loanOfferStore;
    }

    private void saveFakeApplication(String applicationid, String contractIdentifier) {
        ConsorsAcceptOfferResponse emailSubscriptionDocument = new ConsorsAcceptOfferResponse();
        emailSubscriptionDocument.setContractIdentifier(contractIdentifier);
        Link link = new Link();
        link.setRel("_downloadSubscriptionDocument");
        link.setHref("/subscription/457561707845636568686972596164345161584577776671495554784533444353627153714b3254426c733d/documents?version=5.0");
        emailSubscriptionDocument.setLinks(List.of(link));


        ConsorsAcceptedOfferStore consorsAcceptedOfferStore = ConsorsAcceptedOfferStore.builder()
                .consorsAcceptOfferResponse(emailSubscriptionDocument)
                .loanApplicationId(applicationid)
                .build();
        mongoTemplate.insert(consorsAcceptedOfferStore);

        loanDemandStore.setApplicationId(applicationid);
        mongoTemplate.insert(loanDemandStore);
    }

    private void makeAssertionsForIdentificationAuditTrail(List<IdentificationAuditTrail> identificationAuditTrails) {
        Set<KycAuditStatus> statusSet = new HashSet<>() {{
            add(KYC_INITIATED);
            add(IDENT_CREATED);
            add(KYC_LINK_CREATED);
            add(CONTRACT_UPLOADED);
        }};
        identificationAuditTrails.forEach(l -> {
            assertTrue(statusSet.remove(KycAuditStatus.valueOf(l.getStatus())));
        });
        assertThat(statusSet).isEmpty();
    }

    private void makeAssertionsForIdentCreationFailFail(List<IdentificationAuditTrail> identificationAuditTrails) {
        Set<KycAuditStatus> statusSet = new HashSet<>() {{
            add(IDENT_CREATION_ERROR);
        }};
        identificationAuditTrails.forEach(l -> {
            assertTrue(statusSet.remove(KycAuditStatus.valueOf(l.getStatus())));
        });
        assertThat(statusSet).isEmpty();
    }

    private void makeAssertionsForContractUploadFail(List<IdentificationAuditTrail> identificationAuditTrails) {
        Set<KycAuditStatus> statusSet = new HashSet<>() {{
            add(IDENT_CREATED);
            add(CONTRACT_UPLOAD_ERROR);
        }};
        identificationAuditTrails.forEach(l -> {
            assertTrue(statusSet.remove(KycAuditStatus.valueOf(l.getStatus())));
        });
        assertThat(statusSet).isEmpty();
    }
}
