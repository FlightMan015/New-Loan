package de.joonko.loan.offer.api;

import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.Header;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;


class SantanderMockServerClient {

    private MockServerClient mockServer;

    SantanderMockServerClient(MockServerClient mockServer) {
        this.mockServer = mockServer;
    }

    void fakeGetOffers() {
        fakeGetSingleOffer();
        fakeUploadDocument();

    }

    private void fakeGetSingleOffer() {
        mockServer.when(request()
                .withMethod("POST")
                .withPath("/bco/services/ScbCapsBcoWS")
                .withHeader(new Header("Content-Type", "text/xml"))
                .withHeader(new Header("SOAPAction", "")))
                .respond(response()
                        .withBody( SantanderResponses.SINGLE_OFFER_RESPONSE)
                        );

    }

    private void fakeUploadDocument() {
        mockServer.when(request()
                .withMethod("POST")
                .withPath("/caps-docs/services/ScbCapsDocsWS")
                .withHeader(new Header("Content-Type", "text/xml"))
                .withHeader(new Header("SOAPAction", "")))
                .respond(response()
                        .withBody(SantanderResponses.UPLOAD_DOCUMENT_RESPONSE));

    }
}
