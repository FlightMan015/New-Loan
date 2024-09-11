package de.joonko.loan.partner.consors;

import de.joonko.loan.common.partner.consors.auth.JwtToken;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.filter.LogResponseFilter;
import de.joonko.loan.metric.ApiMetric;
import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;
import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferRequest;
import de.joonko.loan.partner.consors.model.LinkRelation;
import de.joonko.loan.partner.consors.model.SubscriptionStatus;
import de.joonko.loan.partner.consors.model.ValidateSubscriptionRequest;
import de.joonko.loan.partner.consors.testData.ConsorsClientMocks;
import de.joonko.loan.webclient.LocalMockServerRunner;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ConsorsClientTest {

    private ConsorsClient consorsClient;

    private ConsorsPropertiesConfig consorsPropertiesConfig;
    private ApiMetric apiMetric;
    private LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private LogResponseFilter logResponseFilter;

    private static WebClient webClient;
    private static LocalMockServerRunner mockServerRunner;
    private static ConsorsClientMocks consorsClientMocks;
    private static final ExchangeFilterFunction filter = ExchangeFilterFunction.ofResponseProcessor(Mono::just);

    @BeforeAll
    static void beforeAll() {
        mockServerRunner = new LocalMockServerRunner();
        consorsClientMocks = new ConsorsClientMocks(mockServerRunner.getServer());
        webClient = mockServerRunner.getWebClient("consors");
    }

    @AfterAll
    static void afterAll() {
        mockServerRunner.stop();
    }

    @BeforeEach
    void beforeEach() {
        mockServerRunner.resetAll();

        consorsPropertiesConfig = mock(ConsorsPropertiesConfig.class);
        apiMetric = mock(ApiMetric.class);
        loanApplicationAuditTrailService = mock(LoanApplicationAuditTrailService.class);
        logResponseFilter = mock(LogResponseFilter.class);
        consorsClient = new ConsorsClient(webClient, consorsPropertiesConfig, apiMetric, loanApplicationAuditTrailService, logResponseFilter);

        when(consorsPropertiesConfig.getRatanet()).thenReturn("/ratanet-api/cfg");
        when(consorsPropertiesConfig.getProductEndpoint()).thenReturn("/partner/neu_test/products");
        when(consorsPropertiesConfig.getTokenUri()).thenReturn(uriBuilder -> uriBuilder.path("/common-services/cfg/token/neu_test")
                .queryParam("version", "5.0").build());
        when(consorsPropertiesConfig.buildProductUri()).thenReturn(uriBuilder -> uriBuilder.path("/ratanet-api/cfg/partner/neu_test/products")
                .queryParam("version", "5.0").build());
        when(consorsPropertiesConfig.getUsername()).thenReturn("someUser");
        when(consorsPropertiesConfig.getPassword()).thenReturn("somePassword");
    }

    @Test
    void getToken() {
        // given
        final var applicationId = "27738730hf39";
        consorsClientMocks.setGetTokenExpectations();

        // when
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.CONSORS, ApiName.AUTHORIZATION)).thenReturn(filter);

        var actualToken = consorsClient.getToken(applicationId);

        // then
        assertAll(
                () -> StepVerifier.create(actualToken).consumeNextWith(token -> assertEquals("jwtToken", token.getToken())).verifyComplete(),
                () -> verifyNoInteractions(loanApplicationAuditTrailService)
        );
    }

    @Test
    void getProducts() {
        // given
        consorsClientMocks.setGetProductsExpectations();

        // when
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.CONSORS, ApiName.GET_PRODUCTS)).thenReturn(filter);
        var actualProducts = consorsClient.getProducts(new JwtToken("jwtToken"), "applicationId");

        // then
        assertAll(
                () -> StepVerifier.create(actualProducts).expectSubscription().expectNextCount(1).verifyComplete()
        );

    }

    @Test
    void validateRules() {
        // given
        consorsClientMocks.setValidationRulesExpectations();
        Link link = new Link("http://localhost:" + mockServerRunner.getServer().port(), "/consors/ratanet-api/cfg/partner/freie_verfuegung/validationrules?version=5.0", "GET");

        // when
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.CONSORS, ApiName.VALIDATE_RULES)).thenReturn(filter);
        var actualValidations = consorsClient.validateRules(new JwtToken("jwtToken"), link, "applicationId");

        // then
        assertAll(
                () -> StepVerifier.create(actualValidations).expectSubscription().expectNextCount(1).verifyComplete()
        );

    }

    @Test
    void validateSubscriptions() {
        // given
        consorsClientMocks.setValidateSubscriptionExpectations();
        Link link = new Link("http://localhost:" + mockServerRunner.getServer().port(), "/consors/ratanet-api/cfg/subscription/freie_verfuegung?version=5.0", "POST");

        // when
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.CONSORS, ApiName.VALIDATE_SUBSCRIPTION)).thenReturn(filter);
        var actualValidations = consorsClient.validateSubscription(new JwtToken("jwtToken"), link, new ValidateSubscriptionRequest(), "applicationId");

        // then
        assertAll(
                () -> StepVerifier.create(actualValidations)
                        .expectSubscription()
                        .expectNextCount(1)
                        .verifyComplete()
        );

    }

    @Test
    void getPersonalizedCalculations() {
        // given
        consorsClientMocks.setPersonalizeCalculationsExpectations();
        Link link = new Link("http://localhost:" + mockServerRunner.getServer().port(), "/consors/ratanet-api/cfg/subscription/freie_verfuegung/744151656e5743456b614730422b7379626a7464356d42736f6f7a38527779327a777a6c6f754d4f7645413d/personalizedcalculations?version=5.0", "PUT");

        // when
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.CONSORS, ApiName.GET_PERSONALIZED_CALCULATIONS)).thenReturn(filter);
        var personalizedCalculations = consorsClient.getPersonalizedCalculations(new JwtToken("jwtToken"), link, "applicationId");

        // then
        assertAll(
                () -> StepVerifier.create(personalizedCalculations)
                        .expectSubscription()
                        .expectNextCount(1)
                        .verifyComplete()
        );

    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void finalizeSubscription(SubscriptionStatus subscriptionStatus) {
        // given
        consorsClientMocks.setFinalizeCalculationsExpectations(subscriptionStatus);
        LinkRelation link = new LinkRelation("finalize", "/subscription/freie_verfuegung/6178796253616d6f6d70505045356c394f4f384562536a6953453667564934672b352f55742b57663836303d/finalizesubscription?version=5.0", "PUT", null);
        when(consorsPropertiesConfig.buildUriFromUrlLink(anyString()))
                .thenReturn(uriBuilder -> uriBuilder.path("/ratanet-api/cfg/subscription/freie_verfuegung/6178796253616d6f6d70505045356c394f4f384562536a6953453667564934672b352f55742b57663836303d/finalizesubscription")
                        .queryParam("version", "5.0").build());
        // when
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.CONSORS, ApiName.FINALIZE_SUBSCRIPTION)).thenReturn(filter);
        var finalizeSubscription = consorsClient.finalizeSubscription(new JwtToken("jwtToken"), link, new ConsorsAcceptOfferRequest(null, 1), "applicationId");

        // then
        assertAll(
                () -> StepVerifier.create(finalizeSubscription)
                        .expectSubscription()
                        .expectNextCount(1)
                        .verifyComplete()

        );

    }

    @Test
    void cancelSubscription() {
        // given
        consorsClientMocks.setCancelSubscriptionExpectations();
        de.joonko.loan.partner.consors.model.Link link = new de.joonko.loan.partner.consors.model.Link();
        link.setMethod("DELETE");
        link.setHref("/subscription/freie_verfuegung/6178796253616d6f6d70505045356c394f4f384562536a6953453667564934672b352f55742b57663836303d?version=5.0");
        when(consorsPropertiesConfig.buildUriFromUrlLink(anyString()))
                .thenReturn(uriBuilder -> uriBuilder.path("/ratanet-api/cfg/subscription/freie_verfuegung/6178796253616d6f6d70505045356c394f4f384562536a6953453667564934672b352f55742b57663836303d")
                        .queryParam("version", "5.0").build());

        // when
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.CONSORS, ApiName.CANCEL_SUBSCRIPTION)).thenReturn(filter);
        var cancelSubscription = consorsClient.cancelSubscription(new JwtToken("jwtToken"), link, "applicationId");

        // then
        assertAll(
                () -> StepVerifier.create(cancelSubscription)
                        .expectSubscription()
                        .expectNextCount(1)
                        .verifyComplete()
        );
    }

    @Test
    void getContract() {
        // given
        consorsClientMocks.setGetContractExpectations();
        final var url = "/subscription/freie_verfuegung/6178796253616d6f6d70505045356c394f4f384562536a6953453667564934672b352f55742b57663836303d/documents?version=5.0";
        when(consorsPropertiesConfig.buildUriFromUrlLink(anyString()))
                .thenReturn(uriBuilder -> uriBuilder.path("/ratanet-api/cfg/subscription/freie_verfuegung/6178796253616d6f6d70505045356c394f4f384562536a6953453667564934672b352f55742b57663836303d/documents")
                        .queryParam("version", "5.0").build());

        // when
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.CONSORS, ApiName.GET_CONTRACT)).thenReturn(filter);
        var actualContract = consorsClient.getContract(new JwtToken("jwtToken"), url, "applicationId");

        // then
        assertAll(
                () -> StepVerifier.create(actualContract).expectNextCount(1).verifyComplete()
        );
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(SubscriptionStatus.APPROVED),
                Arguments.of(SubscriptionStatus.STUDY),
                Arguments.of(SubscriptionStatus.REFUSED)
        );
    }
}
