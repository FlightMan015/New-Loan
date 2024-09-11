package de.joonko.loan.offer.api;

import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.Header;
import org.springframework.http.HttpStatus;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class SolarisMockServerClient {

    private MockServerClient mockServer;

    SolarisMockServerClient(MockServerClient mockServer) {
        this.mockServer = mockServer;
    }

    void fakeSingleOffer() {
        fakeGetAccessToken();
        fakeCreatePerson();
        fakeCreateCreditRecord();
        fakeGetOffer();
        fakeUploadAccountSnapshot();
        fakeGetAccountSnapshot();
        fakeUpdateAccountSnap();
        fakeGetApplicationStatus();

    }

    private void fakeGetAccessToken() {
        mockServer.when(request()
                .withMethod("POST")
                .withPath("/solaris/oauth/token"))
                .respond(response()
                        .withBody(de.joonko.loan.offer.api.SolarisResponses.ACCESS_TOKEN_RESPONSE)
                        .withHeader(new Header("Content-Type", "application/json")));

    }

    private void fakeCreatePerson() {

        mockServer.when(request()
                .withMethod("POST")
                .withHeader("Authorization", "Bearer accessToken")
                .withPath("/solaris/v1/persons"))
                .respond(response()
                        .withBody(de.joonko.loan.offer.api.SolarisResponses.CREATE_PERSON_RESPONSE)
                        .withHeader(new Header("Content-Type", "application/json")));
    }

    private void fakeCreateCreditRecord() {

        mockServer.when(request()
                .withMethod("POST")
                .withHeader("Authorization", "Bearer accessToken")
                .withPath("/solaris/v1/persons/fakePersonId/credit_records"))
                .respond(response()
                        .withBody(de.joonko.loan.offer.api.SolarisResponses.CREATE_CREDIT_RECORD_RESPONSE)
                        .withHeader(new Header("Content-Type", "application/json")));
    }

    private void fakeGetOffer() {

        mockServer.when(request()
                .withMethod("POST")
                .withHeader("Authorization", "Bearer accessToken")
                .withPath("/solaris/v1/persons/8e8aee7d8f3dc19e2e4f877baed0d3dccper/consumer_loan_applications"))
                .respond(response()
                        .withBody(de.joonko.loan.offer.api.SolarisResponses.GET_OFFER_RESPONSE)
                        .withHeader(new Header("Content-Type", "application/json")));
    }

    private void fakeUploadAccountSnapshot() {

        mockServer.when(request()
                .withMethod("POST")
                .withHeader("Authorization", "Bearer accessToken")
                .withPath("/solaris/v1/persons/8e8aee7d8f3dc19e2e4f877baed0d3dccper/account_snapshots"))
                .respond(response()
                        .withBody(de.joonko.loan.offer.api.SolarisResponses.UPLOAD_ACCOUNT_SNAP_RESPONSE)
                        .withHeader(new Header("Content-Type", "application/json")));
    }

    private void fakeUpdateAccountSnap() {

        mockServer.when(request()
                .withMethod("PUT")
                .withHeader("Authorization", "Bearer accessToken")
                .withPath("/solaris/v1/persons/8e8aee7d8f3dc19e2e4f877baed0d3dccper/consumer_loan_applications/2d14c935f4bf401ca3ac8b655b45c2aaclap/account_snapshot"))
                .respond(response()
                        .withStatusCode(HttpStatus.NO_CONTENT.value())
                );
    }

    private void fakeGetApplicationStatus() {

        mockServer.when(request()
                .withMethod("GET")
                .withHeader("Authorization", "Bearer accessToken")
                .withPath("/solaris/v1/persons/8e8aee7d8f3dc19e2e4f877baed0d3dccper/consumer_loan_applications/2d14c935f4bf401ca3ac8b655b45c2aaclap"))
                .respond(response()
                        .withBody(de.joonko.loan.offer.api.SolarisResponses.GET_APPLICATION_STATUS_RESPONSE)
                        .withHeader(new Header("Content-Type", "application/json")));
    }

    private void fakeGetAccountSnapshot() {
        mockServer.when(request()
                .withMethod("GET")
                .withHeader("Authorization", "Basic YXBpOnNvbWVBcGlLZXk=")
                .withPath("/fts/accountSnapshot")
                .withQueryStringParameter("format", "json"))
                .respond(response()
                        .withBody(SolarisResponses.FTS__ACCOUNTSNAPSHOT_RESPONSE)
                        .withHeader(new Header("Content-Type", "application/json")));
    }

    void fakeUnauthorized() {
        mockServer.when(request()
                .withMethod("POST")
                .withPath("/solaris/oauth/token"))
                .respond(response().withStatusCode(401));
    }
}
