package de.joonko.loan.reporting.api;

import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.reporting.api.model.GetOffersStatusResponse;
import de.joonko.loan.user.states.UserStatesStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static de.joonko.loan.reporting.api.testdata.OfferStatusTestData.buildLoanOfferStore;
import static de.joonko.loan.reporting.api.testdata.OfferStatusTestData.buildUserStatesStore;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureWebTestClient
@ActiveProfiles("integration")
@SpringBootTest
class OfferStatusIT {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String REPORTING_USERNAME = "reporting123";
    private static final String REPORTING_PASSWORD = "reporting123";
    private static final String POSTBANK_USERNAME = "pb123";
    private static final String POSTBANK_PASSWORD = "pb123";

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(LoanOfferStore.class);
        mongoTemplate.dropCollection(UserStatesStore.class);
    }

    @Test
    void get401WhenMissingCredentials() {
        webClient
                .get()
                .uri(uri -> uri
                        .pathSegment("loan")
                        .pathSegment("distribution-channel")
                        .pathSegment(UUID.randomUUID().toString())
                        .pathSegment("status")
                        .queryParam("start-date", "2022-03-01")
                        .queryParam("end-date", "2022-04-01")
                        .build())
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void get403WhenPostbankCredentials() {
        webClient
                .get()
                .uri(uri -> uri
                        .pathSegment("loan")
                        .pathSegment("distribution-channel")
                        .pathSegment(UUID.randomUUID().toString())
                        .pathSegment("status")
                        .queryParam("start-date", "2022-03-01")
                        .queryParam("end-date", "2022-04-01")
                        .build())
                .headers(headers -> headers.setBasicAuth(POSTBANK_USERNAME, POSTBANK_PASSWORD))
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void get200WhenGettingOffersStatusesWithoutResponseFormat() {
        final var tenantId = UUID.randomUUID().toString();

        webClient
                .get()
                .uri(uri -> uri
                        .pathSegment("loan")
                        .pathSegment("distribution-channel")
                        .pathSegment(tenantId)
                        .pathSegment("status")
                        .queryParam("start-date", "2022-03-01")
                        .queryParam("end-date", "2022-04-01")
                        .build())
                .headers(headers -> headers.setBasicAuth(REPORTING_USERNAME, REPORTING_PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void get400WhenGettingOffersStatusesWithInvalidResponseFormat() {
        final var tenantId = UUID.randomUUID().toString();

        webClient
                .get()
                .uri(uri -> uri
                        .pathSegment("loan")
                        .pathSegment("distribution-channel")
                        .pathSegment(tenantId)
                        .pathSegment("status")
                        .queryParam("start-date", "2022-03-01")
                        .queryParam("end-date", "2022-04-01")
                        .queryParam("response-format", "jjsson")
                        .build())
                .headers(headers -> headers.setBasicAuth(REPORTING_USERNAME, REPORTING_PASSWORD))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void get400WhenGettingOffersStatusesWithEndDateBeforeStarDate() {
        final var tenantId = UUID.randomUUID().toString();

        webClient
                .get()
                .uri(uri -> uri
                        .pathSegment("loan")
                        .pathSegment("distribution-channel")
                        .pathSegment(tenantId)
                        .pathSegment("status")
                        .queryParam("start-date", "2022-04-01")
                        .queryParam("end-date", "2022-03-01")
                        .queryParam("response-format", "json")
                        .build())
                .headers(headers -> headers.setBasicAuth(REPORTING_USERNAME, REPORTING_PASSWORD))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .value(body ->
                        assertEquals("Required request parameter 'start-date' has to be before 'end-date'", body)
                );
    }

    @Test
    void get400WhenGettingOffersStatusesWithTimeDurationBiggerThan3MonthsLimit() {
        final var tenantId = UUID.randomUUID().toString();

        webClient
                .get()
                .uri(uri -> uri
                        .pathSegment("loan")
                        .pathSegment("distribution-channel")
                        .pathSegment(tenantId)
                        .pathSegment("status")
                        .queryParam("start-date", "2022-01-01")
                        .queryParam("end-date", "2022-04-01")
                        .queryParam("response-format", "json")
                        .build())
                .headers(headers -> headers.setBasicAuth(REPORTING_USERNAME, REPORTING_PASSWORD))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .value(body ->
                        assertEquals("Max duration between request parameters 'start-date' and 'end-date' has to be less than 3 months", body)
                );
    }

    @Test
    void get200WhenGettingOffersStatusesWithEndDateEqualsStarDate() {
        final var tenantId = UUID.randomUUID().toString();

        webClient
                .get()
                .uri(uri -> uri
                        .pathSegment("loan")
                        .pathSegment("distribution-channel")
                        .pathSegment(tenantId)
                        .pathSegment("status")
                        .queryParam("start-date", "2022-04-01")
                        .queryParam("end-date", "2022-04-01")
                        .queryParam("response-format", "json")
                        .build())
                .headers(headers -> headers.setBasicAuth(REPORTING_USERNAME, REPORTING_PASSWORD))
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void get200WhenGettingOffersStatusesWithJsonFormatWhenOffersNotFound() {
        // given
        final var tenantId = UUID.randomUUID();
        final var endDateTime = OffsetDateTime.now();
        final var startDateTime = endDateTime.minusMonths(3).plusDays(1);

        mongoTemplate.insertAll(List.of(
                buildUserStatesStore(tenantId, endDateTime.minusMonths(2), endDateTime.minusMonths(4)),
                buildUserStatesStore(tenantId, endDateTime.minusMonths(1)),

                buildUserStatesStore(tenantId, endDateTime.minusMonths(5), endDateTime.minusMonths(4)),
                buildUserStatesStore(tenantId, endDateTime.minusMonths(6)),
                buildUserStatesStore(tenantId),
                buildUserStatesStore(UUID.fromString("3998e936-6943-41f9-bdf2-db9f9ba3d5b5"), endDateTime.minusMonths(2), endDateTime.minusMonths(4)),
                buildUserStatesStore(UUID.fromString("5f3e8670-42d6-41d8-980d-ee3cfbbad5ca"), endDateTime.minusMonths(1)),
                buildUserStatesStore(UUID.fromString("e37647af-9171-466e-b533-08daad956aa3")),
                buildUserStatesStore(UUID.fromString("5bc6b355-8eb5-4b94-a313-ed24de494e8a"), endDateTime.minusMonths(5)),
                buildUserStatesStore(UUID.fromString("7c560aca-6a76-45cf-acba-782a40a131dc"), endDateTime.minusMonths(5), endDateTime.minusMonths(7))
        ));

        // when
        // then
        webClient
                .get()
                .uri(uri -> uri
                        .pathSegment("loan")
                        .pathSegment("distribution-channel")
                        .pathSegment(tenantId.toString())
                        .pathSegment("status")
                        .queryParam("start-date", startDateTime.toLocalDate())
                        .queryParam("end-date", endDateTime.toLocalDate())
                        .queryParam("response-format", "json")
                        .build())
                .headers(headers -> headers.setBasicAuth(REPORTING_USERNAME, REPORTING_PASSWORD))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(GetOffersStatusResponse.class)
                .value(response ->
                        assertAll(
                                () -> assertEquals(2, response.getOffers().size()),
                                () -> assertTrue(response.getOffers().get(0).getLoanAmountRequestedAt().isAfter(response.getOffers().get(1).getLoanAmountRequestedAt())),
                                () -> assertNotNull(response.getOffers().get(0).getUserUUID()),
                                () -> assertEquals(tenantId.toString(), response.getOffers().get(0).getDistributionChannelUUID()),

                                () -> assertNotNull(response.getOffers().get(0).getBankAccountAddedAt()),
                                () -> assertNotNull(response.getOffers().get(0).getPersonalDataAddedAt()),
                                () -> assertNotNull(response.getOffers().get(0).getPurpose()),
                                () -> assertNotNull(response.getOffers().get(0).getLoanAmountRequested()),
                                () -> assertNotNull(response.getOffers().get(0).getLoanAmountRequestedAt()),

                                () -> assertNull(response.getOffers().get(0).getOfferProvider()),
                                () -> assertNull(response.getOffers().get(0).getOffersReceivedAt()),
                                () -> assertNull(response.getOffers().get(0).getOfferAcceptedAt()),
                                () -> assertNull(response.getOffers().get(0).getKycStatus()),
                                () -> assertNull(response.getOffers().get(0).getKycStatusLastUpdatedAt()),
                                () -> assertNull(response.getOffers().get(0).getOfferStatus()),
                                () -> assertNull(response.getOffers().get(0).getOfferStatusLastUpdatedAt())
                        )
                );
    }

    @Test
    void get200WhenGettingOffersStatusesWithJsonFormat() {
        // given
        final var tenantId = UUID.randomUUID();
        final var endDateTime = OffsetDateTime.now();
        final var startDateTime = endDateTime.minusMonths(3).plusDays(1);

        mongoTemplate.insertAll(List.of(
                buildUserStatesStore(tenantId, endDateTime.minusMonths(2), endDateTime.minusMonths(4)),
                buildUserStatesStore(tenantId, endDateTime.minusMonths(1)),
                buildUserStatesStore(tenantId, endDateTime.minusDays(20), endDateTime.minusDays(4), endDateTime.minusDays(50), endDateTime.minusMonths(12)),

                buildUserStatesStore(tenantId, endDateTime.minusMonths(5), endDateTime.minusMonths(4)),
                buildUserStatesStore(tenantId, endDateTime.minusMonths(6)),
                buildUserStatesStore(tenantId),
                buildUserStatesStore(UUID.fromString("3998e936-6943-41f9-bdf2-db9f9ba3d5b5"), endDateTime.minusMonths(2), endDateTime.minusMonths(4)),
                buildUserStatesStore(UUID.fromString("5f3e8670-42d6-41d8-980d-ee3cfbbad5ca"), endDateTime.minusMonths(1)),
                buildUserStatesStore(UUID.fromString("e37647af-9171-466e-b533-08daad956aa3")),
                buildUserStatesStore(UUID.fromString("5bc6b355-8eb5-4b94-a313-ed24de494e8a"), endDateTime.minusMonths(5)),
                buildUserStatesStore(UUID.fromString("7c560aca-6a76-45cf-acba-782a40a131dc"), endDateTime.minusMonths(5), endDateTime.minusMonths(7))
        ));
        mongoTemplate.insertAll(List.of(
                buildLoanOfferStore(tenantId.toString() + endDateTime.minusMonths(2), OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),
                buildLoanOfferStore(tenantId.toString() + endDateTime.minusMonths(2), OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),

                buildLoanOfferStore(tenantId.toString() + endDateTime.minusDays(4), OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),
                buildLoanOfferStore(tenantId.toString() + endDateTime.minusDays(4), OffsetDateTime.now(), OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), LocalDateTime.now()),

                buildLoanOfferStore(tenantId.toString() + endDateTime.minusMonths(1), null, null, OffsetDateTime.now().minusDays(1), LocalDateTime.now()),
                buildLoanOfferStore(tenantId.toString() + endDateTime.minusMonths(1), null, null, OffsetDateTime.now(), LocalDateTime.now()),

                buildLoanOfferStore(tenantId.toString() + endDateTime.minusDays(20), null, OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),
                buildLoanOfferStore(tenantId.toString() + endDateTime.minusDays(20), null, OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),

                buildLoanOfferStore(tenantId.toString() + endDateTime.minusMonths(12), OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),
                buildLoanOfferStore(tenantId.toString() + endDateTime.minusMonths(12), OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),
                buildLoanOfferStore(tenantId.toString() + endDateTime.minusMonths(12), OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),

                buildLoanOfferStore("3998e936-6943-41f9-bdf2-db9f9ba3d5b5" + endDateTime.minusMonths(2), OffsetDateTime.now().minusDays(3), OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),
                buildLoanOfferStore("3998e936-6943-41f9-bdf2-db9f9ba3d5b5" + endDateTime.minusMonths(2), OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now())
        ));

        // when
        // then
        webClient
                .get()
                .uri(uri -> uri
                        .pathSegment("loan")
                        .pathSegment("distribution-channel")
                        .pathSegment(tenantId.toString())
                        .pathSegment("status")
                        .queryParam("start-date", startDateTime.toLocalDate())
                        .queryParam("end-date", endDateTime.toLocalDate())
                        .queryParam("response-format", "json")
                        .build())
                .headers(headers -> headers.setBasicAuth(REPORTING_USERNAME, REPORTING_PASSWORD))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(GetOffersStatusResponse.class)
                .value(response ->
                        assertAll(
                                () -> assertEquals(5, response.getOffers().size()),
                                () -> assertTrue(response.getOffers().get(0).getLoanAmountRequestedAt().isAfter(response.getOffers().get(1).getLoanAmountRequestedAt())),
                                () -> assertNotNull(response.getOffers().get(0).getUserUUID()),
                                () -> assertEquals(tenantId.toString(), response.getOffers().get(0).getDistributionChannelUUID()),

                                () -> assertNotNull(response.getOffers().get(0).getBankAccountAddedAt()),
                                () -> assertNotNull(response.getOffers().get(0).getPersonalDataAddedAt()),
                                () -> assertNotNull(response.getOffers().get(0).getPurpose()),
                                () -> assertNotNull(response.getOffers().get(0).getLoanAmountRequested()),
                                () -> assertNotNull(response.getOffers().get(0).getLoanAmountRequestedAt()),

                                () -> assertNotNull(response.getOffers().get(0).getOfferProvider()),
                                () -> assertNotNull(response.getOffers().get(0).getOffersReceivedAt()),
                                () -> assertNotNull(response.getOffers().get(0).getOfferAcceptedAt()),
                                () -> assertNotNull(response.getOffers().get(0).getKycStatus()),
                                () -> assertNotNull(response.getOffers().get(0).getKycStatusLastUpdatedAt()),
                                () -> assertNotNull(response.getOffers().get(0).getOfferStatus()),
                                () -> assertNotNull(response.getOffers().get(0).getOfferStatusLastUpdatedAt())
                        )
                );
    }

    @Test
    void get200WhenGettingOffersStatusesWithCsvFormat() {
        final var tenantId = UUID.randomUUID();
        final var endDateTime = OffsetDateTime.now();
        final var startDateTime = endDateTime.minusMonths(3).plusDays(1);

        mongoTemplate.insertAll(List.of(
                buildUserStatesStore(tenantId, endDateTime.minusMonths(2), endDateTime.minusMonths(4)),
                buildUserStatesStore(tenantId, endDateTime.minusMonths(1)),
                buildUserStatesStore(tenantId, endDateTime.minusDays(20), endDateTime.minusDays(4), endDateTime.minusDays(50), endDateTime.minusMonths(12)),

                buildUserStatesStore(tenantId, endDateTime.minusMonths(5), endDateTime.minusMonths(4)),
                buildUserStatesStore(tenantId, endDateTime.minusMonths(6)),
                buildUserStatesStore(tenantId),
                buildUserStatesStore(UUID.fromString("8015d5c9-298c-4005-9f64-3ee260788234"), endDateTime.minusMonths(2), endDateTime.minusMonths(4)),
                buildUserStatesStore(UUID.fromString("6f22dc4e-a14a-4154-be3f-5c502e28768a"), endDateTime.minusMonths(1)),
                buildUserStatesStore(UUID.fromString("f37fd847-2196-4a4f-943c-c8035c878d12")),
                buildUserStatesStore(UUID.fromString("c94868d5-78e5-4e10-b7f2-cf47fbf820b6"), endDateTime.minusMonths(5)),
                buildUserStatesStore(UUID.fromString("2d75cf05-cc48-4d18-b0c9-d8d6d7c4729f"), endDateTime.minusMonths(5), endDateTime.minusMonths(7))
        ));
        mongoTemplate.insertAll(List.of(
                buildLoanOfferStore(tenantId.toString() + endDateTime.minusMonths(2), OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),
                buildLoanOfferStore(tenantId.toString() + endDateTime.minusMonths(2), OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),

                buildLoanOfferStore(tenantId.toString() + endDateTime.minusDays(4), OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),
                buildLoanOfferStore(tenantId.toString() + endDateTime.minusDays(4), OffsetDateTime.now(), OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), LocalDateTime.now()),

                buildLoanOfferStore(tenantId.toString() + endDateTime.minusMonths(1), null, null, OffsetDateTime.now().minusDays(1), LocalDateTime.now()),
                buildLoanOfferStore(tenantId.toString() + endDateTime.minusMonths(1), null, null, OffsetDateTime.now(), LocalDateTime.now()),

                buildLoanOfferStore(tenantId.toString() + endDateTime.minusDays(20), null, OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),
                buildLoanOfferStore(tenantId.toString() + endDateTime.minusDays(20), null, OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),

                buildLoanOfferStore(tenantId.toString() + endDateTime.minusMonths(12), OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),
                buildLoanOfferStore(tenantId.toString() + endDateTime.minusMonths(12), OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),
                buildLoanOfferStore(tenantId.toString() + endDateTime.minusMonths(12), OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),

                buildLoanOfferStore("cbddd96a-c3d9-4beb-b7df-52aa330b1bef" + endDateTime.minusMonths(2), OffsetDateTime.now().minusDays(3), OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now()),
                buildLoanOfferStore("b2b7c043-7169-4350-a040-d33468a8ee01" + endDateTime.minusMonths(2), OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(), LocalDateTime.now())
        ));

        webClient
                .get()
                .uri(uri -> uri
                        .pathSegment("loan")
                        .pathSegment("distribution-channel")
                        .pathSegment(tenantId.toString())
                        .pathSegment("status")
                        .queryParam("start-date", startDateTime.toLocalDate())
                        .queryParam("end-date", endDateTime.toLocalDate())
                        .queryParam("response-format", "csv")
                        .build())
                .headers(headers -> headers.setBasicAuth(REPORTING_USERNAME, REPORTING_PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/csv");
    }
}
