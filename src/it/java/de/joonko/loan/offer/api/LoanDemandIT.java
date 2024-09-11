package de.joonko.loan.offer.api;

import com.github.tomakehurst.wiremock.WireMockServer;

import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.messaging.config.KafkaTestConfig;
import de.joonko.loan.offer.api.model.CustomErrorMessageKey;
import de.joonko.loan.offer.api.model.OfferResponseState;
import de.joonko.loan.offer.api.model.OffersResponse;
import de.joonko.loan.offer.api.model.UserJourneyStateResponse;
import de.joonko.loan.user.states.UserStatesStore;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;

import lombok.SneakyThrows;

import static de.joonko.loan.offer.testdata.LoanDemandTestData.getAdditionalInformation;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getLoanOffers;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getPersonalInformation;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getTransactionalData;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getUserStatesStoreForBonifyOutdatedSalaryAccount;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getUserStatesStoreForBonifyUser;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getUserStatesStoreForClassifyingTransactions;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getUserStatesStoreForFetchingAdditionalInfo;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getUserStatesStoreForFetchingOffers;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getUserStatesStoreForMissingPersonalData;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getUserStatesStoreForMissingSalaryAccountAfterClassification;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getUserStatesStoreForOffersReady;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getUserStatesStoreForOffersReadyWithMissingUserInfo;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.getUserStatesStoreForUserJourneyState;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.mockGettingBonifyUserFromAuthServer;
import static de.joonko.loan.offer.testdata.LoanDemandTestData.mockGettingNonBonifyUserFromAuthServer;
import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailNotVerifiedJwt;
import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailVerifiedJwt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@ContextConfiguration(initializers = WireMockInitializer.class)
@AutoConfigureWebTestClient(timeout = "36000")
class LoanDemandIT extends KafkaTestConfig {

    @Autowired
    private WebTestClient webClient;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private WireMockServer mockServer;

    private static final String GET_OFFERS_URL = "/api/v1/loan/offers";
    private static final String GET_USER_STATE_URL = "/api/v1/loan/user";

    private static final String USER_UUID_1 = "35004d2f-ee8a-45fe-97e9-0542e1a0160a";
    private static final String USER_UUID_2 = "b16024cc-23a7-45c9-8aa8-57b5fc45bfcb";
    private static final String USER_UUID_3 = "b16024cc-23a7-45c9-8aa8-57b5fc45bfca";
    private static final String USER_UUID_4 = "33e9fbff-42d9-46fb-95d0-4a7c72635a85";
    private static final String USER_UUID_5 = "8c54c636-4265-4f4b-a262-19f448508bc7";
    private static final String USER_UUID_6 = "5a3fc491-e985-48a1-b5a6-3bb646c76868";
    private static final String USER_UUID_7 = "a6766367-faf0-4747-bc71-1a931cec8887";
    private static final String USER_UUID_8 = "c2bbae85-1fda-4f86-80ca-66774e8beccf";
    private static final String USER_UUID_9 = "1e3c8e95-8857-448a-aa1f-168d18a7ab1c";
    private static final String USER_UUID_10 = "6ab38231-759a-4e9b-880d-b1888f4ff9d9";
    private static final String USER_UUID_12 = "62fa3588-af13-4723-be30-646859608cc9";
    private static final String USER_UUID_13 = "dcaf08c4-c61f-460f-a82a-fee04be09320";
    private static final String USER_UUID_14 = "d696fbba-7d51-428b-8924-cebc25924d80";
    private static final String USER_UUID_15 = "f5f461bc-7899-4c1d-9eb9-930ba9a1a898";
    private static final String USER_UUID_16 = "1a73b3a2-58b9-4c32-a307-c25e727ee1e4";
    private static final String USER_UUID_17 = "1a73b3a2-58b9-4c32-a307-c25e727ee1e3";

    private static final String LOAN_PURPOSE = "new_car";

    @AfterAll
    void cleanUpMocks() {
        mockServer.resetAll();
    }

    @Test
    void getUnauthorizedWhenMissingJwtToken() {
        webClient
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("purpose", LOAN_PURPOSE)
                        .queryParam("onlyBonify", false).build())
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @SneakyThrows
    @Test
    void getForbiddenWhenUserNotVerifiedByEmail() {
        Jwt jwt = mockEmailNotVerifiedJwt(USER_UUID_1);

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("purpose", LOAN_PURPOSE)
                        .queryParam("onlyBonify", false).build())
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @SneakyThrows
    @Test
    void getWaitingWhenNewUser() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_1);

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("purpose", LOAN_PURPOSE)
                        .queryParam("onlyBonify", false).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo(OfferResponseState.CLASSIFYING_TRANSACTIONS)
                .jsonPath("$.data.messageKey").doesNotExist();
    }

    @Test
    void getMissingSalaryAccountWhenMissingSalaryAccountState() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_2);
        mongoTemplate.insert(getUserStatesStoreForMissingSalaryAccountAfterClassification(USER_UUID_2));

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("purpose", LOAN_PURPOSE).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo(OfferResponseState.MISSING_SALARY_ACCOUNT)
                .jsonPath("$.data.messageKey").isEqualTo(CustomErrorMessageKey.NON_SALARY_ACCOUNT_ADDED);
    }

    @Test
    void getOffersReady() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_3);
        mongoTemplate.insert(getUserStatesStoreForOffersReady(USER_UUID_3));
        mongoTemplate.insert(getTransactionalData(USER_UUID_3));
        mongoTemplate.insertAll(getLoanOffers(USER_UUID_3));
        mongoTemplate.insert(getPersonalInformation(USER_UUID_3));
        int loanAmount = 10000;

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("amount", loanAmount)
                        .queryParam("purpose", LOAN_PURPOSE)
                        .queryParam("onlyBonify", false).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo(OfferResponseState.OFFERS_READY)
                .jsonPath("$.data.kycRelatedPersonalDetails.nameOnAccount").isEqualTo("Doe, Janusz")
                .jsonPath("$.data.kycRelatedPersonalDetails.iban").isEqualTo("DE36500105177243855757")
                .jsonPath("$.data.kycRelatedPersonalDetails.bic").isEqualTo("TESTDE88XXX")
                .jsonPath("$.data.recentQueriedAmounts.size()").isEqualTo(2)
                .jsonPath("$.data.offers.size()").isEqualTo(4)
                .jsonPath("$.data.totalOffers").isEqualTo(4)
                .jsonPath("$.data.requestedOffers").isEqualTo(4);
    }

    @Test
    void getOffersReadyWithOnlyBonifyFilter() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_17);
        mongoTemplate.insert(getUserStatesStoreForOffersReady(USER_UUID_17));
        mongoTemplate.insert(getTransactionalData(USER_UUID_17));
        mongoTemplate.insertAll(getLoanOffers(USER_UUID_17));
        mongoTemplate.insert(getPersonalInformation(USER_UUID_17));
        int loanAmount = 10000;

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("amount", loanAmount)
                        .queryParam("purpose", LOAN_PURPOSE)
                        .queryParam("onlyBonify", true).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo(OfferResponseState.OFFERS_READY)
                .jsonPath("$.data.kycRelatedPersonalDetails.nameOnAccount").isEqualTo("Doe, Janusz")
                .jsonPath("$.data.kycRelatedPersonalDetails.iban").isEqualTo("DE36500105177243855757")
                .jsonPath("$.data.kycRelatedPersonalDetails.bic").isEqualTo("TESTDE88XXX")
                .jsonPath("$.data.recentQueriedAmounts.size()").isEqualTo(2)
                .jsonPath("$.data.offers.size()").isEqualTo(1)
                .jsonPath("$.data.totalOffers").isEqualTo(4)
                .jsonPath("$.data.requestedOffers").isEqualTo(1);
    }

    @Test
    void getOffersReadyWhenValidOffersExistEvenWithMissingUserInfo() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_14);
        mongoTemplate.insert(getUserStatesStoreForOffersReadyWithMissingUserInfo(USER_UUID_14));
        mongoTemplate.insert(getTransactionalData(USER_UUID_14));
        mongoTemplate.insertAll(getLoanOffers(USER_UUID_14));
        mongoTemplate.insert(getPersonalInformation(USER_UUID_14));
        int loanAmount = 10000;

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("amount", loanAmount)
                        .queryParam("purpose", LOAN_PURPOSE)
                        .queryParam("onlyBonify", false).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo(OfferResponseState.OFFERS_READY)
                .jsonPath("$.data.kycRelatedPersonalDetails.nameOnAccount").isEqualTo("Doe, Janusz")
                .jsonPath("$.data.kycRelatedPersonalDetails.iban").isEqualTo("DE36500105177243855757")
                .jsonPath("$.data.kycRelatedPersonalDetails.bic").isEqualTo("TESTDE88XXX")
                .jsonPath("$.data.recentQueriedAmounts.size()").isEqualTo(2)
                .jsonPath("$.data.offers.size()").isEqualTo(4)
                .jsonPath("$.data.totalOffers").isEqualTo(4)
                .jsonPath("$.data.requestedOffers").isEqualTo(4);
    }


    @SneakyThrows
    @Test
    void getFetchingAdditionalInformation() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_4);
        mongoTemplate.insert(getUserStatesStoreForFetchingAdditionalInfo(USER_UUID_4));
        mockGettingBonifyUserFromAuthServer(mockServer, USER_UUID_4);

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("onlyBonify", false).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(OffersResponse.class)
                .value(offersResponse ->
                        assertEquals(OfferResponseState.CLASSIFYING_TRANSACTIONS, offersResponse.getState())
                );
    }


    @SneakyThrows
    @Test
    void getFetchingTransactionalData() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_5);

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("purpose", LOAN_PURPOSE)
                        .queryParam("onlyBonify", false).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(OffersResponse.class)
                .value(offersResponse ->
                        assertEquals(OfferResponseState.CLASSIFYING_TRANSACTIONS, offersResponse.getState())
                );
    }

    @SneakyThrows
    @Test
    void getFetchingOffers() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_6);
        mongoTemplate.insert(getUserStatesStoreForFetchingOffers(USER_UUID_6));
        mongoTemplate.insert(getTransactionalData(USER_UUID_6));
        mongoTemplate.insert(getPersonalInformation(USER_UUID_6));
        mongoTemplate.insert(getAdditionalInformation(USER_UUID_6));
        mockGettingNonBonifyUserFromAuthServer(mockServer, USER_UUID_6);

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("purpose", LOAN_PURPOSE)
                        .queryParam("onlyBonify", false).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(OffersResponse.class)
                .value(offersResponse ->
                        assertEquals(OfferResponseState.CLASSIFYING_TRANSACTIONS, offersResponse.getState())
                );
    }

    @SneakyThrows
    @Test
    void getMissingSalaryAccount() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_7);
        UserStatesStore userStatesStore = getUserStatesStoreForBonifyUser(USER_UUID_7);
        mongoTemplate.insert(userStatesStore);
        mockGettingNonBonifyUserFromAuthServer(mockServer, USER_UUID_7);

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("purpose", LOAN_PURPOSE)
                        .queryParam("onlyBonify", false).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(OffersResponse.class)
                .value(offersResponse ->
                        assertEquals(OfferResponseState.MISSING_SALARY_ACCOUNT, offersResponse.getState())
                );
    }


    @SneakyThrows
    @Test
    void getBonifyUserWithOutdatedSalaryAccount() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_8);
        UserStatesStore userStatesStore = getUserStatesStoreForBonifyOutdatedSalaryAccount(USER_UUID_8);
        mongoTemplate.insert(userStatesStore);
        mockGettingBonifyUserFromAuthServer(mockServer, USER_UUID_8);

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("purpose", LOAN_PURPOSE)
                        .queryParam("onlyBonify", false).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo(OfferResponseState.CLASSIFYING_TRANSACTIONS);
    }

    @SneakyThrows
    @Test
    void getMissingPersonalData() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_9);
        mongoTemplate.insert(getUserStatesStoreForMissingPersonalData(USER_UUID_9));
        mongoTemplate.insert(getTransactionalData(USER_UUID_9));
        mongoTemplate.insert(getPersonalInformation(USER_UUID_9));
        mongoTemplate.insert(getAdditionalInformation(USER_UUID_9));
        mockGettingBonifyUserFromAuthServer(mockServer, USER_UUID_9);

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("purpose", LOAN_PURPOSE)
                        .queryParam("onlyBonify", false).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo(OfferResponseState.MISSING_PERSONAL_DATA);
    }


    @SneakyThrows
    @Test
    void getClassifyingTransactions() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_10);
        mongoTemplate.insert(getUserStatesStoreForClassifyingTransactions(USER_UUID_10));
        mockGettingBonifyUserFromAuthServer(mockServer, USER_UUID_10);

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("purpose", LOAN_PURPOSE)
                        .queryParam("onlyBonify", false).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(OffersResponse.class)
                .value(offersResponse ->
                        assertEquals(OfferResponseState.CLASSIFYING_TRANSACTIONS, offersResponse.getState())
                );
    }


    @SneakyThrows
    @Test
    void getMissingSalaryAccountAfterFtsClassification() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_13);
        mockGettingNonBonifyUserFromAuthServer(mockServer, USER_UUID_13);
        mongoTemplate.insert(getUserStatesStoreForMissingSalaryAccountAfterClassification(USER_UUID_13));

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(uri -> uri.path(GET_OFFERS_URL)
                        .queryParam("purpose", LOAN_PURPOSE)
                        .queryParam("onlyBonify", false).build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo(OfferResponseState.MISSING_SALARY_ACCOUNT)
                .jsonPath("$.data.messageKey").isEqualTo(CustomErrorMessageKey.NON_SALARY_ACCOUNT_ADDED);
    }

    @Test
    void getUnauthorizedWhenGettingLatestUserJourneyState() {
        webClient
                .get()
                .uri(URI.create(GET_USER_STATE_URL))
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void getMissingLoanAmountWhenGettingLatestUserJourneyState() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_12);

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(URI.create(GET_USER_STATE_URL))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.amount").doesNotExist()
                .jsonPath("$.state").isEqualTo(UserJourneyStateResponse.UserJourneyState.MISSING_LOAN_AMOUNT)
                .jsonPath("$.purpose").doesNotExist();
    }

    @Test
    void getExistingLoanAmountWhenGettingLatestUserJourneyState() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_15);

        var loanAmount = 2000;
        mongoTemplate.insert(getUserStatesStoreForUserJourneyState(USER_UUID_15, loanAmount, null));

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(URI.create(GET_USER_STATE_URL))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.amount").isEqualTo(loanAmount)
                .jsonPath("$.state").isEqualTo(UserJourneyStateResponse.UserJourneyState.EXISTING_LOAN_AMOUNT)
                .jsonPath("$.purpose").doesNotExist();
    }

    @Test
    void getExistingLoanAmountAndPurposeWhenGettingLatestUserJourneyState() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID_16);

        var loanAmount = 2000;
        mongoTemplate.insert(getUserStatesStoreForUserJourneyState(USER_UUID_16, loanAmount, LOAN_PURPOSE));

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(URI.create(GET_USER_STATE_URL))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.amount").isEqualTo(loanAmount)
                .jsonPath("$.state").isEqualTo(UserJourneyStateResponse.UserJourneyState.EXISTING_LOAN_AMOUNT)
                .jsonPath("$.purpose").isEqualTo(LOAN_PURPOSE);
    }
}
