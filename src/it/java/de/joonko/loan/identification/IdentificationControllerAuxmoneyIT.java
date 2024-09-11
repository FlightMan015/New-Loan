package de.joonko.loan.identification;

import com.github.tomakehurst.wiremock.WireMockServer;

import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.identification.model.CreateIdentResponse;
import de.joonko.loan.identification.model.GetIdentStatusResponse;
import de.joonko.loan.identification.model.IdentificationAuditTrail;
import de.joonko.loan.identification.model.IdentificationLink;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.model.InitiateIdentificationRequest;
import de.joonko.loan.identification.model.KycAuditStatus;
import de.joonko.loan.identification.service.IdentificationAuditTrailRepository;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.user.service.UserPersonalInformationStore;
import de.joonko.loan.partner.auxmoney.AuxmoneySingleOfferCallResponseStore;

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
import static de.joonko.loan.identification.model.KycAuditStatus.PARTNER_NOTIFICATION_SENT;
import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailVerifiedJwt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@AutoConfigureWebTestClient(timeout = "36000")
@SpringBootTest
@ContextConfiguration(initializers = WireMockInitializer.class)
@ExtendWith(RandomBeansExtension.class)
@ActiveProfiles("integration")
public class IdentificationControllerAuxmoneyIT {

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

    @Random
    private AuxmoneySingleOfferCallResponseStore auxmoneySingleOfferCallResponseStore;
    @Random
    private LoanDemandStore loanDemandStore;
    @Random
    private LoanOfferStore loanOfferStore;

    private IDNowMockServerClient idNowowMockServerClient;
    private AuxmoneyMockServerClient auxmoneyMockServerClient;
    private static final String USER_UUID_1 = "35004d2f-ee8a-45fe-97e9-0542e1a0160a";
    private static final String USER_UUID_2 = "b16024cc-23a7-45c9-8aa8-57b5fc45bfcb";
    private static final String USER_UUID_3 = "33e9fbff-42d9-46fb-95d0-4a7c72635a85";

    @BeforeEach
    void clearMockServerExpectations() {
        mockServer.resetAll();
        idNowowMockServerClient = new IDNowMockServerClient(mockServer);
        auxmoneyMockServerClient = new AuxmoneyMockServerClient(mockServer);
    }

    @Disabled
    @Test
    @DisplayName("Should create identification url, send push notification and store status in database: Happy Flow")
    void createIdentificationUrl() throws InterruptedException {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_1);
        InitiateIdentificationRequest requestBody = getInitiateIdentificationRequest();
        String applicationId = requestBody.getApplicationId();
        String loanOfferId = requestBody.getLoanOfferId();
        auxmoneySingleOfferCallResponseStore.setLoanApplicationId(applicationId);
        mongoTemplate.insert(auxmoneySingleOfferCallResponseStore);
        mongoTemplate.insert(loanDemandStore);
        mongoTemplate.insert(getLoanOfferStore(applicationId, loanOfferId, USER_UUID_1));
        mongoTemplate.insert(getUserPersonalInformationStore(USER_UUID_1));

        idNowowMockServerClient.fakeGetToken("joonkoswkauxmoneyesign");
        idNowowMockServerClient.fakeCreateIdent(applicationId, "joonkoswkauxmoneyesign");
        auxmoneyMockServerClient.fakeGetContract();
        auxmoneyMockServerClient.fakePushNotification();
        idNowowMockServerClient.fakeUploadContractAuxmoney(applicationId);

        FluxExchangeResult<CreateIdentResponse> result = webClient
                .mutateWith(mockJwt().jwt(jwt))
                .post()
                .uri(URI.create("/api/v1/loan/identification/"))
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
    @DisplayName("Should Get Identification status")
    void getIdentificationStatus() {

        // Arrange
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_1);
        idNowowMockServerClient.fakeGetToken("joonkoswkauxmoneyesign");
        idNowowMockServerClient.fakeGetStatus("joonkoswkauxmoneyesign", "47e70ebb-9471-48c2-b673-daa89929e5dd");

        IdentificationAuditTrail auxmoney = IdentificationAuditTrail.builder()
                .applicationId("47e70ebb-9471-48c2-b673-daa89929e5dd")
                .status(KycAuditStatus.KYC_LINK_CREATED.name())
                .loanProvider("Auxmoney")
                .build();
        identificationAuditTrailRepository.save(auxmoney);

        loanDemandStore.setApplicationId(auxmoney.getApplicationId());
        loanDemandStore.setFirstName("515566ea462339d300c22ccfb382cf7a");
        mongoTemplate.insert(loanDemandStore);
        loanOfferStore.setLoanOfferId("5f5798975940571ab99d3e4e");
        loanOfferStore.setUserUUID("1234");
        UserPersonalInformationStore userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setUserUUID("1234");
        userPersonalInformationStore.setFirstName("Rashmin");
        mongoTemplate.insert(userPersonalInformationStore);

        mongoTemplate.insert(loanOfferStore);
        IdentificationLink identificationLink = IdentificationLink.builder()
                .applicationId("47e70ebb-9471-48c2-b673-daa89929e5dd")
                .offerId("5f5798975940571ab99d3e4e")
                .externalIdentId("47e70ebb-9471-48c2-b673-daa89929e5dd")
                .loanProvider(Bank.AUXMONEY.toString())
                .identProvider(IdentificationProvider.ID_NOW)
                .kycUrl("https://go.test.idnow.de/TST-VFZES")
                .build();
        mongoTemplate.insert(identificationLink);

        // Act
        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(URI.create("/api/v1/loan/identification/status?externalIdentId=47e70ebb-9471-48c2-b673-daa89929e5dd"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(GetIdentStatusResponse.class)
                .value(response -> {
                    assertEquals("SUCCESS_DATA_CHANGED", response.getStatus());
                    assertEquals("Rashmin", response.getFirstName());
                    assertEquals(IdentificationProvider.ID_NOW, response.getKycProvider());
                    assertEquals("AUXMONEY", response.getLoanProvider());
                });
    }

    @Test
    @DisplayName("Ident Creation fail")
    void identificationFailure() throws InterruptedException {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_2);
        InitiateIdentificationRequest requestBody = getInitiateIdentificationRequest();
        String applicationId = requestBody.getApplicationId();
        String loanOfferId = requestBody.getLoanOfferId();

        idNowowMockServerClient.fakeGetToken("joonkoswkauxmoneyesign");
        idNowowMockServerClient.fakeCreateIdentFailure(applicationId, "joonkoswkauxmoneyesign");
        loanDemandStore.setApplicationId(applicationId);
        mongoTemplate.insert(loanDemandStore);
        mongoTemplate.insert(getLoanOfferStore(applicationId, loanOfferId, USER_UUID_2));
        mongoTemplate.insert(getUserPersonalInformationStore(USER_UUID_2));

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

    @Disabled
    @Test
    @DisplayName("Contract upload fail")
    void contractUploadFailure() throws InterruptedException {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_1);
        InitiateIdentificationRequest requestBody = getInitiateIdentificationRequest();
        String applicationId = requestBody.getApplicationId();
        String loanOfferId = requestBody.getLoanOfferId();

        idNowowMockServerClient.fakeGetToken("joonkoswkauxmoneyesign");
        idNowowMockServerClient.fakeCreateIdent(applicationId, "joonkoswkauxmoneyesign");
        auxmoneyMockServerClient.fakeGetContract();
        auxmoneyMockServerClient.fakePushNotification();
        auxmoneyMockServerClient.fakeUploadContractFailure(applicationId);
        loanDemandStore.setApplicationId(applicationId);
        mongoTemplate.insert(loanDemandStore);
        mongoTemplate.insert(getLoanOfferStore(applicationId, loanOfferId, USER_UUID_3));
        mongoTemplate.insert(getUserPersonalInformationStore(USER_UUID_3));

        webClient.mutateWith(mockJwt().jwt(t -> t.subject(USER_UUID_3)))
                .post()
                .uri(URI.create("/api/v1/loan/identification/"))
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
        loanOfferStore.setKycUrl(null);
        loanOfferStore.setKycStatus(null);
        loanOfferStore.setContracts(null);
        loanOfferStore.setOffer(LoanOffer.builder()
                .loanProvider(new LoanProvider("Auxmoney"))
                .build());

        return loanOfferStore;
    }

    private void makeAssertionsForIdentificationAuditTrail(List<IdentificationAuditTrail> identificationAuditTrails) {
        Set<KycAuditStatus> statusSet = new HashSet<>() {{
            add(KYC_INITIATED);
            add(IDENT_CREATED);
            add(KYC_LINK_CREATED);
            add(PARTNER_NOTIFICATION_SENT);
            add(CONTRACT_UPLOADED);
        }};
        identificationAuditTrails.forEach(l ->
                assertTrue(statusSet.remove(KycAuditStatus.valueOf(l.getStatus())))
        );
        assertThat(statusSet).isEmpty();
    }

    private void makeAssertionsForIdentCreationFailFail(List<IdentificationAuditTrail> identificationAuditTrails) {
        Set<KycAuditStatus> statusSet = new HashSet<>() {{
            add(IDENT_CREATION_ERROR);
        }};
        identificationAuditTrails.forEach(l ->
                assertTrue(statusSet.remove(KycAuditStatus.valueOf(l.getStatus())))
        );
        assertThat(statusSet).isEmpty();
    }

    private void makeAssertionsForContractUploadFail(List<IdentificationAuditTrail> identificationAuditTrails) {
        Set<KycAuditStatus> statusSet = new HashSet<>() {{
            add(IDENT_CREATED);
            add(CONTRACT_UPLOAD_ERROR);
        }};
        identificationAuditTrails.forEach(l ->
                assertTrue(statusSet.remove(KycAuditStatus.valueOf(l.getStatus())))
        );
        assertThat(statusSet).isEmpty();
    }
}
