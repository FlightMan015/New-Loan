package de.joonko.loan.identification.service.idnow.testdata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.idnow.*;
import de.joonko.loan.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static de.joonko.loan.identification.service.idnow.testdata.IdNowResponses.*;

@AllArgsConstructor
public class IdNowClientApiMocks {

    @Getter
    private final WireMockServer mockServer;

    public IDNowJwtToken getIdNowJwtToken() {
        IDNowJwtToken idNowJwtToken = new IDNowJwtToken();
        idNowJwtToken.setAuthToken("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJkZS5pZG5vdy5nYXRld2F5IiwiYXVkIjoiZGUuaWRub3cuYXBwbGljYXRpb24iLCJleHAiOjE2MTQyMDg3ODAsImp0aSI6InlwYmxKTlZpMVVDS2h1OC1wSU9SLUEiLCJpYXQiOjE2MTQyMDUxODAsIm5iZiI6MTYxNDIwNTA2MCwic3ViIjoiam9vbmtvc3drZXNpZ24iLCJ0eXBlIjoiQ09NUEFOWSIsInBlcm1pc3Npb25zIjoiQUxMIn0.cfJjh43PXPEFGtdeDdeUKxmdWuMxX07rKLVHYrjo_s9bNDo1Dw7apDvBu276izpYaPSEd4z89G9ET2jqjVhULg");

        return idNowJwtToken;
    }

    public void fake200WhenLoginToAccount(String accountId, String apiKey) throws JsonProcessingException {
        mockServer.stubFor(
                WireMock.post("/idnow/api/v1/" + accountId + "/login")
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_JSON.getMimeType()))
                        .withRequestBody(equalTo(JsonUtil.getObjectAsJsonString(IDNowGetTokenRequest.builder()
                                .apiKey(apiKey)
                                .build())))
                        .willReturn(aResponse()
                                .withBody(get200LoginResponse())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.OK.value())
                        )
        );
    }

    public void fake500WhenLoginToAccount(String accountId, String apiKey) throws JsonProcessingException {
        mockServer.stubFor(
                WireMock.post("/idnow/api/v1/" + accountId + "/login")
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_JSON.getMimeType()))
                        .withRequestBody(equalTo(JsonUtil.getObjectAsJsonString(IDNowGetTokenRequest.builder()
                                .apiKey(apiKey)
                                .build())))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        )
        );
    }

    public void fake200WhenGettingListOfDocumentDefinitions(String accountId, String authToken) {
        mockServer.stubFor(
                WireMock.get("/idnow/api/v1/" + accountId + "/documentdefinitions")
                        .withHeader("X-API-LOGIN-TOKEN", matching(authToken))
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_JSON.getMimeType()))
                        .willReturn(aResponse()
                                .withBody(get200ListOfDocumentsResponse())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.OK.value())
                        )
        );
    }

    public void fake401WhenGettingListOfDocumentDefinitions(String accountId, String authToken) {
        mockServer.stubFor(
                WireMock.get("/idnow/api/v1/" + accountId + "/documentdefinitions")
                        .withHeader("X-API-LOGIN-TOKEN", matching(authToken))
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_JSON.getMimeType()))
                        .willReturn(aResponse()
                                .withBody(get401Response())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.UNAUTHORIZED.value())
                        )
        );
    }

    public void fake201WhenCreatingDocumentDefinition(String accountId, String authToken, DocumentDefinition documentDefinition) throws JsonProcessingException {
        mockServer.stubFor(
                WireMock.post("/idnow/api/v1/" + accountId + "/documentdefinitions")
                        .withHeader("X-API-LOGIN-TOKEN", matching(authToken))
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_JSON.getMimeType()))
                        .withRequestBody(equalTo(JsonUtil.getObjectAsJsonString(documentDefinition)))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.CREATED.value())
                        )
        );
    }

    public void fake412WhenCreatingDocumentDefinition(String accountId, String authToken, DocumentDefinition documentDefinition) throws JsonProcessingException {
        mockServer.stubFor(
                WireMock.post("/idnow/api/v1/" + accountId + "/documentdefinitions")
                        .withHeader("X-API-LOGIN-TOKEN", matching(authToken))
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_JSON.getMimeType()))
                        .withRequestBody(equalTo(JsonUtil.getObjectAsJsonString(documentDefinition)))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.PRECONDITION_FAILED.value())
                        )
        );
    }

    public void fake200WhenGettingIdent(String accountId, String authToken, String transactionId) {
        mockServer.stubFor(
                WireMock.get("/idnow/api/v1/" + accountId + "/identifications/" + transactionId)
                        .withHeader("X-API-LOGIN-TOKEN", matching(authToken))
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_JSON.getMimeType()))
                        .willReturn(aResponse()
                                .withBody(get200IdentResponse())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.OK.value())
                        )
        );
    }

    public void fake404WhenGettingIdent(String accountId, String authToken, String transactionId) {
        mockServer.stubFor(
                WireMock.get("/idnow/api/v1/" + accountId + "/identifications/" + transactionId)
                        .withHeader("X-API-LOGIN-TOKEN", matching(authToken))
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_JSON.getMimeType()))
                        .willReturn(aResponse()
                                .withBody(get404IdentResponse())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.NOT_FOUND.value())
                        )
        );
    }

    public void fake401WhenGettingIdent(String accountId, String authToken, String transactionId) {
        mockServer.stubFor(
                WireMock.get("/idnow/api/v1/" + accountId + "/identifications/" + transactionId)
                        .withHeader("X-API-LOGIN-TOKEN", matching(authToken))
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_JSON.getMimeType()))
                        .willReturn(aResponse()
                                .withBody(get401Response())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.UNAUTHORIZED.value())
                        )
        );
    }

    public void fake500WhenGettingIdent(String accountId, String authToken, String transactionId) {
        mockServer.stubFor(
                WireMock.get("/idnow/api/v1/" + accountId + "/identifications/" + transactionId)
                        .withHeader("X-API-LOGIN-TOKEN", matching(authToken))
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_JSON.getMimeType()))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        )
        );
    }

    public void fake201WhenCreatingIdent(String accountId, String authToken, String transactionId, CreateIdentRequest createIdentRequest) throws JsonProcessingException {
        mockServer.stubFor(
                WireMock.post("/idnow/api/v1/" + accountId + "/identifications/" + transactionId + "/start")
                        .withHeader("X-API-LOGIN-TOKEN", matching(authToken))
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_JSON.getMimeType()))
                        .withRequestBody(equalTo(JsonUtil.getObjectAsJsonString(createIdentRequest)))
                        .willReturn(aResponse()
                                .withBody(get201CreatingIdentResponse())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.CREATED.value())
                        )
        );
    }

    public void fake401WhenCreatingIdent(String accountId, String authToken, String transactionId, CreateIdentRequest createIdentRequest) throws JsonProcessingException {
        mockServer.stubFor(
                WireMock.post("/idnow/api/v1/" + accountId + "/identifications/" + transactionId + "/start")
                        .withHeader("X-API-LOGIN-TOKEN", matching(authToken))
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_JSON.getMimeType()))
                        .withRequestBody(equalTo(JsonUtil.getObjectAsJsonString(createIdentRequest)))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.UNAUTHORIZED.value())
                        )
        );
    }

    public void fake200WhenUploadingDocument(String accountId, String authToken, String transactionId, Document document) {
        mockServer.stubFor(
                WireMock.post("/idnow/api/v1/" + accountId + "/identifications/" + transactionId + "/documents/" + document.getDocumentId() + "/data")
                        .withHeader("X-API-LOGIN-TOKEN", matching(authToken))
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_OCTET_STREAM.getMimeType()))
                        .withRequestBody(binaryEqualTo(document.getContent()))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.OK.value())
                        )
        );
    }

    public void fake401WhenUploadingDocument(String accountId, String authToken, String transactionId, Document document) {
        mockServer.stubFor(
                WireMock.post("/idnow/api/v1/" + accountId + "/identifications/" + transactionId + "/documents/" + document.getDocumentId() + "/data")
                        .withHeader("X-API-LOGIN-TOKEN", matching(authToken))
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_OCTET_STREAM.getMimeType()))
                        .withRequestBody(binaryEqualTo(document.getContent()))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.UNAUTHORIZED.value())
                        )
        );
    }

}
