package de.joonko.loan.identification;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.AllArgsConstructor;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

@AllArgsConstructor
class AuxmoneyMockServerClient {

    private final WireMockServer mockServer;

    public void fakeGetContract() {
        //https://acc.auxacc.de/distributionline/api/rest/partnerendpoints/contracts
        mockServer.stubFor(
                WireMock.post("/auxmoney/distributionline/api/rest/partnerendpoints/contracts")
                        .willReturn(aResponse()
                                .withBody(AuxmoneyResponses.GET_CONTRACT_RESPONSE)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    public void fakePushNotification() {
        mockServer.stubFor(
                WireMock.post("/auxmoney/partner/esign/joonko/webhook/init")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                        )
        );
    }

    public void fakeUploadContractFailure(String id) {
        mockServer.stubFor(
                WireMock.post("/idnow/api/v1/joonkoswkauxmoneyesign/identifications/" + id + "/documents/contract/data")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }
}
