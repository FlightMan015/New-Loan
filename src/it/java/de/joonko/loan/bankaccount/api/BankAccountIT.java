package de.joonko.loan.bankaccount.api;

import de.joonko.loan.bankaccount.testdata.BankAccountTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;

import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailNotVerifiedJwt;
import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailVerifiedJwt;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@AutoConfigureWebTestClient
@SpringBootTest
@ActiveProfiles("integration")
class BankAccountIT {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WebTestClient webClient;

    private static final String USER_BANK_ACCOUNT_URI = "/api/v1/user/bank-account";

    private static final String USER_UUID_1 = "261221f6-1286-4c6f-b74c-15f67b53b302";
    private static final String USER_UUID_2 = "b366d1e1-bb49-487b-9a5d-2073c318241f";
    private static final String USER_UUID_3 = "84eb26f7-6e8a-4dd5-823a-94a460f87e2c";
    private static final String USER_UUID_4 = "540721d6-8d21-413f-bbf0-c63d58687128";
    private static final String USER_UUID_5 = "b4112bf3-f56e-4441-914f-8a23566c799c";

    private BankAccountTestData testData;

    @BeforeEach
    void setUp() {
        testData = new BankAccountTestData();
    }

    @Test
    void getUnauthorizedWhenMissingJwtTokenForGet() {
        webClient
                .delete()
                .uri(URI.create(USER_BANK_ACCOUNT_URI))
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void getForbiddenWhenUserEmailNotFoundForGet() {
        Jwt jwt = mockEmailNotVerifiedJwt(USER_UUID_1);

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .delete()
                .uri(URI.create(USER_BANK_ACCOUNT_URI))
                .exchange()
                .expectStatus()
                .isForbidden();
    }
    
    @Test
    void getNotFoundWhenUserMissing() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_2);

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .delete()
                .uri(URI.create(USER_BANK_ACCOUNT_URI))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getNotFoundWhenUserTransactionalStateMissing() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_3);
        mongoTemplate.insert(testData.getUserStatesStoreWithoutTransactionalState(USER_UUID_3));

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .delete()
                .uri(URI.create(USER_BANK_ACCOUNT_URI))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void get204WhenOnlyUserTransactionalStateExists() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_4);
        mongoTemplate.insert(testData.getUserStatesStore(USER_UUID_4));

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .delete()
                .uri(URI.create(USER_BANK_ACCOUNT_URI))
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void get204WhenUserTransactionalDataAndStateExists() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_5);
        mongoTemplate.insert(testData.getUserStatesStore(USER_UUID_5));
        mongoTemplate.insert(testData.getUserTransactionalDataStore(USER_UUID_5));

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .delete()
                .uri(URI.create(USER_BANK_ACCOUNT_URI))
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}
