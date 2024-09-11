package de.joonko.loan.identification;

import com.github.tomakehurst.wiremock.WireMockServer;

import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.model.InitiateIdentificationRequest;
import de.joonko.loan.identification.service.idnow.testdata.IdNowClientApiMocks;
import de.joonko.loan.identification.testdata.IdentificationControllerAionITTestData;
import de.joonko.loan.partner.aion.AionClientMocks;
import de.joonko.loan.partner.aion.model.CreditApplicationResponseStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.SneakyThrows;

import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailVerifiedJwt;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@AutoConfigureWebTestClient(timeout = "36000")
@SpringBootTest
@ContextConfiguration(initializers = WireMockInitializer.class)
@ActiveProfiles("integration")
@ExtendWith(RandomBeansExtension.class)
class IdentificationControllerAionIT {

    @Autowired
    private WebTestClient webClient;
    @Autowired
    private WireMockServer mockServer;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String IDENTIFICATION_URL = "/api/v1/loan/identification";

    private IdentificationControllerAionITTestData testData;

    private AionClientMocks aionClientMocks;
    private IdNowClientApiMocks idNowClientApiMocks;

    private static final String USER_UUID_1 = "baa9815a-f8ba-43f2-a63d-1de2cbbdd2b1";
    private static final String USER_UUID_2 = "25c48712-a863-4750-a86d-a8a23d750fca";

    @Random
    private LoanOfferStore loanOfferStore;
    @Random
    private CreditApplicationResponseStore creditApplicationResponseStore;

    @BeforeEach
    void setUp() {
        testData = new IdentificationControllerAionITTestData();
        idNowClientApiMocks = new IdNowClientApiMocks(mockServer);
        aionClientMocks = new AionClientMocks(mockServer);

        mockServer.resetAll();
    }

    @Disabled
    @SneakyThrows
    @Test
    void createIdentificationUrl() {
        // given
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_1);
        final var applicationId = "7hf29fh82389";
        final var loanOfferId = "fj8293f83h2f9";
        final var authToken = "token";
        final var request = InitiateIdentificationRequest.builder()
                .applicationId(applicationId)
                .loanOfferId(loanOfferId)
                .build();

        mongoTemplate.insert(testData.getEditedLoanOfferStore(loanOfferStore, applicationId, loanOfferId, USER_UUID_1));
        mongoTemplate.insert(testData.getUserPersonalInformationStore(USER_UUID_1));
        mongoTemplate.insert(testData.getUserAdditionalInformationStore(USER_UUID_1));
        mongoTemplate.insert(testData.getEditedCreditApplicationResponseStore(creditApplicationResponseStore, applicationId));

        aionClientMocks.fake200WhenAuth();
        aionClientMocks.fake200WhenGettingOfferStatus(authToken, creditApplicationResponseStore.getProcessId());
        idNowClientApiMocks.fake200WhenLoginToAccount("aionAccountId", "aionApiKey");
        idNowClientApiMocks.fake201WhenCreatingIdent("aionAccountId", authToken, applicationId, testData.getCreateIdentRequest());
        idNowClientApiMocks.fake200WhenUploadingDocument("aionAccountId", authToken, applicationId, testData.getDocument("agreement", "agreementbase64"));
        idNowClientApiMocks.fake200WhenUploadingDocument("aionAccountId", authToken, applicationId, testData.getDocument("schedule", "schedulebase64"));
        idNowClientApiMocks.fake200WhenUploadingDocument("aionAccountId", authToken, applicationId, testData.getDocument("secci", "seccibase64"));


        // when
        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .post()
                .uri(URI.create(IDENTIFICATION_URL))
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.kycUrl").isEqualTo("http://localhost/TST-FXWF")
                .jsonPath("$.kycProvider").isEqualTo(IdentificationProvider.ID_NOW);
    }

    @SneakyThrows
    @Test
    void contractUploadFailure() {
        // given
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_2);
        final var applicationId = "7hf29fh823893";
        final var loanOfferId = "fj8293f83h2f91";
        final var authToken = "token";
        final var request = InitiateIdentificationRequest.builder()
                .applicationId(applicationId)
                .loanOfferId(loanOfferId)
                .build();

        mongoTemplate.insert(testData.getEditedLoanOfferStore(loanOfferStore, applicationId, loanOfferId, USER_UUID_2));
        mongoTemplate.insert(testData.getUserPersonalInformationStore(USER_UUID_2));
        mongoTemplate.insert(testData.getUserAdditionalInformationStore(USER_UUID_2));
        mongoTemplate.insert(testData.getEditedCreditApplicationResponseStore(creditApplicationResponseStore, applicationId));

        aionClientMocks.fake200WhenAuth();
        aionClientMocks.fake200WhenGettingOfferStatus(authToken, creditApplicationResponseStore.getProcessId());
        idNowClientApiMocks.fake200WhenLoginToAccount("aionAccountId", "aionApiKey");
        idNowClientApiMocks.fake201WhenCreatingIdent("aionAccountId", authToken, applicationId, testData.getCreateIdentRequest());
        idNowClientApiMocks.fake200WhenUploadingDocument("aionAccountId", authToken, applicationId, testData.getDocument("agreement", "agreementbase64"));
        idNowClientApiMocks.fake200WhenUploadingDocument("aionAccountId", authToken, applicationId, testData.getDocument("schedule", "schedulebase64"));
        idNowClientApiMocks.fake401WhenUploadingDocument("aionAccountId", authToken, applicationId, testData.getDocument("secci", "seccibase64"));


        // when
        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .post()
                .uri(URI.create(IDENTIFICATION_URL))
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }
}
