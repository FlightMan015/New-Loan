package de.joonko.loan.user.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;

@AutoConfigureWebTestClient
@SpringBootTest
@ActiveProfiles("integration")
class UsersFtsIT {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WebTestClient webClient;

    private static final String USERS_FTS_URI = "/admin/v1/user/fts";
    private static final String USER_USERNAME = "user123";
    private static final String USER_PASSWORD = "pass123";
    private static final String ADMIN_USERNAME = "admin123";
    private static final String ADMIN_PASSWORD = "pass123";

    @Test
    void get401WhenMissingCredentials() {
        webClient
                .delete()
                .uri(URI.create(USERS_FTS_URI))
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void get403WhenInvalidCredentials() {
        webClient
                .delete()
                .uri(URI.create(USERS_FTS_URI))
                .headers(headers -> headers.setBasicAuth(USER_USERNAME, USER_PASSWORD))
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void get200() {
        webClient
                .delete()
                .uri(URI.create(USERS_FTS_URI))
                .headers(headers -> headers.setBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD))
                .exchange()
                .expectStatus()
                .isOk();
    }
}
