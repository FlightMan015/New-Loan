package de.joonko.loan.user.api;

import de.joonko.loan.user.api.testdata.UserDataITTestData;
import de.joonko.loan.userdata.api.model.UserDataRequest;
import de.joonko.loan.userdata.api.model.UserDataResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;

import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailNotVerifiedJwt;
import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailVerifiedJwt;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@AutoConfigureWebTestClient
@SpringBootTest
@ActiveProfiles("integration")
class UserDataIT {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WebTestClient webClient;

    private static final String USER_DATA_URI = "/api/v1/userdata";

    private static final String USER_UUID_1 = "936e0d09-cf8a-48d8-8587-e230c2e639d8";
    private static final String USER_UUID_2 = "1f8100a9-66c0-4bf5-8b50-845beb61ce10";
    private static final String USER_UUID_3 = "6b5bf99d-5f8d-456f-979e-321e2fd5f418";
    private static final String USER_UUID_4 = "bbf82291-60c0-471a-aa89-80eda6f7bc63";
    private static final String USER_UUID_5 = "f00c91a8-7161-47f8-9fc4-5ffd04f5190a";
    private static final String USER_UUID_6 = "a0e6fc19-50fb-40e5-88ca-e8400df25ca8";
    private static final String USER_UUID_7 = "a6fa45e7-c87d-4f03-99b3-f8bbd398ef44";

    private UserDataITTestData testData;

    @BeforeEach
    void setUp() {
        testData = new UserDataITTestData();
    }

    @Nested
    class GetRequests {
        @Test
        void getUnauthorizedWhenMissingJwtTokenForGet() {
            webClient
                    .get()
                    .uri(URI.create(USER_DATA_URI))
                    .exchange()
                    .expectStatus()
                    .isUnauthorized();
        }

        @Test
        void getNotFoundWhenUserMissing() {
            Jwt jwt = mockEmailVerifiedJwt(USER_UUID_4);

            webClient
                    .mutateWith(mockJwt().jwt(jwt))
                    .get()
                    .uri(URI.create(USER_DATA_URI))
                    .exchange()
                    .expectStatus()
                    .isNotFound();
        }

        @Test
        void getForbiddenWhenUserEmailNotFoundForGet() {
            Jwt jwt = mockEmailNotVerifiedJwt(USER_UUID_1);

            webClient
                    .mutateWith(mockJwt().jwt(jwt))
                    .get()
                    .uri(URI.create(USER_DATA_URI))
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }

        @Test
        void getOkWithMissingUserDataWhenGettingUserData() {
            Jwt jwt = mockEmailVerifiedJwt(USER_UUID_6);
            mongoTemplate.insert(testData.buildUserDraftInformationStore(USER_UUID_6));
            mongoTemplate.insert(testData.buildUserTransactionalDataStore(USER_UUID_6));

            webClient
                    .mutateWith(mockJwt().jwt(jwt))
                    .get()
                    .uri(URI.create(USER_DATA_URI))
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(UserDataResponse.class)
                    .value(response -> assertAll(
                            () -> assertTrue(response.getUserPersonal().isValid()),
                            () -> assertTrue(response.getUserContact().isValid()),
                            () -> assertFalse(response.getUserEmployment().isValid()),
                            () -> assertNull(response.getUserHousing()),
                            () -> assertNull(response.getUserCredit()),
                            () -> assertEquals("MUSTERMANN, HARTMUT", response.getUserAccount().getNameOnAccount()),
                            () -> assertEquals("DE****************5678", response.getUserAccount().getIban()),
                            () -> assertEquals("TESTDE8****", response.getUserAccount().getBic()),
                            () -> assertEquals("SANTANDER", response.getUserAccount().getBankName())
                    ));
        }

        @Test
        void getOkWithInvalidUserDataWhenGettingUserData() {
            Jwt jwt = mockEmailVerifiedJwt(USER_UUID_5);
            final var additionalInfo = testData.buildUserAdditionalInformationStore(USER_UUID_5);
            additionalInfo.getEmploymentDetails().setEmployerName(null);
            mongoTemplate.insert(additionalInfo);
            mongoTemplate.insert(testData.buildUserDraftInformationStore(USER_UUID_5));

            webClient
                    .mutateWith(mockJwt().jwt(jwt))
                    .get()
                    .uri(URI.create(USER_DATA_URI))
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(UserDataResponse.class)
                    .value(response -> assertAll(
                            () -> assertTrue(response.getUserPersonal().isValid()),
                            () -> assertTrue(response.getUserContact().isValid()),
                            () -> assertFalse(response.getUserEmployment().isValid()),
                            () -> assertNull(response.getUserHousing()),
                            () -> assertNull(response.getUserCredit()),
                            () -> assertNull(response.getUserAccount())
                    ));
        }

        @Test
        void getOkWithValidUserDataWhenGettingUserData() {
            Jwt jwt = mockEmailVerifiedJwt(USER_UUID_2);
            mongoTemplate.insert(testData.buildUserAdditionalInformationStore(USER_UUID_2));

            webClient
                    .mutateWith(mockJwt().jwt(jwt))
                    .get()
                    .uri(URI.create(USER_DATA_URI))
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(UserDataResponse.class)
                    .value(response -> assertAll(
                            () -> assertTrue(response.getUserPersonal().isValid()),
                            () -> assertTrue(response.getUserContact().isValid()),
                            () -> assertTrue(response.getUserEmployment().isValid()),
                            () -> assertTrue(response.getUserHousing().isValid()),
                            () -> assertTrue(response.getUserCredit().isValid())
                    ));
        }
    }

    @Nested
    class PutRequests {
        @Test
        void getUnauthorizedWhenMissingJwtTokenForPut() {
            webClient
                    .put()
                    .uri(URI.create(USER_DATA_URI))
                    .bodyValue(new UserDataRequest())
                    .exchange()
                    .expectStatus()
                    .isUnauthorized();
        }

        @Test
        void getForbiddenWhenUserEmailNotFoundForPut() {
            Jwt jwt = mockEmailNotVerifiedJwt(USER_UUID_1);

            webClient
                    .mutateWith(mockJwt().jwt(jwt))
                    .put()
                    .uri(URI.create(USER_DATA_URI))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new UserDataRequest())
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }

        @Test
        void getOkWhenUpdatingValidUserData() {
            Jwt jwt = mockEmailVerifiedJwt(USER_UUID_7);
            var userDataReq = testData.buildValidUserDataRequest();
            mongoTemplate.insert(testData.buildUserStatesStore(USER_UUID_7));

            webClient
                    .mutateWith(mockJwt().jwt(jwt))
                    .put()
                    .uri(URI.create(USER_DATA_URI))
                    .bodyValue(userDataReq)
                    .exchange()
                    .expectStatus()
                    .isNoContent();
        }

        @Test
        void getOkWhenUpdatingDraftUserData() {
            Jwt jwt = mockEmailVerifiedJwt(USER_UUID_3);

            webClient
                    .mutateWith(mockJwt().jwt(jwt))
                    .put()
                    .uri(URI.create(USER_DATA_URI))
                    .bodyValue(new UserDataRequest())
                    .exchange()
                    .expectStatus()
                    .isNoContent();
        }
    }
}
