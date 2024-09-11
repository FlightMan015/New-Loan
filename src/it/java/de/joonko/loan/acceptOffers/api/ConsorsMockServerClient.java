package de.joonko.loan.acceptOffers.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.http.entity.ContentType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

class ConsorsMockServerClient {

    public static void fakeGetToken(WireMockServer mockServer) {
        mockServer.stubFor(
                WireMock.post("/consors/common-services/cfg/token/neu_test?version=5.0")
                        .withRequestBody(equalTo("username=someUser&password=somePassword"))
                        .willReturn(aResponse()
                                .withBody(ConsorsResponse.TOKEN_RESPONSE)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())));
    }

    public static void fakeGetApprovedOffer(WireMockServer mockServer) {
        mockServer.stubFor(
                WireMock.put("/consors/ratanet-api/cfg/subscription/freie_verfuegung/6178796253616d6f6d70505045356c394f4f384562536a6953453667564934672b352f55742b57663836303d/finalizesubscription?version=5.0")
                        .willReturn(aResponse()
                                .withBody(ConsorsResponse.OFFER_ACCPETED)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())));
    }
}
