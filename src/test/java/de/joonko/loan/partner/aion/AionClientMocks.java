package de.joonko.loan.partner.aion;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static de.joonko.loan.partner.aion.testdata.AionResponses.*;

@AllArgsConstructor
public class AionClientMocks {

    @Getter
    private final WireMockServer mockServer;

    public void fake200WhenSendingOfferChoice(String authToken, String processId) {
        mockServer.stubFor(
                WireMock.put("/aion/someBrandId/credits-channel-app/api/1/processes/" + processId)
                        .withHeader("X-Token", equalTo(authToken))
                        .willReturn(aResponse()
                                .withBody(get200OfferChoice())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.OK.value())
                        )
        );
    }

    public void fake400WhenSendingOfferChoice(String authToken, String processId) {
        mockServer.stubFor(
                WireMock.put("/aion/someBrandId/credits-channel-app/api/1/processes/" + processId)
                        .withHeader("X-Token", equalTo(authToken))
                        .willReturn(aResponse()
                                .withBody(get400OfferChoice())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.BAD_REQUEST.value())
                        )
        );
    }

    public void fake401WhenSendingOfferChoice(String authToken, String processId) {
        mockServer.stubFor(
                WireMock.put("/aion/someBrandId/credits-channel-app/api/1/processes/" + processId)
                        .withHeader("X-Token", equalTo(authToken))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.UNAUTHORIZED.value())
                        )
        );
    }

    public void fake200WhenGettingOfferStatus(String authToken, String processId) {
        mockServer.stubFor(
                WireMock.get("/aion/someBrandId/credits-channel-app/api/1/processes/" + processId)
                        .withHeader("X-Token", equalTo(authToken))
                        .willReturn(aResponse()
                                .withBody(get200OfferChoice())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.OK.value())
                        )
        );
    }

    public void fake400WhenGettingOfferStatus(String authToken, String processId) {
        mockServer.stubFor(
                WireMock.get("/aion/someBrandId/credits-channel-app/api/1/processes/" + processId)
                        .withHeader("X-Token", equalTo(authToken))
                        .willReturn(aResponse()
                                .withBody(get400OfferChoice())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.BAD_REQUEST.value())
                        )
        );
    }

    public void fake401WhenGettingOfferStatus(String authToken, String processId) {
        mockServer.stubFor(
                WireMock.get("/aion/someBrandId/credits-channel-app/api/1/processes/" + processId)
                        .withHeader("X-Token", equalTo(authToken))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.UNAUTHORIZED.value())
                        )
        );
    }

    public void fake200WhenAuth() {
        mockServer.stubFor(
                WireMock.post("/aion/someBrandId/oauth2/token")
                        .withRequestBody(equalTo("client_id=someClientId&client_secret=someClientSecret&grant_type=client_credentials&audience=someAuthAudience"))
                        .willReturn(aResponse()
                                .withBody(get200Auth())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }
}
