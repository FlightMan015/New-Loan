package de.joonko.loan.identification;

import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.CreateIdentResponse;
import de.joonko.loan.identification.model.GetIdentStatusResponse;
import de.joonko.loan.identification.model.GetKycUrlResponse;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.model.InitiateIdentificationRequest;
import de.joonko.loan.identification.testdata.IdentificationControllerPostbankITTestData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;

import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailVerifiedJwt;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@AutoConfigureWebTestClient(timeout = "36000")
@SpringBootTest
@ContextConfiguration(initializers = WireMockInitializer.class)
@ActiveProfiles("integration")
@ExtendWith(RandomBeansExtension.class)
class IdentificationControllerPostbankIT {

    @Autowired
    private WebTestClient webClient;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String IDENTIFICATION_URI = "/api/v1/loan/identification";
    private static final String IDENTIFICATION_STATUS_URI = "/api/v1/loan/identification/status";
    private static final String IDENTIFICATION_URL_URI = "/api/v1/loan/identification/url";

    private IdentificationControllerPostbankITTestData testData;

    private static final String USER_UUID_1 = "baa9815a-f8ba-43f2-a63d-1de2cbbdd2b3";
    private static final String USER_UUID_2 = "baa9815a-f8ba-43f2-a63d-1de2cbbdd2b2";

    @Random
    private LoanOfferStore loanOfferStore;

    @BeforeEach
    void setUp() {
        testData = new IdentificationControllerPostbankITTestData();
    }

    @Test
    void createIdentificationUrl() {
        // given
        final var jwt = mockEmailVerifiedJwt(USER_UUID_1);
        final var applicationId = "7hf29fh823896";
        final var loanOfferId = "fj8293f83h2f94";
        final var kycUrl = "https://test.webid-solutions.de/service/index/ti/123/cn/421/act/pass";
        final var request = InitiateIdentificationRequest.builder()
                .applicationId(applicationId)
                .loanOfferId(loanOfferId)
                .build();

        mongoTemplate.insert(testData.getEditedLoanOfferStore(loanOfferStore, applicationId, loanOfferId, USER_UUID_1));
        mongoTemplate.insert(testData.getUserPersonalInformationStore(USER_UUID_1));
        mongoTemplate.insert(testData.getUserAdditionalInformationStore(USER_UUID_1));
        mongoTemplate.insert(testData.getPostbankLoanDemandStore(applicationId, kycUrl));

        // when
        // then
        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .post()
                .uri(URI.create(IDENTIFICATION_URI))
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CreateIdentResponse.class)
                .value(response -> assertAll(
                        () -> assertEquals(kycUrl, response.getKycUrl()),
                        () -> assertEquals(IdentificationProvider.WEB_ID, response.getKycProvider())
                ));
    }

    @Test
    void getIdentificationUrl() {
        // given
        final var jwt = mockEmailVerifiedJwt(USER_UUID_1);
        final var externalIdentId = "3hr2938rh";
        final var kycUrl = "https://test.webid-solutions.de/service/index/ti/123/cn/421/act/pass";
        mongoTemplate.insert(testData.getIdentificationLink(externalIdentId, "3j8f923hf", kycUrl));

        // when
        // then
        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri
                        .path(IDENTIFICATION_URL_URI)
                        .queryParam("externalIdentId", externalIdentId)
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(GetKycUrlResponse.class)
                .value(response -> assertAll(
                        () -> assertEquals(kycUrl, response.getKycUrl()),
                        () -> assertEquals("POSTBANK", response.getLoanProvider()),
                        () -> assertEquals(IdentificationProvider.WEB_ID, response.getKycProvider())
                ));
    }

    @Test
    void getIdentificationStatus() {
        // given
        final var jwt = mockEmailVerifiedJwt(USER_UUID_2);
        final var externalIdentId = "3hr2938rh11";
        final var kycUrl = "https://test.webid-solutions.de/service/index/ti/123/cn/421/act/pass";
        final var loanOfferId = "3j8f923hf14";

        mongoTemplate.insert(testData.getIdentificationLink(externalIdentId, loanOfferId, kycUrl));
        mongoTemplate.insert(testData.getCancelledKycLoanOfferStore(loanOfferStore, externalIdentId, loanOfferId, USER_UUID_2));
        mongoTemplate.insert(testData.getUserPersonalInformationStore(USER_UUID_2));

        // when
        // then
        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri
                        .path(IDENTIFICATION_STATUS_URI)
                        .queryParam("externalIdentId", externalIdentId)
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(GetIdentStatusResponse.class)
                .value(response -> assertAll(
                        () -> assertEquals("CANCELLED", response.getStatus()),
                        () -> assertEquals("John", response.getFirstName()),
                        () -> assertEquals("POSTBANK", response.getLoanProvider()),
                        () -> assertEquals(IdentificationProvider.WEB_ID, response.getKycProvider())
                ));
    }
}
