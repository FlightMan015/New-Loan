package de.joonko.loan.messaging;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import de.joonko.loan.avro.dto.loan_demand.LoanDemandMessage;
import de.joonko.loan.avro.dto.loan_offers.LoanOffersMessage;
import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.common.messaging.KafkaTopicNames;
import de.joonko.loan.identification.AuxmoneyResponses;
import de.joonko.loan.messaging.config.KafkaTestConfig;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.api.SolarisResponses;
import de.joonko.loan.offer.api.model.OfferResponseState;
import de.joonko.loan.offer.api.model.OffersResponse;

import org.apache.http.entity.ContentType;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import lombok.SneakyThrows;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getAdditionalInformation;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getLoanOffers;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getPersonalInformation;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getTransactionalData;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getUserStatesStoreForFetchingOffers;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getUserStatesStoreForOffersReady;
import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailVerifiedJwt;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;


@ContextConfiguration(initializers = WireMockInitializer.class)
@AutoConfigureWebTestClient(timeout = "36000")
class DSCommunicationIT extends KafkaTestConfig {

    private static final String GET_OFFERS_URL = "/api/v1/loan/offers";

    private static final String USER_FETCHING_OFFERS = "4a3fc491-e985-48a1-b5a6-3bb646c76868";
    private static final String USER_WITHOUT_OFFERS = "56004d2f-ee8a-45fe-97e9-0542e1a0160a";
    private static final String USER_WITH_OFFERS = "c16024cc-23a7-45c9-8aa8-57b5fc45bfcb";
    @Autowired
    private WebTestClient webClient;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private WireMockServer mockServer;

    private static final String LOAN_PURPOSE = "new_car";

    @AfterAll
    void clean() {
        mockServer.resetAll();
    }

    @Test
    void whenOffersAreReadyThenDSisNotified() {
        //given
        Jwt jwt = mockEmailVerifiedJwt(USER_WITH_OFFERS);
        mongoTemplate.insert(getUserStatesStoreForOffersReady(USER_WITH_OFFERS));
        mongoTemplate.insert(getTransactionalData(USER_WITH_OFFERS));
        mongoTemplate.insertAll(getLoanOffers(USER_WITH_OFFERS));
        mongoTemplate.insert(getPersonalInformation(USER_WITH_OFFERS));

        //when
        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("amount", 10000)
                        .queryParam("purpose", LOAN_PURPOSE)
                        .queryParam("onlyBonify", false).build())
                .exchange()
                .expectStatus()
                .isOk();

        ConsumerRecord<String, LoanOffersMessage> firstRecord = KafkaTestUtils.getSingleRecord(testLoanOfferConsumer, KafkaTopicNames.LOAN_OFFERS, 3000);
        LoanOffersMessage actual = firstRecord.value();
        assertAll(
                () -> assertNotNull(firstRecord),
                () -> assertEquals(USER_WITH_OFFERS, firstRecord.key()),
                () -> assertEquals(USER_WITH_OFFERS, actual.getUserUUID()),
                () -> assertEquals(4, actual.getOffers().size()),
                () -> assertEquals("applicationId1", actual.getApplicationId()));
    }

    @Test
    void whenOffersAreNoneThenDSisNotified() {
        //given
        Jwt jwt = mockEmailVerifiedJwt(USER_WITHOUT_OFFERS);
        mongoTemplate.insert(getUserStatesStoreForOffersReady(USER_WITHOUT_OFFERS));
        mongoTemplate.insert(getTransactionalData(USER_WITHOUT_OFFERS));
        mongoTemplate.insert(getPersonalInformation(USER_WITHOUT_OFFERS));

        //when
        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("amount", 10000)
                        .queryParam("purpose", LOAN_PURPOSE)
                        .queryParam("onlyBonify", false).build())
                .exchange()
                .expectStatus()
                .isOk();

        //then
        ConsumerRecord<String, LoanOffersMessage> singleRecord = KafkaTestUtils.getSingleRecord(testLoanOfferConsumer, KafkaTopicNames.LOAN_OFFERS, 3000);
        LoanOffersMessage actual = singleRecord.value();

        assertAll(
                () -> assertNotNull(singleRecord),
                () -> assertEquals(USER_WITHOUT_OFFERS, singleRecord.key()),
                () -> assertEquals(USER_WITHOUT_OFFERS, actual.getUserUUID()),
                () -> assertTrue(actual.getOffers().isEmpty()),
                () -> assertEquals("applicationId1", actual.getApplicationId()));
    }

    @SneakyThrows
    @Test
    void whenBanksAreRequestedThenDSisNotified() {
        Jwt jwt = mockEmailVerifiedJwt(USER_FETCHING_OFFERS);
        mongoTemplate.insert(getUserStatesStoreForFetchingOffers(USER_FETCHING_OFFERS));
        mongoTemplate.insert(getTransactionalData(USER_FETCHING_OFFERS));
        mongoTemplate.insert(getPersonalInformation(USER_FETCHING_OFFERS));
        mongoTemplate.insert(getAdditionalInformation(USER_FETCHING_OFFERS));
        mockAuxmoneyCall(mockServer);
        mockSolarisToken(mockServer);

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("amount", 10000)
                        .queryParam("purpose", LOAN_PURPOSE)
                        .queryParam("onlyBonify", false).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(OffersResponse.class)
                .value(offersResponse ->
                        assertEquals(OfferResponseState.CLASSIFYING_TRANSACTIONS, offersResponse.getState())
                );

        await().atMost(10, SECONDS).until(consumeLoanDemandMessage(), equalTo(10000));
        assertFalse(mongoTemplate.find(new Query(Criteria.where("userUUID").is(USER_FETCHING_OFFERS)), LoanDemandRequest.class).get(0).getPreChecks().isEmpty());
    }

    private Callable consumeLoanDemandMessage() {
        return () -> {
            ConsumerRecords<String, LoanDemandMessage> allRecord = KafkaTestUtils.getRecords(testLoanDemandConsumer);
            final var actualRecords = allRecord.records(KafkaTopicNames.LOAN_DEMAND);
            Spliterator<ConsumerRecord<String, LoanDemandMessage>> spliterator = Spliterators.spliteratorUnknownSize(actualRecords.iterator(), 0);
            final var loanDemands = StreamSupport.stream(spliterator, false).map(ConsumerRecord::value).collect(Collectors.toSet());

            final var actual = loanDemands.stream().filter(loanDemandMessage -> loanDemandMessage.getLoanAsked() == 10000).findFirst();
            return actual.map(message -> (int) message.getLoanAsked()).orElse(0);
        };
    }

    private static void mockAuxmoneyCall(WireMockServer mockServer) {
        mockServer.stubFor(
                WireMock.post("/auxmoney/distributionline/api/rest/partnerendpoints")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withBody(AuxmoneyResponses.GET_CONTRACT_RESPONSE)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())));
    }

    private static void mockSolarisToken(WireMockServer mockServer) {
        mockServer.stubFor(
                WireMock.post("/oauth/token")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withBody(SolarisResponses.ACCESS_TOKEN_RESPONSE)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())));
    }

}