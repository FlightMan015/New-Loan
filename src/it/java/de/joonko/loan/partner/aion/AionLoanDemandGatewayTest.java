package de.joonko.loan.partner.aion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.dac.fts.model.Account;
import de.joonko.loan.dac.fts.model.Balance;
import de.joonko.loan.dac.fts.model.FtsRawData;
import de.joonko.loan.dac.fts.model.Turnover;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.partner.aion.model.*;
import de.joonko.loan.util.JsonUtil;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static de.joonko.loan.partner.aion.CreditApplicationRequestTestData.buildCreditApplicationRequest;
import static de.joonko.loan.partner.aion.OffersToBeatRequestTestData.buildBestOffersRequest;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(RandomBeansExtension.class)
@AutoConfigureWebTestClient
@SpringBootTest
@ContextConfiguration(initializers = WireMockInitializer.class)
@ActiveProfiles("integration")
class AionLoanDemandGatewayTest {

    private static final String authURL = "/aion/someBrandId/oauth2/token";
    private static final String processURL = "/aion/someBrandId/credits-channel-app/api/1/processes";
    private static final String bestOffersToBeatURL = "/aion/someBrandId/credits-channel-app/api/1/processes/zzz";
    private static final String ftsURL = "/fts/123/accountSnapshot?format=json";
    private static final String ftsApiKey = "Basic " + Base64.getEncoder().encodeToString(("api:someApiKey").getBytes());
    private static final String authToken = "eyJhbGciOiJSUzI1NiJ9.eyJkYXRhIjp7InJpZ2h0c0J5Um9sZSI6eyJBUElfSU5JVF9SSUdIVF9TRVQiOlsiQ0FOX0FDQ0VTU19BUEkiXX19LCJqdGkiOiIxMzAuMjNhZDFmOGYtMzljNS00ZjY1LWIxYmQtMjNjODU2NjAzYzE5IiwiZXhwIjoxNTgxMzUwOTUwLCJhdWQiOiJvQXV0aDIiLCJwcm4iOiI2MzczOTUiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQVBJX0lOSVRfUklHSFRfU0VUIl19fQ.f76MIJe6zRbFit8D5y9Pcnbd-qj8d_6zUAUdjPnnu02vT7wyEz5m4NheEi0IFaLg-P9SOPjIEb0RIyQ9ziPTKYRmvOOZXaMy7nRwIZv75tfZ9YOyGHZ3e3xozSL0on30LMnOeX442s3gfVz3qbfhs63_ZZans-MD2QvIPIWg7r-u6vl0tGv8nz6xSyP9AXwSjAfdlg9vUdlfLOWb1EgVRyH4uq9m3pnO4wMPf72mMWy_lFb-FNkYH5Aluf9Zk54Ntwj1YzRIBD2msgPK0Ud0rLBZ36IKW_rM_gIqjJqVD8ghaJjmXvA-eNDXDa8nZ7J9iVCDTlNcZTgjTn5kdlpVtQ";
    private static final String processId = "zzz";

    @Autowired
    private WireMockServer mockServer;
    @Autowired
    private AionLoanDemandGateway aionLoanDemandGateway;
    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void clearMockServerExpectations() {
        mockServer.resetAll();
    }

    @Test
    @DisplayName("Should successfully submit the data to process endpoint")
    void callApi(@Random LoanDemandRequest loanDemandRequest) throws JsonProcessingException {
        loanDemandRequest.setFtsTransactionId("123");
        mongoTemplate.insert(loanDemandRequest);
        setHappyPathExpectationsForProcess();

        final var response = aionLoanDemandGateway.callApi(buildCreditApplicationRequest(), loanDemandRequest.getApplicationId());

        StepVerifier.create(response).expectNextCount(1).verifyComplete();
    }

    @Test
    @DisplayName("Should successfully call offers to beat endpoint and return empty result")
    void offersToBeat_emptyCase(@Random LoanDemandRequest loanDemandRequest) throws JsonProcessingException {
        loanDemandRequest.setFtsTransactionId("123");
        final var creditApplicationResponseStore = CreditApplicationResponseStore.builder()
                .processId(processId)
                .applicationId(loanDemandRequest.getApplicationId())
                .build();
        mongoTemplate.insert(creditApplicationResponseStore);
        setErrorPathExpectationsForOffersToBeat();

        final var bestOfferRequests = new BestOffersRequest[]{buildBestOffersRequest()};

        final var response = aionLoanDemandGateway.getOffers(loanDemandRequest.getApplicationId(), processId, bestOfferRequests);

        StepVerifier.create(response).expectNextCount(0).verifyComplete();
    }

    @Test
    @DisplayName("Should successfully call offers to beat endpoint and return error when no process found")
    void offersToBeat_errorCase(@Random LoanDemandRequest loanDemandRequest) throws JsonProcessingException {
        loanDemandRequest.setFtsTransactionId("123");
        setErrorPathExpectationsForOffersToBeat();

        final var bestOfferRequests = new BestOffersRequest[]{buildBestOffersRequest()};

        final var response = aionLoanDemandGateway.getOffers(loanDemandRequest.getApplicationId(), processId, bestOfferRequests);

        StepVerifier.create(response).verifyError();
    }

    @Test
    @DisplayName("Should successfully call offers to beat endpoint")
    void offersToBeat_successCase() throws JsonProcessingException {
        final var applicationId = "38923hf9238f";
        final var creditApplicationResponseStore = CreditApplicationResponseStore.builder()
                .processId(processId)
                .applicationId(applicationId)
                .build();
        mongoTemplate.insert(creditApplicationResponseStore);
        setHappyPathExpectationsForOffersToBeat();
        final var offerReq = buildBestOffersRequest();
        final var offerRes = offerReq.getTransmissionData().getOffers().get(0).getOfferDetails();

        final var bestOfferRequests = new BestOffersRequest[]{offerReq};
        final var response = aionLoanDemandGateway.getOffers(applicationId, processId, bestOfferRequests);

        StepVerifier.create(response).consumeNextWith(res -> assertAll(
                        () -> assertEquals(offerRes.getAmount().intValue(), res.getAmount()),
                        () -> assertEquals(offerRes.getMaturity(), res.getDurationInMonth()),
                        () -> assertEquals(offerRes.getMonthlyInstalmentAmount(), res.getMonthlyRate()),
                        () -> assertEquals(offerRes.getTotalRepaymentAmount(), res.getTotalPayment()),
                        () -> assertEquals(offerRes.getAnnualPercentageRate().multiply(BigDecimal.valueOf(100)), res.getEffectiveInterestRate()),
                        () -> assertEquals(offerRes.getNominalInterestRate().multiply(BigDecimal.valueOf(100)), res.getNominalInterestRate()),
                        () -> assertEquals(Bank.AION.label, res.getLoanProvider().getName()),
                        () -> assertEquals(offerRes.getId(), res.getLoanProviderOfferId())
                )).expectNextCount(2)
                .verifyComplete();
    }


    private void setHappyPathExpectationsForProcess() throws JsonProcessingException {
        setGetTokenExpectations(mockServer);
        setFTSExpectations(mockServer);
        setProcessExpectations(mockServer);
    }

    private void setErrorPathExpectationsForOffersToBeat() throws JsonProcessingException {
        setGetTokenExpectations(mockServer);
        setOffersToBeatErrorExpectations(mockServer);
    }

    private void setHappyPathExpectationsForOffersToBeat() throws JsonProcessingException {
        setGetTokenExpectations(mockServer);
        setOffersToBeatSuccessExpectations(mockServer);
    }

    private void setGetTokenExpectations(WireMockServer mockServer) throws JsonProcessingException {
        mockServer.stubFor(
                WireMock.post(authURL)
                        .willReturn(aResponse()
                                .withBody(JsonUtil.getObjectAsJsonString(AionAuthToken.builder()
                                        .token(authToken)
                                        .build()))
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    private void setProcessExpectations(WireMockServer mockServer) throws JsonProcessingException {
        mockServer.stubFor(
                WireMock.post(processURL)
                        .withHeader("X-Token", equalTo(authToken))
                        .willReturn(aResponse()
                                .withBody(JsonUtil.getObjectAsJsonString(CreditApplicationResponse.builder()
                                        .processId(processId)
                                        .variables(List.of(CreditApplicationResponse.Variable.builder().name(AionResponseValueType.DECISION).value("POSITIVE").build()))
                                        .build()))
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    private void setOffersToBeatErrorExpectations(WireMockServer mockServer) throws JsonProcessingException {
        mockServer.stubFor(
                WireMock.post(bestOffersToBeatURL)
                        .withHeader("X-Token", equalTo(authToken))
                        .willReturn(aResponse()
                                .withBody(JsonUtil.getObjectAsJsonString(CreditApplicationResponse.builder()
                                        .processId(processId)
                                        .variables(List.of(CreditApplicationResponse.Variable.builder().name(AionResponseValueType.ERROR_MESSAGE).value("2").build()))
                                        .build()))
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    private void setOffersToBeatSuccessExpectations(WireMockServer mockServer) throws JsonProcessingException {
        mockServer.stubFor(
                WireMock.put(bestOffersToBeatURL)
                        .withHeader("X-Token", equalTo(authToken))
                        .willReturn(aResponse()
                                .withBody(JsonUtil.getObjectAsJsonString(OffersToBeatResponse.builder()
                                        .processId(processId)
                                        .variables(List.of(OffersToBeatResponse.Variable.builder().name(AionResponseValueType.OFFERS_LIST).value(
                                                buildBestOffersRequest().getTransmissionData().getOffers()
                                        ).build()))
                                        .build()))
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    private void setFTSExpectations(WireMockServer mockServer) throws JsonProcessingException {
        final var ftsRawData = FtsRawData.builder()
                .account(Account.builder().build())
                .balance(Balance.builder().build())
                .turnovers(List.of(Turnover.builder().build()))
                .build();

        mockServer.stubFor(
                WireMock.get(ftsURL)
                        .withHeader(HttpHeaders.AUTHORIZATION, equalTo(ftsApiKey))
                        .willReturn(aResponse()
                                .withBody(JsonUtil.getObjectAsJsonString(ftsRawData))
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }
}