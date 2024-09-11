package de.joonko.loan.partner.auxmoney.getoffers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;

import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.offer.domain.*;
import de.joonko.loan.partner.auxmoney.AuxmoneyLoanDemandGateway;
import de.joonko.loan.partner.auxmoney.model.AuxmoneySingleCallResponse;
import de.joonko.loan.partner.auxmoney.model.ErrorResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static de.joonko.loan.partner.auxmoney.getoffers.testdata.AuxmoneyLoanDemandTestData.setBadRequestResponseExpectations;
import static de.joonko.loan.partner.auxmoney.getoffers.testdata.AuxmoneyLoanDemandTestData.setGetAuxmoneyGetOfferExpectations;

@ContextConfiguration(initializers = WireMockInitializer.class)
@ExtendWith(RandomBeansExtension.class)
@SpringBootTest
@ActiveProfiles("integration")
class AuxmoneyLoanDemandGatewayTest {

    @Autowired
    private WireMockServer mockServer;
    @Autowired
    private AuxmoneyLoanDemandGateway auxmoneyLoanDemandGateway;

    @Random
    private PersonalDetails personalDetails;
    @Random
    private CreditDetails creditDetails;
    @Random
    private EmploymentDetails employmentDetails;
    @Random
    private ContactData contactData;
    @Random
    private DigitalAccountStatements digitalAccountStatements;
    @Random
    private AuxmoneySingleCallResponse auxmoneySingleCallResponse;
    @Random
    private ErrorResponse errorResponse;

    @BeforeEach
    void clearMockServerExpectations() {
        mockServer.resetAll();
    }

    @Test
    @DisplayName("Should Return Offers from Auxmoney Mock Server For Valid Request : For all durations")
    void getAuxmoneyOffersForValidRequest() throws JsonProcessingException {
        auxmoneySingleCallResponse.setIsSuccess(true);
        auxmoneySingleCallResponse.setManualQualityAssurance(false);
        auxmoneySingleCallResponse.setLoanAsked(1000);
        auxmoneySingleCallResponse.setDuration(48);
        LoanDemand loanDemand = loanDemandWithLoanAsked(1000);
        setGetAuxmoneyGetOfferExpectations(mockServer, auxmoneySingleCallResponse);

        Flux<LoanOffer> auxmoneyOffers = this.auxmoneyLoanDemandGateway.getLoanOffers(loanDemand, LoanDuration.FORTY_EIGHT);
        StepVerifier.create(auxmoneyOffers)
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

    }

    private LoanDemand loanDemandWithLoanAsked(int loanAsked) {
        return new LoanDemand(RandomStringUtils.randomAlphabetic(20), loanAsked, "car", LoanDuration.TWENTY_FOUR, LoanCategory.CAR_LOAN, personalDetails, creditDetails, employmentDetails, contactData, digitalAccountStatements, null, null, null, null, null, List.of(), null);
    }

    @Test
    @DisplayName("Should Return Empty Offers For Invalid Request")
    void getAuxmoneyOffersForInvalidValidRequest() throws JsonProcessingException {
        auxmoneySingleCallResponse.setIsSuccess(true);
        LoanDemand loanDemand = loanDemandWithLoanAsked(999);
        setGetAuxmoneyGetOfferExpectations(mockServer, auxmoneySingleCallResponse);

        Flux<LoanOffer> auxmoneyOffers = this.auxmoneyLoanDemandGateway.getLoanOffers(loanDemand, LoanDuration.FORTY_EIGHT);
        StepVerifier.create(auxmoneyOffers)
                .expectSubscription()
                .expectNextCount(10);
    }

    @Test
    @DisplayName("should return empty flux , If all API calls return for bad request error   ")
    void handleException() throws JsonProcessingException {
        setBadRequestResponseExpectations(mockServer, errorResponse);
        LoanDemand loanDemand = loanDemandWithLoanAsked(1000);
        auxmoneySingleCallResponse.setIsSuccess(true);

        Flux<LoanOffer> auxmoneyOffers = this.auxmoneyLoanDemandGateway.getLoanOffers(loanDemand, LoanDuration.FORTY_EIGHT);
        StepVerifier.create(auxmoneyOffers)
                .expectComplete()
                .verify();
    }


    @Test
    @DisplayName("Should Return Empty Response For Valid Request and Valid Response But For is_success=false ")
    void handleIsSuccessFalse() throws JsonProcessingException {
        auxmoneySingleCallResponse.setIsSuccess(false);
        LoanDemand loanDemand = loanDemandWithLoanAsked(1000);
        setGetAuxmoneyGetOfferExpectations(mockServer, auxmoneySingleCallResponse);

        Flux<LoanOffer> auxmoneyOffers = this.auxmoneyLoanDemandGateway.getLoanOffers(loanDemand, LoanDuration.FORTY_EIGHT);
        StepVerifier.create(auxmoneyOffers)
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }
}
