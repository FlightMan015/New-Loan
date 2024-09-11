package de.joonko.loan.identification;

import com.github.tomakehurst.wiremock.WireMockServer;

import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.GetIdentStatusResponse;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.model.idnow.Result;
import de.joonko.loan.user.service.UserPersonalInformationStore;

import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.reactive.server.WebTestClient;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;

import static de.joonko.loan.identification.testdata.IdentificationStatusITTestData.fakeGetStatus;
import static de.joonko.loan.identification.testdata.IdentificationStatusITTestData.fakeGetToken;
import static de.joonko.loan.identification.testdata.IdentificationStatusITTestData.fakeNotFoundWhenGettingIdent;
import static de.joonko.loan.identification.testdata.IdentificationStatusITTestData.getEditedLoanDemandStore;
import static de.joonko.loan.identification.testdata.IdentificationStatusITTestData.getIdentificationLink;
import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailVerifiedJwt;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@AutoConfigureWebTestClient
@SpringBootTest
@ContextConfiguration(initializers = WireMockInitializer.class)
@ActiveProfiles("integration")
@ExtendWith(RandomBeansExtension.class)
class IdentificationStatusControllerIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private WireMockServer mockServer;

    @Random
    LoanDemandStore loanDemandStore;

    @Random
    LoanOfferStore loanOfferStore;

    private static final String APPLICATION_ID = "5f561054e3fa3d3d1a7ef3f4";
    private static final String APPLICATION_ID_2 = "5f561054e3fa3d3d1a7ef3f5";
    private static final String EXTERNAL_IDENT_ID = "60420678";
    private static final String IDENTIFICATION_STATUS_URL = "/api/v1/loan/identification/status";
    private static final String USER_UUID_1 = "ba097a01-f9e0-4741-a69e-1f416e06abd1";

    @BeforeEach
    void clearMockServerExpectations() {
        mockServer.resetAll();
    }

    @Test
    @DisplayName("Should Get SUCCESS_DATA_CHANGED Identification status")
    void getIdentificationStatus() {
        // given
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_1);
        mongoTemplate.insert(getIdentificationLink(APPLICATION_ID, EXTERNAL_IDENT_ID, "5f5798975940571ab99d3e4b", Bank.AION));
        mongoTemplate.insert(getEditedLoanDemandStore(loanDemandStore, APPLICATION_ID));
        loanOfferStore.setLoanOfferId("5f5798975940571ab99d3e4b");
        loanOfferStore.setUserUUID("123");
        UserPersonalInformationStore userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setUserUUID("123");
        userPersonalInformationStore.setFirstName("Mahmoud");
        userPersonalInformationStore.setLastName("Mohamed");
        mongoTemplate.insert(userPersonalInformationStore);
        mongoTemplate.insert(loanOfferStore);
        fakeGetToken(mockServer, "aionAccountId");
        fakeGetStatus(mockServer, "aionAccountId", EXTERNAL_IDENT_ID);

        // when
        // then
        webTestClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri
                        .path(IDENTIFICATION_STATUS_URL)
                        .queryParam("externalIdentId", EXTERNAL_IDENT_ID)
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(GetIdentStatusResponse.class)
                .value(response ->
                        assertEquals(Result.SUCCESS_DATA_CHANGED.name(), response.getStatus())
                );
    }

    @Test
    @DisplayName("Should Get CANCELLED Identification status")
    void getCancelledIdentificationStatus() {
        // given
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_1);
        mongoTemplate.insert(getIdentificationLink(APPLICATION_ID_2, EXTERNAL_IDENT_ID, "5f5798975940571ab99d3e4c", Bank.AION));
        mongoTemplate.insert(getEditedLoanDemandStore(loanDemandStore, APPLICATION_ID_2));
        loanOfferStore.setLoanOfferId("5f5798975940571ab99d3e4c");
        loanOfferStore.setUserUUID("1235");
        UserPersonalInformationStore userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setUserUUID("1235");
        userPersonalInformationStore.setFirstName("Mahmoud");
        userPersonalInformationStore.setLastName("Mohamed");
        mongoTemplate.insert(userPersonalInformationStore);
        mongoTemplate.insert(loanOfferStore);
        fakeGetToken(mockServer, "aionAccountId");
        fakeNotFoundWhenGettingIdent(mockServer, "aionAccountId", EXTERNAL_IDENT_ID);

        // when
        // then
        webTestClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri
                        .path(IDENTIFICATION_STATUS_URL)
                        .queryParam("externalIdentId", EXTERNAL_IDENT_ID)
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(GetIdentStatusResponse.class)
                .value(response -> assertAll(
                        () -> assertEquals("CANCELLED", response.getStatus()),
                        () -> assertEquals("Mahmoud", response.getFirstName()),
                        () -> assertEquals("AION", response.getLoanProvider()),
                        () -> assertEquals(IdentificationProvider.ID_NOW, response.getKycProvider())
                ));
    }

    @Test
    @DisplayName("Should Get Not Found when externalIdentId does not exist")
    void getNotFoundIdentificationStatus() {
        // given
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_1);

        // when
        // then
        webTestClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri
                        .path(IDENTIFICATION_STATUS_URL)
                        .queryParam("externalIdentId", EXTERNAL_IDENT_ID)
                        .build())
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}
