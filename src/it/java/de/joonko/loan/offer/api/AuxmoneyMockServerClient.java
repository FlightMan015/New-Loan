package de.joonko.loan.offer.api;

import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.Header;
import org.mockserver.model.HttpResponse;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class AuxmoneyMockServerClient {


    private MockServerClient mockServer;

    AuxmoneyMockServerClient(MockServerClient mockServer) {

        this.mockServer = mockServer;
    }

    void fakeSingleOffer() {
        fakeGetOffers(AuxmoneyResponses.SINGLE_OFFER_CALL);
    }

    private void fakeGetOffers(String json) {
        mockServer.when(request().withMethod("POST")
                .withHeader("urlkey", "somekey")
                .withPath("/auxmoney/distributionline/api/rest/partnerendpoints"))
                .respond(response()
                        .withBody(json)
                        .withHeader(new Header("Content-Type", "application/json")));
    }

    void fakeBadRequest() {
        mockServer.when(request().withMethod("POST")
                .withHeader("urlkey", "somekey")
                .withPath("/auxmoney/distributionline/api/rest/partnerendpoints"))
                .respond(HttpResponse.response().withBody("This is a fake error for testing").withStatusCode(400));
    }
}
