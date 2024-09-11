package de.joonko.loan.offer.api;

import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.Header;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class ConsorsMockServerClient {

    private MockServerClient mockServer;

    ConsorsMockServerClient(MockServerClient mockServer) {

        this.mockServer = mockServer;
    }

    void fakeSingleOffer() {
        fakeGetToken();
        fakeGetProducts();
        fakeValidationRules();
        fakeValidateSubscription();
        fakePersonalizedCalculations();
        fakeGreenProfileFinalizeCalculationsExpectations();
    }

    private void fakeGetToken() {
        mockServer.when(request()
                .withMethod("POST")
                .withPath("/consors/common-services/cfg/token/neu_test")
                .withQueryStringParameter("version", "5.0")
                .withBody("username=someUser&password=somePassword"))
                .respond(response()
                        .withBody(ConsorsResponses.TOKEN_RESPONSE)
                        .withHeader(new Header("Content-Type", "application/json")));

    }

    private void fakeGetProducts() {
        mockServer.when(request()
                .withMethod("GET")
                .withPath("/consors/ratanet-api/cfg/partner/neu_test/products")
                .withQueryStringParameter("version", "5.0"))
                .respond(response()
                        .withBody(ConsorsResponses.PRODUCT_RESPONSE)
                        .withHeader(new Header("Content-Type", "application/json")));
    }

    private void fakeValidationRules() {
        mockServer.when(request()
                .withMethod("GET")
                .withPath("/consors/ratanet-api/cfg/partner/freie_verfuegung/validationrules")
                .withQueryStringParameter("version", "5.0"))
                .respond(response()
                        .withBody(ConsorsResponses.VALIDATION_RULES_RESPONSE)
                        .withHeader(new Header("Content-Type", "application/json")));
    }

    private void fakeValidateSubscription() {
        mockServer.when(request()
                .withMethod("POST")
                .withPath("/consors/ratanet-api/cfg/subscription/freie_verfuegung")
                .withQueryStringParameter("version", "5.0"))
                .respond(response()
                        .withBody(ConsorsResponses.VALIDATE_SUBSCRIPTION_RESPONSE)
                        .withHeader(new Header("Content-Type", "application/json")));
    }


    private void fakePersonalizedCalculations() {
        mockServer.when(request()
                .withMethod("PUT")
                .withPath("/consors/ratanet-api/cfg/subscription/freie_verfuegung/744151656e5743456b614730422b7379626a7464356d42736f6f7a38527779327a777a6c6f754d4f7645413d/personalizedcalculations")
                .withQueryStringParameter("version", "5.0"))
                .respond(response()
                        .withBody(ConsorsResponses.PERSONALIZED_CALCULATION_SINGLE_OFFER_RESPONSE)
                        .withHeader(new Header("Content-Type", "application/json")));
    }

    private void fakeGreenProfileFinalizeCalculationsExpectations() {
        mockServer.when(request().withMethod("PUT")
                .withQueryStringParameter("version", "5.0")
                .withPath("/consors/ratanet-api/cfg/subscription/freie_verfuegung/6178796253616d6f6d70505045356c394f4f384562536a6953453667564934672b352f55742b57663836303d/finalizesubscription"))
                .respond(response()
                        .withBody(ConsorsResponses.FINALIZED_CALCULATION_GREEN_OFFER_RESPONSE)
                        .withHeader(new Header("Content-Type", "application/json")));
    }

    void fakeUnauthorized() {
        mockServer.when(request()
                .withMethod("POST")
                .withPath("/consors/common-services/cfg/token/neu_test")
                .withQueryStringParameter("version", "5.0"))
                .respond(response().withStatusCode(401));
    }
}
