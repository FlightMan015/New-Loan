package de.joonko.loan.offer.status;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.offer.api.status.LoanStatusRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.util.Set;

import static de.joonko.loan.offer.testdata.LoanStatusTestData.*;

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureWebTestClient
class LoanStatusIT {

    private static final String LOAN_STATUS_URL = "/admin/v1/sync/offer/status";
    private static final String USER_USERNAME = "user123";
    private static final String USER_PASSWORD = "pass123";
    private static final String ADMIN_USERNAME = "admin123";
    private static final String ADMIN_PASSWORD = "pass123";

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void get401WhenMissingCredentials() {
        webClient
                .post()
                .uri(URI.create(LOAN_STATUS_URL))
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void get403WhenUserCredentials() {
        LoanStatusRequest req = LoanStatusRequest.builder().banks(Set.of(Bank.SANTANDER, Bank.CONSORS)).build();

        webClient
                .post()
                .uri(URI.create(LOAN_STATUS_URL))
                .headers(headers -> headers.setBasicAuth(USER_USERNAME, USER_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void get404WhenRequestedWithEmptyBanksSet() {
        LoanStatusRequest req = LoanStatusRequest.builder().banks(Set.of()).build();

        webClient
                .post()
                .uri(URI.create(LOAN_STATUS_URL))
                .headers(headers -> headers.setBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void get200WhenNoOffersToUpdate() {
        LoanStatusRequest req = LoanStatusRequest.builder().banks(Set.of(Bank.SANTANDER, Bank.CONSORS)).build();
        mongoTemplate.insertAll(getLoanOffersWithInvalidStatus());

        webClient
                .post()
                .uri(URI.create(LOAN_STATUS_URL))
                .headers(headers -> headers.setBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void get200WhenSyncingStatuses() {
        LoanStatusRequest req = LoanStatusRequest.builder().banks(Set.of(Bank.SANTANDER, Bank.CONSORS)).build();
        mongoTemplate.insertAll(getLoanOffersWithValidStatus());

        webClient
                .post()
                .uri(URI.create(LOAN_STATUS_URL))
                .headers(headers -> headers.setBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus()
                .isOk();
    }
}
