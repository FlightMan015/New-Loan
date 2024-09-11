package de.joonko.loan.identification.service.idnow;

import de.joonko.loan.identification.model.idnow.CreateIdentRequest;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.idnow.DocumentDefinition;
import de.joonko.loan.identification.model.idnow.IdNowAccount;
import de.joonko.loan.identification.service.idnow.testdata.IdNowClientApiMocks;
import de.joonko.loan.metric.ApiMetric;
import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;
import de.joonko.loan.webclient.LocalMockServerRunner;

import lombok.SneakyThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class IdNowClientApiTest {

    private IdNowClientApi idNowClientApi;

    private static IdNowAccountMapper idNowAccountMapper;
    private static WebClient webClient;
    private ApiMetric apiMetric;

    private static IdNowClientApiMocks idNowClientApiMocks;
    private static LocalMockServerRunner mockServerRunner;

    private static final String TRANSACTION_ID = "123";
    private static final String DOCUMENT_ID = "123hf9328fho";
    private static final String FAKE_ID_NOW_ACCOUNT = "fake-idnow-account";
    private static final String FAKE_ID_NOW_API_KEY = "fake-idnow-api-key";

    @BeforeAll
    static void beforeAll() {
        mockServerRunner = new LocalMockServerRunner();
        idNowClientApiMocks = new IdNowClientApiMocks(mockServerRunner.getServer());
        webClient = mockServerRunner.getWebClient("idnow");
    }

    @BeforeEach
    void setUp() {
        mockServerRunner.resetAll();
        idNowAccountMapper = mock(IdNowAccountMapper.class);
        apiMetric = mock(ApiMetric.class);
        idNowClientApi = new IdNowClientApi(webClient, idNowAccountMapper, apiMetric);

        when(idNowAccountMapper.getAccountId(any(IdNowAccount.class))).thenReturn(FAKE_ID_NOW_ACCOUNT);
    }

    @AfterAll
    static void afterAll() {
        mockServerRunner.stop();
    }

    @SneakyThrows
    @Test
    void get200WhenLoginToAccount() {
        // given
        when(idNowAccountMapper.getApiKey(IdNowAccount.SWK)).thenReturn(FAKE_ID_NOW_API_KEY);
        idNowClientApiMocks.fake200WhenLoginToAccount(FAKE_ID_NOW_ACCOUNT, FAKE_ID_NOW_API_KEY);

        // when
        var authResponseMono = idNowClientApi.getJwtToken(IdNowAccount.SWK);

        // then
        assertAll(
                () -> StepVerifier.create(authResponseMono).expectNextCount(1).verifyComplete(),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.OK, ApiComponent.ID_NOW, ApiName.AUTHORIZATION)
        );
    }

    @SneakyThrows
    @Test
    void get500WhenLoginToAccount() {
        // given
        when(idNowAccountMapper.getApiKey(IdNowAccount.SWK)).thenReturn(FAKE_ID_NOW_API_KEY);
        idNowClientApiMocks.fake500WhenLoginToAccount(FAKE_ID_NOW_ACCOUNT, FAKE_ID_NOW_API_KEY);

        // when
        var authResponseMono = idNowClientApi.getJwtToken(IdNowAccount.SWK);

        // then
        assertAll(
                () -> StepVerifier.create(authResponseMono).verifyError(),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.INTERNAL_SERVER_ERROR, ApiComponent.ID_NOW, ApiName.AUTHORIZATION)
        );
    }

    @Test
    void get200WhenGettingListOfDocumentDefinitions() {
        // given
        var authToken = idNowClientApiMocks.getIdNowJwtToken().getAuthToken();
        idNowClientApiMocks.fake200WhenGettingListOfDocumentDefinitions(FAKE_ID_NOW_ACCOUNT, authToken);

        // when
        var listOfDocumentsResponseMono = idNowClientApi.getDocumentDefinitions(IdNowAccount.AION, authToken);

        // then
        assertAll(
                () -> StepVerifier.create(listOfDocumentsResponseMono).consumeNextWith(listOfDocuments -> assertAll(
                        () -> assertEquals("agreement", listOfDocuments[0].getIdentifier()),
                        () -> assertEquals("schedule", listOfDocuments[1].getIdentifier()),
                        () -> assertEquals("secci", listOfDocuments[2].getIdentifier())
                )).verifyComplete(),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.OK, ApiComponent.ID_NOW, ApiName.GET_DOCUMENT_DEFINITIONS)
        );
    }

    @Test
    void get401WhenGettingListOfDocumentDefinitions() {
        // given
        var authToken = idNowClientApiMocks.getIdNowJwtToken().getAuthToken();
        idNowClientApiMocks.fake401WhenGettingListOfDocumentDefinitions(FAKE_ID_NOW_ACCOUNT, authToken);

        // when
        var listOfDocumentsResponseMono = idNowClientApi.getDocumentDefinitions(IdNowAccount.AION, authToken);

        // then
        assertAll(
                () -> StepVerifier.create(listOfDocumentsResponseMono).verifyError(),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.UNAUTHORIZED, ApiComponent.ID_NOW, ApiName.GET_DOCUMENT_DEFINITIONS)
        );
    }

    @SneakyThrows
    @Test
    void get201WhenCreatingDocumentDefinition() {
        // given
        var authToken = idNowClientApiMocks.getIdNowJwtToken().getAuthToken();
        var documentDefinition = DocumentDefinition.builder().build();
        idNowClientApiMocks.fake201WhenCreatingDocumentDefinition(FAKE_ID_NOW_ACCOUNT, authToken, documentDefinition);

        // when
        var createDocumentResponseMono = idNowClientApi.createDocumentDefinition(IdNowAccount.AION, authToken, documentDefinition);

        // then
        assertAll(
                () -> StepVerifier.create(createDocumentResponseMono).expectNextCount(0).verifyComplete(),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.CREATED, ApiComponent.ID_NOW, ApiName.CREATE_DOCUMENT_DEFINITION)
        );
    }

    @SneakyThrows
    @Test
    void get412WhenCreatingDocumentDefinition() {
        // given
        var authToken = idNowClientApiMocks.getIdNowJwtToken().getAuthToken();
        var documentDefinition = DocumentDefinition.builder().build();
        idNowClientApiMocks.fake412WhenCreatingDocumentDefinition(FAKE_ID_NOW_ACCOUNT, authToken, documentDefinition);

        // when
        var createDocumentResponseMono = idNowClientApi.createDocumentDefinition(IdNowAccount.AION, authToken, documentDefinition);

        // then
        assertAll(
                () -> StepVerifier.create(createDocumentResponseMono).verifyError(),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.PRECONDITION_FAILED, ApiComponent.ID_NOW, ApiName.CREATE_DOCUMENT_DEFINITION)
        );
    }

    @Test
    void get200WhenGettingIdent() {
        // given
        String authToken = idNowClientApiMocks.getIdNowJwtToken().getAuthToken();
        idNowClientApiMocks.fake200WhenGettingIdent(FAKE_ID_NOW_ACCOUNT, authToken, TRANSACTION_ID);

        // when
        var identResponseMono = idNowClientApi.getIdent(IdNowAccount.SWK, authToken, TRANSACTION_ID);

        // then
        assertAll(
                () -> StepVerifier.create(identResponseMono).expectNextCount(1).verifyComplete(),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.OK, ApiComponent.ID_NOW, ApiName.GET_IDENT)
        );
    }

    @Test
    void get404WhenGettingIdent() {
        // given
        String authToken = idNowClientApiMocks.getIdNowJwtToken().getAuthToken();
        idNowClientApiMocks.fake404WhenGettingIdent(FAKE_ID_NOW_ACCOUNT, authToken, TRANSACTION_ID);

        // when
        var identResponseMono = idNowClientApi.getIdent(IdNowAccount.SWK, authToken, TRANSACTION_ID);

        // then
        assertAll(
                () -> StepVerifier.create(identResponseMono)
                        .verifyErrorMessage("404 Not Found from " + "GET http://localhost:" +
                                mockServerRunner.getServer().port() + "/idnow/api/v1/" + FAKE_ID_NOW_ACCOUNT +
                                "/identifications/" + TRANSACTION_ID),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.NOT_FOUND, ApiComponent.ID_NOW, ApiName.GET_IDENT)
        );
    }

    @Test
    void get401WhenGettingIdent() {
        // given
        String authToken = idNowClientApiMocks.getIdNowJwtToken().getAuthToken();
        idNowClientApiMocks.fake401WhenGettingIdent(FAKE_ID_NOW_ACCOUNT, authToken, TRANSACTION_ID);

        // when
        var identResponseMono = idNowClientApi.getIdent(IdNowAccount.SWK, authToken, TRANSACTION_ID);

        // then
        assertAll(
                () -> StepVerifier.create(identResponseMono)
                        .verifyErrorMessage("401 Unauthorized from " +
                                "GET http://localhost:" + mockServerRunner.getServer().port() +
                                "/idnow/api/v1/" + FAKE_ID_NOW_ACCOUNT + "/identifications/" + TRANSACTION_ID),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.UNAUTHORIZED, ApiComponent.ID_NOW, ApiName.GET_IDENT)
        );
    }

    @Test
    void get500WhenGettingIdent() {
        // given
        String authToken = idNowClientApiMocks.getIdNowJwtToken().getAuthToken();
        idNowClientApiMocks.fake500WhenGettingIdent(FAKE_ID_NOW_ACCOUNT, authToken, TRANSACTION_ID);

        // when
        var identResponseMono = idNowClientApi.getIdent(IdNowAccount.SWK, authToken, TRANSACTION_ID);

        // then
        assertAll(
                () -> StepVerifier.create(identResponseMono)
                        .verifyErrorMessage("500 Internal Server Error from " + "GET http://localhost:" +
                                mockServerRunner.getServer().port() + "/idnow/api/v1/" +
                                FAKE_ID_NOW_ACCOUNT + "/identifications/" + TRANSACTION_ID),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.INTERNAL_SERVER_ERROR, ApiComponent.ID_NOW, ApiName.GET_IDENT)
        );
    }

    @SneakyThrows
    @Test
    void get201WhenCreatingIdent() {
        // given
        String authToken = idNowClientApiMocks.getIdNowJwtToken().getAuthToken();
        var createIdentRequest = new CreateIdentRequest();
        idNowClientApiMocks.fake201WhenCreatingIdent(FAKE_ID_NOW_ACCOUNT, authToken, TRANSACTION_ID, createIdentRequest);

        // when
        var identResponseMono = idNowClientApi.createIdent(IdNowAccount.SWK, authToken, TRANSACTION_ID, createIdentRequest);

        // then
        assertAll(
                () -> StepVerifier.create(identResponseMono).expectNextCount(1).verifyComplete(),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.CREATED, ApiComponent.ID_NOW, ApiName.CREATE_IDENT)
        );
    }

    @SneakyThrows
    @Test
    void get401WhenCreatingIdent() {
        // given
        String authToken = idNowClientApiMocks.getIdNowJwtToken().getAuthToken();
        var createIdentRequest = new CreateIdentRequest();
        idNowClientApiMocks.fake401WhenCreatingIdent(FAKE_ID_NOW_ACCOUNT, authToken, TRANSACTION_ID, createIdentRequest);

        // when
        var identResponseMono = idNowClientApi.createIdent(IdNowAccount.SWK, authToken, TRANSACTION_ID, createIdentRequest);

        // then
        assertAll(
                () -> StepVerifier.create(identResponseMono).verifyError(),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.UNAUTHORIZED, ApiComponent.ID_NOW, ApiName.CREATE_IDENT)
        );
    }

    @Test
    void get200WhenUploadingDocument() {
        // given
        var authToken = idNowClientApiMocks.getIdNowJwtToken().getAuthToken();
        var document = Document.builder()
                .content(new byte[]{})
                .documentId(DOCUMENT_ID)
                .build();
        idNowClientApiMocks.fake200WhenUploadingDocument(FAKE_ID_NOW_ACCOUNT, authToken, TRANSACTION_ID, document);

        // when
        var documentUploadedMono = idNowClientApi.uploadDocument(IdNowAccount.AION, authToken, TRANSACTION_ID, document);

        // then
        assertAll(
                () -> StepVerifier.create(documentUploadedMono).expectNextMatches(
                        doc -> DOCUMENT_ID.equals(doc.getDocumentId()) &&
                                doc.getContent().length == 0
                ).verifyComplete(),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.OK, ApiComponent.ID_NOW, ApiName.UPLOAD_DOCUMENT)
        );
    }

    @Test
    void get401WhenUploadingDocument() {
        // given
        var authToken = idNowClientApiMocks.getIdNowJwtToken().getAuthToken();
        var document = Document.builder()
                .content(new byte[]{})
                .documentId(DOCUMENT_ID)
                .build();
        idNowClientApiMocks.fake401WhenUploadingDocument(FAKE_ID_NOW_ACCOUNT, authToken, TRANSACTION_ID, document);

        // when
        var documentUploadedMono = idNowClientApi.uploadDocument(IdNowAccount.AION, authToken, TRANSACTION_ID, document);

        // then
        assertAll(
                () -> StepVerifier.create(documentUploadedMono).verifyError(),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.UNAUTHORIZED, ApiComponent.ID_NOW, ApiName.UPLOAD_DOCUMENT)
        );
    }
}
