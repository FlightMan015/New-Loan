package de.joonko.loan.partner.consors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.common.partner.consors.auth.JwtToken;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.domain.*;
import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferResponse;
import de.joonko.loan.partner.consors.model.PersonalizedCalculationsResponse;
import de.joonko.loan.partner.consors.model.SubscriptionStatus;
import de.joonko.loan.partner.consors.model.ValidateSubscriptionRequest;
import de.joonko.loan.util.JsonUtil;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.UUID;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static de.joonko.loan.partner.consors.ConsorsFixtures.FINALIZE_CALCULATION_RESPONSE_APPROVED;
import static de.joonko.loan.partner.consors.ConsorsFixtures.FINALIZE_CALCULATION_YELLOW_PROFILE_RESPONSE;
import static de.joonko.loan.partner.consors.ConsorsFixtures.PERSONALIZED_CALCULATION_RESPONSE;
import static de.joonko.loan.partner.consors.ConsorsFixtures.PRODUCT_RESPONSE_WITHOUT_VALIDATION_LINKS;
import static de.joonko.loan.partner.consors.ConsorsFixtures.VALIDATE_SUBSCRIPTION_RESPONSE;
import static de.joonko.loan.partner.consors.ConsorsFixtures.VALIDATE_SUBSCRIPTION_RESPONSE_WITHOUT_LINK;
import static de.joonko.loan.partner.consors.ConsorsFixtures.VALIDATION_RULES_RESPONSE_WITHOUT_LINKS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(RandomBeansExtension.class)
@AutoConfigureWebTestClient
@SpringBootTest
@ContextConfiguration(initializers = WireMockInitializer.class)
@ActiveProfiles("integration")
class ConsorsLoanDemandGatewayTest {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private WireMockServer mockServer;
    @Autowired
    private ConsorsLoanDemandGateway consorsLoanDemandGateway;
    @MockBean
    private ConsorsPrecheckFilter precheckFilter;
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
    private LoanDemandRequest loanDemandRequest;

    @BeforeEach
    void clearMockServerExpectations() {
        mockServer.resetAll();
        loanDemandRequest.setApplicationId(UUID.randomUUID().toString());
        loanDemandRequest.setLoanAsked(1500);
        mongoTemplate.insert(loanDemandRequest);
    }

    @Test
    void filterGateway(@Random LoanDemand loanDemand) {
        // given

        // when
        consorsLoanDemandGateway.filterGateway(loanDemand);

        // then
        verify(precheckFilter).test(any(LoanDemand.class));
    }

    @Test
    @DisplayName("Should return PersonalizedCalculationsResponse by Following links from get Products API call ")
    void callApi() throws JsonProcessingException {
        // given
        setHappyPathExpectations();
        setFinalizeCalculationsExpectations(mockServer);
        setCancelSubscriptionExpectations(mockServer);
        ValidateSubscriptionRequest build = ValidateSubscriptionRequest.builder().build();

        // when
        Mono<PersonalizedCalculationsResponse> personalizedCalculationsResponseMono = consorsLoanDemandGateway.callApi(build, loanDemandRequest.getApplicationId());

        // then
        StepVerifier.create(personalizedCalculationsResponseMono).consumeNextWith(personalizedCalculationsResponse -> {
            assertEquals(4, personalizedCalculationsResponse.getFinancialCalculations().getFinancialCalculation().size());
        }).verifyComplete();
    }

    @Test
    @DisplayName("Should exclude unwarranted offers")
    void filterOtherOffers() throws JsonProcessingException {
        // given
        setHappyPathExpectations();
        setFinalizeCalculationsExpectations(mockServer);
        setCancelSubscriptionExpectations(mockServer);
        LoanDemand loanDemand = getLoanDemand();

        // when
        Flux<LoanOffer> cfOffers = this.consorsLoanDemandGateway.getLoanOffers(loanDemand, LoanDuration.FORTY_EIGHT);

        // then
        StepVerifier.create(cfOffers)
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();

    }

    private LoanDemand getLoanDemand() {
        return new LoanDemand(loanDemandRequest.getApplicationId(), loanDemandRequest.getLoanAsked(), "car", LoanDuration.TWENTY_FOUR, LoanCategory.CAR_LOAN, personalDetails, creditDetails, employmentDetails, contactData, digitalAccountStatements, null, null, null, null, null, null, null);
    }

    @Test
    @DisplayName("Should filter offers for yellow case user profile ")
    void callApiYellowProfile() throws JsonProcessingException {
        // given
        setHappyPathExpectations();
        setYellowProfileFinalizeCalculationsExpectations(mockServer);
        setCancelSubscriptionExpectations(mockServer);
        ValidateSubscriptionRequest build = ValidateSubscriptionRequest.builder().build();

        // when
        Mono<PersonalizedCalculationsResponse> personalizedCalculationsResponseMono = consorsLoanDemandGateway.callApi(build, loanDemandRequest.getApplicationId());

        // then
        StepVerifier.create(personalizedCalculationsResponseMono)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("should return true for green profile")
    void greenProfile(@Random ConsorsAcceptOfferResponse consorsAcceptOfferResponse, @Random PersonalizedCalculationsResponse personalizedCalculationsResponse) {
        consorsAcceptOfferResponse.setSubscriptionStatus(SubscriptionStatus.APPROVED);
        consorsAcceptOfferResponse.setSupportingDocumentsRequired(Arrays.asList(30415, 30440, 30651, 30652, 30660));
        Assert.assertTrue(consorsLoanDemandGateway.isGreenProfile(consorsAcceptOfferResponse, UUID.randomUUID()
                .toString(), personalizedCalculationsResponse));
    }

    @Test
    @DisplayName("should return false for yellow profile")
    void yellowProfile(@Random ConsorsAcceptOfferResponse consorsAcceptOfferResponse, @Random PersonalizedCalculationsResponse personalizedCalculationsResponse) {
        consorsAcceptOfferResponse.setSubscriptionStatus(SubscriptionStatus.APPROVED);
        consorsAcceptOfferResponse.setSupportingDocumentsRequired(Arrays.asList(30005));
        Assert.assertFalse(consorsLoanDemandGateway.isGreenProfile(consorsAcceptOfferResponse, UUID.randomUUID()
                .toString(), personalizedCalculationsResponse));
    }


    private void setPersonalizeCalculations5XXErrorExpectations(WireMockServer mockServer) {
        mockServer.stubFor(
                WireMock.put("/consors/ratanet-api/cfg/subscription/freie_verfuegung/744151656e5743456b614730422b7379626a7464356d42736f6f7a38527779327a777a6c6f754d4f7645413d/personalizedcalculations?version=5.0")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        )
        );
    }

    private void setFinalizeCalculations5XXErrorExpectations(WireMockServer mockServer) {
        mockServer.stubFor(
                WireMock.put("/consors/ratanet-api/cfg/subscription/freie_verfuegung/6178796253616d6f6d70505045356c394f4f384562536a6953453667564934672b352f55742b57663836303d/finalizesubscription?version=5.0")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        )
        );
    }

    private void setValidateSubscription5XXErrorExpectations(WireMockServer mockServer) {
        mockServer.stubFor(
                WireMock.post("/consors/ratanet-api/cfg/subscription/freie_verfuegung?version=5.0")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        )
        );
    }

    private void setValidationRulesExpectations5XXError(WireMockServer mockServer) {
        mockServer.stubFor(
                WireMock.get("/consors/ratanet-api/cfg/partner/freie_verfuegung/validationrules?version=5.0")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .withBody("Mock Error Message from validationrules API ")
                        )
        );
    }

    private void setHappyPathExpectations() throws JsonProcessingException {
        setGetTokenExpectations(mockServer);
        setGetProductExpectations(mockServer);
        setValidationRulesExpectations(mockServer);
        setValidateSubscriptionExpectations(mockServer);
        setPersonalizeCalculationsExpectations(mockServer);
    }

    private void setValidateSubscriptionWithouPersonalizedCalculationsLinksExpectations(WireMockServer mockServer) {
        mockServer.stubFor(
                WireMock.post("/consors/ratanet-api/cfg/subscription/freie_verfuegung?version=5.0")
                        .willReturn(aResponse()
                                .withBody(VALIDATE_SUBSCRIPTION_RESPONSE_WITHOUT_LINK)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    private void setValidationRulesExpectationsWithoutValidateSubscriptionLinks(WireMockServer mockServer) {
        mockServer.stubFor(
                WireMock.get("/consors/ratanet-api/cfg/partner/freie_verfuegung/validationrules?version=5.0")
                        .willReturn(aResponse()
                                .withBody(VALIDATION_RULES_RESPONSE_WITHOUT_LINKS)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    private void setGetTokenExpectations(WireMockServer mockServer) throws JsonProcessingException {
        mockServer.stubFor(
                WireMock.post("/consors/common-services/cfg/token/neu_test?version=5.0")
                        .willReturn(aResponse()
                                .withBody(JsonUtil.getObjectAsJsonString(new JwtToken("Bearer eyJhbGciOiJSUzI1NiJ9.eyJkYXRhIjp7InJpZ2h0c0J5Um9sZSI6eyJBUElfSU5JVF9SSUdIVF9TRVQiOlsiQ0FOX0FDQ0VTU19BUEkiXX19LCJqdGkiOiIxMzAuMjNhZDFmOGYtMzljNS00ZjY1LWIxYmQtMjNjODU2NjAzYzE5IiwiZXhwIjoxNTgxMzUwOTUwLCJhdWQiOiJvQXV0aDIiLCJwcm4iOiI2MzczOTUiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQVBJX0lOSVRfUklHSFRfU0VUIl19fQ.f76MIJe6zRbFit8D5y9Pcnbd-qj8d_6zUAUdjPnnu02vT7wyEz5m4NheEi0IFaLg-P9SOPjIEb0RIyQ9ziPTKYRmvOOZXaMy7nRwIZv75tfZ9YOyGHZ3e3xozSL0on30LMnOeX442s3gfVz3qbfhs63_ZZans-MD2QvIPIWg7r-u6vl0tGv8nz6xSyP9AXwSjAfdlg9vUdlfLOWb1EgVRyH4uq9m3pnO4wMPf72mMWy_lFb-FNkYH5Aluf9Zk54Ntwj1YzRIBD2msgPK0Ud0rLBZ36IKW_rM_gIqjJqVD8ghaJjmXvA-eNDXDa8nZ7J9iVCDTlNcZTgjTn5kdlpVtQ")))
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    private void setGetProductExpectations(WireMockServer mockServer) {
        // https://green-2.consorsfinanz.de?version=5.0
        mockServer.stubFor(
                WireMock.get("/consors/ratanet-api/cfg/partner/neu_test/products?version=5.0")
                        .willReturn(aResponse()
                                .withBody(ConsorsFixtures.PRODUCT_RESPONSE)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    private void setGetProductExpectationsWithoutValidationRules(WireMockServer mockServer) {
        // https://green-2.consorsfinanz.de?version=5.0
        mockServer.stubFor(
                WireMock.get("/consors/ratanet-api/cfg/partner/neu_test/products?version=5.0")
                        .willReturn(aResponse()
                                .withBody(PRODUCT_RESPONSE_WITHOUT_VALIDATION_LINKS)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    private void setValidationRulesExpectations(WireMockServer mockServer) {
        // http://localhost:33034/ratanet-api/cfg/partner/freie_verfuegung/validationrules?version=5.0
        mockServer.stubFor(
                WireMock.get("/consors/ratanet-api/cfg/partner/freie_verfuegung/validationrules?version=5.0")
                        .willReturn(aResponse()
                                .withBody(ConsorsFixtures.VALIDATION_RULES_RESPONSE)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    private void setValidateSubscriptionExpectations(WireMockServer mockServer) {
        // http://localhost:33038/ratanet-api/cfg/subscription/freie_verfuegung?version=5.0
        mockServer.stubFor(
                WireMock.post("/consors/ratanet-api/cfg/subscription/freie_verfuegung?version=5.0")
                        .willReturn(aResponse()
                                .withBody(VALIDATE_SUBSCRIPTION_RESPONSE)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    private void setPersonalizeCalculationsExpectations(WireMockServer mockServer) {
        //http://localhost:33044/ratanet-api/cfg/subscription/freie_verfuegung/744151656e5743456b614730422b7379626a7464356d42736f6f7a38527779327a777a6c6f754d4f7645413d/personalizedcalculations?version=5.0
        mockServer.stubFor(
                WireMock.put("/consors/ratanet-api/cfg/subscription/freie_verfuegung/744151656e5743456b614730422b7379626a7464356d42736f6f7a38527779327a777a6c6f754d4f7645413d/personalizedcalculations?version=5.0")
                        .willReturn(aResponse()
                                .withBody(PERSONALIZED_CALCULATION_RESPONSE)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    private void setFinalizeCalculationsExpectations(WireMockServer mockServer) {
        mockServer.stubFor(
                WireMock.put("/consors/ratanet-api/cfg/subscription/freie_verfuegung/6178796253616d6f6d70505045356c394f4f384562536a6953453667564934672b352f55742b57663836303d/finalizesubscription?version=5.0")
                        .willReturn(aResponse()
                                .withBody(FINALIZE_CALCULATION_RESPONSE_APPROVED)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    private void setCancelSubscriptionExpectations(WireMockServer mockServer) {
        mockServer.stubFor(
                WireMock.delete("/consors/ratanet-api/cfg/subscription/3770754a744d4e6a7772446b514250383358643056534e4d4674773232476f6f59355a74506f67576233553d?version=5.0")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    private void setYellowProfileFinalizeCalculationsExpectations(WireMockServer mockServer) {
        mockServer.stubFor(
                WireMock.put("/consors/ratanet-api/cfg/subscription/freie_verfuegung/6178796253616d6f6d70505045356c394f4f384562536a6953453667564934672b352f55742b57663836303d/finalizesubscription?version=5.0")
                        .willReturn(aResponse()
                                .withBody(FINALIZE_CALCULATION_YELLOW_PROFILE_RESPONSE)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    private void setGetProductExpectations5XXError(WireMockServer mockServer) {
        mockServer.stubFor(
                WireMock.get("/consors/ratanet-api/cfg/partner/neu_test/products?version=5.0")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .withBody("Mock Error Message From Get Product API")
                        )
        );
    }

    @Nested
    class ErrorHandling {

        @Test
        @DisplayName("Should return LoanDemandGatewayException If Validation Links are not available")
        void callApiNoValidationLinks() throws JsonProcessingException {
            setGetTokenExpectations(mockServer);
            setGetProductExpectationsWithoutValidationRules(mockServer);
            ValidateSubscriptionRequest build = ValidateSubscriptionRequest.builder()
                    .build();
            Mono<PersonalizedCalculationsResponse> personalizedCalculationsResponseMono = consorsLoanDemandGateway.callApi(build, loanDemandRequest.getApplicationId());
            StepVerifier.create(personalizedCalculationsResponseMono)
                    .verifyErrorMessage("No Suitable Operation available for validation rules");
        }

        @Test
        @DisplayName("Should return LoanDemandGatewayException If _validatesubscription Links are not available in Validation Rules response")
        void callApi_validateSubscriptionLinks() throws JsonProcessingException {
            setGetTokenExpectations(mockServer);
            setGetProductExpectations(mockServer);
            setValidationRulesExpectationsWithoutValidateSubscriptionLinks(mockServer);
            ValidateSubscriptionRequest build = ValidateSubscriptionRequest.builder()
                    .build();
            Mono<PersonalizedCalculationsResponse> personalizedCalculationsResponseMono = consorsLoanDemandGateway.callApi(build, loanDemandRequest.getApplicationId());
            StepVerifier.create(personalizedCalculationsResponseMono)
                    .verifyErrorMessage("No Suitable Operation available validate subscription");
        }

        @Test
        @DisplayName("terminates with error if personalized calculations links are not available in Validation Rules response")
        void callApi_WithoutPersonalizedCalculationsLinks() throws JsonProcessingException {
            setGetTokenExpectations(mockServer);
            setGetProductExpectations(mockServer);
            setValidationRulesExpectations(mockServer);
            setValidateSubscriptionWithouPersonalizedCalculationsLinksExpectations(mockServer);
            ValidateSubscriptionRequest build = ValidateSubscriptionRequest.builder()
                    .build();
            Mono<PersonalizedCalculationsResponse> personalizedCalculationsResponseMono = consorsLoanDemandGateway.callApi(build, loanDemandRequest.getApplicationId());
            StepVerifier.create(personalizedCalculationsResponseMono)
                    .verifyErrorMessage("No Suitable Operation available personalized calculations");
        }

        @Test
        @DisplayName("terminates with error if get products fails")
        void callApi_5XXErrorForGetProducts() throws JsonProcessingException {
            setGetTokenExpectations(mockServer);
            setGetProductExpectations5XXError(mockServer);
            ValidateSubscriptionRequest build = ValidateSubscriptionRequest.builder()
                    .build();
            Mono<PersonalizedCalculationsResponse> personalizedCalculationsResponseMono = consorsLoanDemandGateway.callApi(build, UUID.randomUUID()
                    .toString());
            StepVerifier.create(personalizedCalculationsResponseMono).verifyError();
        }


        @Test
        @DisplayName("terminates with error get validation Rule fails")
        void callApi_5XXErrorForGetValidationRules() throws JsonProcessingException {
            setGetTokenExpectations(mockServer);
            setGetProductExpectations(mockServer);
            setValidationRulesExpectations5XXError(mockServer);

            ValidateSubscriptionRequest build = ValidateSubscriptionRequest.builder()
                    .build();
            Mono<PersonalizedCalculationsResponse> personalizedCalculationsResponseMono = consorsLoanDemandGateway.callApi(build, UUID.randomUUID()
                    .toString());
            StepVerifier.create(personalizedCalculationsResponseMono).verifyError();
        }

        @Test
        @DisplayName("terminates with error if validate subscription fails")
        void callApi_5XXErrorForValidateSubscription() throws JsonProcessingException {
            setGetTokenExpectations(mockServer);
            setGetProductExpectations(mockServer);
            setValidationRulesExpectations(mockServer);
            setValidateSubscription5XXErrorExpectations(mockServer);
            ValidateSubscriptionRequest build = ValidateSubscriptionRequest.builder()
                    .build();
            Mono<PersonalizedCalculationsResponse> personalizedCalculationsResponseMono = consorsLoanDemandGateway.callApi(build, UUID.randomUUID()
                    .toString());
            StepVerifier.create(personalizedCalculationsResponseMono).verifyError();
        }

        @Test
        @DisplayName("terminates with error if personalized calculations fails")
        void callApi_5XXErrorForGetPersonalizedCalculations() throws JsonProcessingException {
            setGetTokenExpectations(mockServer);
            setGetProductExpectations(mockServer);
            setValidationRulesExpectations(mockServer);
            setValidateSubscriptionExpectations(mockServer);
            setPersonalizeCalculations5XXErrorExpectations(mockServer);

            ValidateSubscriptionRequest build = ValidateSubscriptionRequest.builder()
                    .build();
            Mono<PersonalizedCalculationsResponse> personalizedCalculationsResponseMono = consorsLoanDemandGateway.callApi(build, UUID.randomUUID()
                    .toString());
            StepVerifier.create(personalizedCalculationsResponseMono).verifyError();
        }

        @Test
        @DisplayName("terminates with error if finalize calculations fails")
        void callApi_5XXErrorForGetFinalizeCalculations() throws JsonProcessingException {
            setHappyPathExpectations();
            setFinalizeCalculations5XXErrorExpectations(mockServer);
            ValidateSubscriptionRequest build = ValidateSubscriptionRequest.builder()
                    .build();
            Mono<PersonalizedCalculationsResponse> personalizedCalculationsResponseMono = consorsLoanDemandGateway.callApi(build, UUID.randomUUID()
                    .toString()
                    .toString());
            StepVerifier.create(personalizedCalculationsResponseMono).verifyError();
        }

    }
}
