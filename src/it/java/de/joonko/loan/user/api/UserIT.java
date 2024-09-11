package de.joonko.loan.user.api;

import de.joonko.loan.messaging.config.KafkaTestConfig;
import de.joonko.loan.offer.api.model.UserPersonalDetails;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;

import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailNotVerifiedJwt;
import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailVerifiedJwt;
import static de.joonko.loan.user.testdata.UserTestData.getPersonalInformation;
import static de.joonko.loan.user.testdata.UserTestData.getUserPersonalDetails;
import static de.joonko.loan.user.testdata.UserTestData.getUserStatesStore;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@AutoConfigureWebTestClient(timeout = "36000")
class UserIT extends KafkaTestConfig {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String UPDATE_USER_URI = "/api/v1/user/";

    private static final String USER_UUID_1 = "35004d2f-ee8a-45fe-97e9-0542e1a0160a";
    private static final String USER_UUID_2 = "b16024cc-23a7-45c9-8aa8-57b5fc45bfcb";
    private static final String USER_UUID_3 = "8c54c636-4265-4f4b-a262-19f448508bc7";
    private static final String USER_UUID_4 = "f991114d-bcb6-40e3-a134-484b9c9389ec";

    @Test
    void getUnauthorizedWhenMissingJwtToken() {
        UserPersonalDetails userPersonalDetails = UserPersonalDetails.builder().build();

        webClient
                .put()
                .uri(URI.create(UPDATE_USER_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userPersonalDetails)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void getForbiddenWhenUserEmailNot() {
        Jwt jwt = mockEmailNotVerifiedJwt(USER_UUID_1);
        UserPersonalDetails userPersonalDetails = UserPersonalDetails.builder().build();

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .put()
                .uri(URI.create(UPDATE_USER_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userPersonalDetails)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void getValidationErrorWhenInvalidTaxId() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_1);
        UserPersonalDetails userPersonalDetails = getUserPersonalDetails();
        userPersonalDetails.getPersonalDetails().setTaxId("123");

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .put()
                .uri(URI.create(UPDATE_USER_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userPersonalDetails)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @Disabled
    void getNotFoundWhenMissingUserStates() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_1);
        UserPersonalDetails userPersonalDetails = getUserPersonalDetails();

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .put()
                .uri(URI.create(UPDATE_USER_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userPersonalDetails)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    @Disabled
    void getNotFoundWhenMissingUserPersonalInformation() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_2);
        UserPersonalDetails userPersonalDetails = getUserPersonalDetails();
        mongoTemplate.insert(getUserStatesStore(USER_UUID_2));

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .put()
                .uri(URI.create(UPDATE_USER_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userPersonalDetails)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getOkWhenValidUpdateNoTaxId() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_3);
        UserPersonalDetails userPersonalDetails = getUserPersonalDetails();
        mongoTemplate.insert(getUserStatesStore(USER_UUID_3));
        mongoTemplate.insert(getPersonalInformation(USER_UUID_3));

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .put()
                .uri(URI.create(UPDATE_USER_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userPersonalDetails)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void getOkWhenValidUpdateWithTaxId() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_4);
        UserPersonalDetails userPersonalDetails = getUserPersonalDetails();
        userPersonalDetails.getPersonalDetails().setTaxId("12345678901");
        mongoTemplate.insert(getUserStatesStore(USER_UUID_4));
        mongoTemplate.insert(getPersonalInformation(USER_UUID_4));

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .put()
                .uri(URI.create(UPDATE_USER_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userPersonalDetails)
                .exchange()
                .expectStatus()
                .isOk();
    }
}
