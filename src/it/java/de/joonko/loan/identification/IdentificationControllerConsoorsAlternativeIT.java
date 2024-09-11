package de.joonko.loan.identification;

import com.github.tomakehurst.wiremock.WireMockServer;

import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.CreateIdentResponse;
import de.joonko.loan.identification.model.GetIdentStatusResponse;
import de.joonko.loan.identification.model.GetKycUrlResponse;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.testdata.IdentificationControllerConsorsITTestData;
import de.joonko.loan.partner.consors.ConsorsPropertiesConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.util.UUID;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;

import static de.joonko.loan.identification.IdentificationFixture.getInitiateIdentificationRequest;
import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailVerifiedJwt;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@AutoConfigureWebTestClient(timeout = "36000")
@SpringBootTest
@ContextConfiguration(initializers = WireMockInitializer.class)
@ActiveProfiles("integration")
@ExtendWith(RandomBeansExtension.class)
class IdentificationControllerConsoorsAlternativeIT {

    @Autowired
    private WebTestClient webClient;
    @Autowired
    private WireMockServer mockServer;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ConsorsPropertiesConfig consorsPropertiesConfig;

    private ConsorsMockServerClient consorsMockServerClient;
    private static final String IDENTIFICATION_URI = "/api/v1/loan/identification";
    private static final String IDENTIFICATION_STATUS_URI = "/api/v1/loan/identification/status";
    private static final String IDENTIFICATION_URL_URI = "/api/v1/loan/identification/url";

    private IdentificationControllerConsorsITTestData testData;

    private static final String USER_UUID_1 = "75bf1281-79ce-3d5c-8862-f954807cf10a";
    private static final String USER_UUID_2 = "75bf1281-79ce-3d5c-8862-f954807cf10b";

    @Random
    private LoanOfferStore loanOfferStore;

    @BeforeEach
    void setUp() {
        testData = new IdentificationControllerConsorsITTestData();
        consorsMockServerClient = new ConsorsMockServerClient(mockServer);
        ReflectionTestUtils.setField(consorsPropertiesConfig, "webidEnabled", true);
    }

    @Disabled
    @Test
    void createIdentificationUrl() {
        // given
        final var jwt = mockEmailVerifiedJwt(USER_UUID_1);
        final var kycUrl = "https://test.webid-solutions.de/service/index/ti/123/cn/421/act/pass";
        final var request = getInitiateIdentificationRequest();
        final var applicationId = request.getApplicationId();
        final var loanOfferId = request.getLoanOfferId();

        mongoTemplate.insert(testData.getEditedLoanOfferStore(loanOfferStore, applicationId, loanOfferId, USER_UUID_1));
        mongoTemplate.insert(testData.getUserPersonalInformationStore(USER_UUID_1));
        mongoTemplate.insert(testData.getUserAdditionalInformationStore(USER_UUID_1));
        mongoTemplate.insert(testData.getConsorsAcceptedOfferStore(applicationId, kycUrl));

        // when
        consorsMockServerClient.fakeGetContract();
        consorsMockServerClient.fakeGetToken();
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
        final var externalIdentId = UUID.randomUUID().toString();
        final var loanOfferId = UUID.randomUUID().toString();

        final var kycUrl = "https://test.webid-solutions.de/service/index/ti/123/cn/421/act/pass";
        mongoTemplate.insert(testData.getIdentificationLink(externalIdentId, loanOfferId, kycUrl));

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
                        () -> assertEquals("Consors Finanz", response.getLoanProvider()),
                        () -> assertEquals(IdentificationProvider.WEB_ID, response.getKycProvider())
                ));
    }

    @Test
    void getIdentificationStatus() {
        // given
        final var jwt = mockEmailVerifiedJwt(USER_UUID_2);
        final var externalIdentId = UUID.randomUUID().toString();
        final var kycUrl = "https://test.webid-solutions.de/service/index/ti/123/cn/421/act/pass";
        final var loanOfferId = UUID.randomUUID().toString();

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
                        () -> assertEquals("Consors Finanz", response.getLoanProvider()),
                        () -> assertEquals(IdentificationProvider.WEB_ID, response.getKycProvider())
                ));
    }
}
