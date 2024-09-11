package de.joonko.loan.identification;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.AllArgsConstructor;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

@AllArgsConstructor
class IDNowMockServerClient {

    private final WireMockServer mockServer;

    public void fakeGetToken(String accountId) {
        mockServer.stubFor(
                WireMock.post("/idnow/api/v1/" + accountId + "/login")
                        .willReturn(aResponse()
                                .withBody(IDNowResponses.GET_TOKEN_RESPONSE)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }


    public void fakeCreateIdent(String id, String accountId) {
        mockServer.stubFor(
                WireMock.post("/idnow/api/v1/" + accountId + "/identifications/" + id + "/start")
                        .willReturn(aResponse()
                                .withBody(IDNowResponses.CREATE_IDENT_RESPONSE)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    public void fakeCreateIdentFailure(String id, String accountId) {
        mockServer.stubFor(
                WireMock.post("/idnow/api/v1/" + accountId + "/identifications/" + id + "/start")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        )
        );
    }

    public void fakeUploadContract(String id, String accountId) {
        mockServer.stubFor(
                WireMock.post("/idnow/api/v1/joonkoswkauxmoneyesign/identifications/" + id + "/documents/contract/data")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    public void fakeUploadContractAuxmoney(String id) {
        mockServer.stubFor(
                WireMock.post("/idnow/api/v1/joonkoswkauxmoneyesign/identifications/" + id + "/documents/contract/data")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    public void fakeUploadContractConsors(String id) {
        mockServer.stubFor(
                WireMock.post("/idnow/api/v1/joonkocfgesign/identifications/" + id + "/documents/contract/data")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    public void fakeGetStatus(String accountId, String id) {
        mockServer.stubFor(
                WireMock.get("/idnow/api/v1/" + accountId + "/identifications/" + id)
                        .willReturn(aResponse()
                                .withBody(IDNowResponses.GET_IDENTIFICATION_STATUS_RESPONSE)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }
}
