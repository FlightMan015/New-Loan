package de.joonko.loan.identification;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import de.joonko.loan.acceptOffers.api.ConsorsResponse;

import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

@AllArgsConstructor
class ConsorsMockServerClient {

    private final WireMockServer mockServer;

    public void fakeGetContract() {
        //https://acc.auxacc.de/distributionline/api/rest/partnerendpoints/contracts
        mockServer.stubFor(
                WireMock.get("/consors/ratanet-api/cfg/subscription/457561707845636568686972596164345161584577776671495554784533444353627153714b3254426c733d/documents?version=5.0")
                        .willReturn(aResponse()
                                .withBody("This is test pdf dummy contract byte stream".getBytes())
                                .withStatus(HttpStatus.OK.value())
                        )
        );
    }

    public void fakeGetToken() {
        mockServer.stubFor(
                WireMock.post("/consors/common-services/cfg/token/neu_test?version=5.0")
                        .withRequestBody(equalTo("username=someUser&password=somePassword"))
                        .willReturn(aResponse()
                                .withBody(ConsorsResponse.TOKEN_RESPONSE)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
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

    public void fakeUploadContractFailureConsors(String id) {
        mockServer.stubFor(
                WireMock.post("/idnow/api/v1/joonkocfgesign/identifications/" + id + "/documents/contract/data")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }
}
