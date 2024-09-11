package de.joonko.loan.acceptOffers.api;

import com.github.tomakehurst.wiremock.WireMockServer;

import de.joonko.loan.acceptoffer.api.AcceptOfferRequest;
import de.joonko.loan.acceptoffer.api.AcceptOfferResponse;
import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.partner.aion.AionClientMocks;
import de.joonko.loan.partner.aion.model.CreditApplicationResponseStore;
import de.joonko.loan.partner.consors.PersonalizedCalculationsStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;

import static de.joonko.loan.acceptOffers.api.AcceptOffersTestData.*;
import static de.joonko.loan.acceptOffers.api.ConsorsMockServerClient.fakeGetApprovedOffer;
import static de.joonko.loan.acceptOffers.api.ConsorsMockServerClient.fakeGetToken;
import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailVerifiedJwt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@ExtendWith({RandomBeansExtension.class, MockitoExtension.class})
@AutoConfigureWebTestClient(timeout = "10000")
@SpringBootTest
@ContextConfiguration(initializers = WireMockInitializer.class)
@ActiveProfiles("integration")
class AcceptOfferIT {

    @Autowired
    private WebTestClient webClient;
    @Autowired
    private WireMockServer mockServer;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Random
    private PersonalizedCalculationsStore personalizedCalculationsStore;
    @Random
    private LoanDemandStore loanDemandStore;
    @Random
    private LoanOfferStore loanOfferStore;
    @Random
    private CreditApplicationResponseStore creditApplicationResponseStore;

    private static final String ACCEPT_OFFER_URI = "/api/v1/loan/accept-offer/";
    private static final String USER_UUID = "35004d2f-ee8a-45fe-97e9-0542e1a0160b";

    @BeforeEach
    void clearMockServerExpectations() {
        mockServer.resetAll();
        loanOfferStore.getOffer().setDurationInMonth(12);
        loanOfferStore.setUserUUID(USER_UUID);
        loanDemandStore.setUserUUID(USER_UUID);
    }

    @Test
    void getUnauthorizedWhenMissingJwtToken() {
        webClient
                .post()
                .uri(URI.create(ACCEPT_OFFER_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Nested
    class Deserialization {

        @Test
        void deserializeLoanProviderString(@Random LoanDemandRequest loanDemandRequest) {
            Jwt jwt = mockEmailVerifiedJwt(USER_UUID);
            mongoTemplate.insert(getEditedPersonalizedCalculationsStore(personalizedCalculationsStore));
            loanOfferStore.getOffer().setLoanProvider(new LoanProvider(Bank.CONSORS.label));
            String applicationId = personalizedCalculationsStore.getApplicationId();
            String loanOfferId = loanOfferStore.getLoanOfferId();
            String validLoanProviderFormat = getRequestWithValidData(loanOfferId, applicationId);

            mongoTemplate.insert(getEditedLoanDemandStore(loanDemandStore, applicationId));
            mongoTemplate.insert(getEditedLoanOfferStore(loanOfferStore, applicationId, Bank.CONSORS));
            mongoTemplate.insert(getEditedLoanDemandRequest(loanDemandRequest, applicationId));

            fakeGetToken(mockServer);
            fakeGetApprovedOffer(mockServer);

            webClient
                    .mutateWith(mockJwt().jwt(jwt))
                    .post()
                    .uri(URI.create(ACCEPT_OFFER_URI))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(validLoanProviderFormat)
                    .exchange()
                    .expectStatus()
                    .isOk();
        }

        @Test
        void deserializeWrongLoanProviderString() {
            Jwt jwt = mockEmailVerifiedJwt(USER_UUID);
            String invalidLoanProviderFormat = getRequestWithInvalidData();

            webClient
                    .mutateWith(mockJwt().jwt(jwt))
                    .post()
                    .uri(URI.create(ACCEPT_OFFER_URI))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidLoanProviderFormat)
                    .exchange()
                    .expectStatus()
                    .is5xxServerError();
        }
    }

    @Test
    void acceptConsorsOffer(@Random LoanDemandRequest loanDemandRequest) {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID);
        mongoTemplate.insert(getEditedPersonalizedCalculationsStore(personalizedCalculationsStore));
        loanOfferStore.getOffer().setLoanProvider(new LoanProvider(Bank.CONSORS.label));
        String applicationId = personalizedCalculationsStore.getApplicationId();

        mongoTemplate.insert(getEditedLoanDemandRequest(loanDemandRequest, applicationId));
        mongoTemplate.insert(getEditedLoanDemandStore(loanDemandStore, applicationId));
        mongoTemplate.insert(getEditedLoanOfferStore(loanOfferStore, applicationId, Bank.CONSORS));

        final AcceptOfferRequest acceptOfferRequest = getAcceptOfferRequestWithoutLoanProvider(applicationId, loanOfferStore.getLoanOfferId());

        fakeGetToken(mockServer);
        fakeGetApprovedOffer(mockServer);

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .post()
                .uri(URI.create(ACCEPT_OFFER_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(acceptOfferRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AcceptOfferResponse.class)
                .value(response -> {
                    assertEquals(84, response.getDuration());
                    assertEquals(30000, response.getLoanAsked());
                });
    }

    @Test
    void shouldReturnBadRequestForInValidRequest(@Random LoanDemandRequest loanDemandRequest) {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID);
        mongoTemplate.insert(getEditedPersonalizedCalculationsStore(personalizedCalculationsStore));
        loanOfferStore.getOffer().setLoanProvider(new LoanProvider(Bank.CONSORS.label));
        String applicationId = personalizedCalculationsStore.getApplicationId();

        mongoTemplate.insert(getEditedLoanDemandRequest(loanDemandRequest, applicationId));
        mongoTemplate.insert(getEditedLoanDemandStore(loanDemandStore, applicationId));
        mongoTemplate.insert(getEditedLoanOfferStore(loanOfferStore, applicationId, Bank.CONSORS));

        final AcceptOfferRequest acceptOfferRequest = getAcceptOfferRequestWithoutLoanProvider(applicationId, loanOfferStore.getLoanOfferId());
        acceptOfferRequest.setLoanOfferId(null);

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .post()
                .uri(URI.create(ACCEPT_OFFER_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(acceptOfferRequest)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Nested
    class AionTests {

        private final Bank bank = Bank.AION;
        private final AionClientMocks aionClientMocks = new AionClientMocks(mockServer);

        @BeforeEach
        void setUp() {
            loanOfferStore.getOffer().setLoanProvider(new LoanProvider(bank.label));
        }

        @Test
        void acceptAionOffer(@Random LoanDemandRequest loanDemandRequest) {
            Jwt jwt = mockEmailVerifiedJwt(USER_UUID);
            final var applicationId = loanDemandRequest.getApplicationId();
            mongoTemplate.insert(getEditedLoanOfferStore(loanOfferStore, applicationId, bank));
            mongoTemplate.insert(getEditedLoanDemandStore(loanDemandStore, applicationId));
            mongoTemplate.insert(getEditedLoanDemandRequest(loanDemandRequest, applicationId));
            mongoTemplate.insert(getEditedCreditApplicationResponseStore(creditApplicationResponseStore, applicationId));

            aionClientMocks.fake200WhenAuth();
            aionClientMocks.fake200WhenSendingOfferChoice("token", creditApplicationResponseStore.getProcessId());

            final var acceptOfferRequest = AcceptOfferRequest.builder()
                    .loanOfferId(loanOfferStore.getLoanOfferId())
                    .build();

            webClient
                    .mutateWith(mockJwt().jwt(jwt))
                    .post()
                    .uri(URI.create(ACCEPT_OFFER_URI))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(acceptOfferRequest)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(AcceptOfferResponse.class)
                    .value(response -> assertEquals(LoanApplicationStatus.OFFER_ACCEPTED, response.getStatus()));
        }

    }

    @Nested
    class PostbankTests {

        private final Bank bank = Bank.POSTBANK;

        @BeforeEach
        void setUp() {
            loanOfferStore.getOffer().setLoanProvider(new LoanProvider(bank.label));
        }

        @Test
        void acceptPostbankOffer(@Random LoanDemandRequest loanDemandRequest) {
            Jwt jwt = mockEmailVerifiedJwt(USER_UUID);
            final var applicationId = loanDemandRequest.getApplicationId();
            mongoTemplate.insert(getEditedLoanOfferStore(loanOfferStore, applicationId, bank));
            mongoTemplate.insert(getEditedLoanDemandStore(loanDemandStore, applicationId));
            mongoTemplate.insert(getEditedLoanDemandRequest(loanDemandRequest, applicationId));
            mongoTemplate.insert(getPostbankLoanDemandStore(applicationId, "56932856"));

            final var acceptOfferRequest = AcceptOfferRequest.builder()
                    .loanOfferId(loanOfferStore.getLoanOfferId())
                    .build();

            webClient
                    .mutateWith(mockJwt().jwt(jwt))
                    .post()
                    .uri(URI.create(ACCEPT_OFFER_URI))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(acceptOfferRequest)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(AcceptOfferResponse.class)
                    .value(response -> assertEquals(LoanApplicationStatus.OFFER_ACCEPTED, response.getStatus()));
        }

    }
}
