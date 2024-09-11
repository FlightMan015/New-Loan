package de.joonko.loan.partner.santander;

import de.joonko.loan.config.SantanderConfig;
import de.joonko.loan.metric.ApiMetric;
import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import de.joonko.loan.partner.santander.stub.ScbCapsDocsWSStub;
import de.joonko.loan.partner.santander.testData.SantanderClientApiMocks;
import de.joonko.loan.webclient.LocalMockServerRunner;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.*;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SantanderClientApiTest {

    private SantanderClientApi santanderClientApi;

    private ScbCapsBcoWSStub bcoWSStub;
    private ScbCapsDocsWSStub docsWSStub;
    private SantanderConfig santanderConfig;
    private ApiMetric apiMetric;

    private static WebClient webClient;
    private static SantanderClientApiMocks santanderClientApiMocks;
    private static LocalMockServerRunner mockServerRunner;

    @BeforeAll
    static void beforeAll() {
        mockServerRunner = new LocalMockServerRunner();
        santanderClientApiMocks = new SantanderClientApiMocks(mockServerRunner.getServer());
        webClient = mockServerRunner.getWebClient();
    }

    @AfterAll
    static void afterAll() {
        mockServerRunner.stop();
    }

    @BeforeEach
    void setUp() {
        mockServerRunner.resetAll();
        bcoWSStub = mock(ScbCapsBcoWSStub.class);
        docsWSStub = mock(ScbCapsDocsWSStub.class);
        apiMetric = mock(ApiMetric.class);
        santanderConfig = mock(SantanderConfig.class);

        santanderClientApi = new SantanderClientApi(docsWSStub, bcoWSStub, webClient, apiMetric, santanderConfig);
    }

    @SneakyThrows
    @Test
    void getOffer() {
        // given
        ScbCapsBcoWSStub.GetKreditvertragsangebot getKreditvertragsangebot = new ScbCapsBcoWSStub.GetKreditvertragsangebot();
        getKreditvertragsangebot.setGetKreditvertragsangebot(new ScbCapsBcoWSStub.GetKreditvertragsangebotParams());

        // when
        santanderClientApi.getOffer(getKreditvertragsangebot);

        // then
        verify(bcoWSStub).getKreditvertragsangebot(getKreditvertragsangebot);
    }

    @SneakyThrows
    @Test
    void getApplicationStatus() {
        // given
        final var params = new ScbCapsBcoWSStub.GetKreditantragsstatusParams();
        final var request = new ScbCapsBcoWSStub.GetKreditantragsstatus();
        request.setGetKreditantragsstatus(params);
        final var status = new ScbCapsBcoWSStub.KreditantragsstatusXO();
        status.setStatus(ScbCapsBcoWSStub.AntragstatusType.ABGESCHLOSSEN);
        final var result = new ScbCapsBcoWSStub.GetKreditantragsstatusResult();
        result.setAntragsstatus(status);
        final var expected = new ScbCapsBcoWSStub.GetKreditantragsstatusResponse();
        expected.setGetKreditantragsstatusResponse(result);


        // when
        when(santanderConfig.getUsername()).thenReturn("a");
        when(santanderConfig.getPassword()).thenReturn("b");
        when(bcoWSStub.getKreditantragsstatus(any(ScbCapsBcoWSStub.GetKreditantragsstatus.class))).thenReturn(expected);
        final var response = santanderClientApi.getApplicationStatus(params);

        // then
        assertAll(
                () -> verify(bcoWSStub).getKreditantragsstatus(any(ScbCapsBcoWSStub.GetKreditantragsstatus.class)),
                () -> assertEquals(status, response.getAntragsstatus())
        );
    }

    @SneakyThrows
    @Test
    void setDocument() {
        // given
        ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource("data", "application/pdf");

        // when
        santanderClientApi.setDocument(byteArrayDataSource, "");

        // then
        verify(docsWSStub).startsetDocument(any(), any());
    }

    @SneakyThrows
    @Test
    void getContract() {
        // given
        String scbAntragId = "13237702";
        ScbCapsDocsWSStub.GetContractListResponse getContractListResponse = mockContract();
        when(docsWSStub.getContractList(any())).thenReturn(getContractListResponse);

        // when
        santanderClientApi.getContract(scbAntragId, false);

        // then
        verify(docsWSStub).getContractList(any());
    }

    @SneakyThrows
    @Test
    void getContractWithAdvertisingConsent() {
        // given
        String scbAntragId = "13237702";
        ScbCapsDocsWSStub.GetContractListResponse getContractListResponse = mockContract();
        when(docsWSStub.getContractList(any())).thenReturn(getContractListResponse);

        // when
        santanderClientApi.getContract(scbAntragId, true);

        // then
        verify(docsWSStub).getContractList(any());
    }

    @SneakyThrows
    @Test
    void get200WhenCreatingContractEntry() {
        // given
        String scbAntragId = "375633";
        String mdti = "bc_375633";
        String actionId = "13512552";
        santanderClientApiMocks.fake200WhenCreatingContractEntry(scbAntragId, mdti, actionId);

        // when
        var contractCreated = santanderClientApi.createContractEntry(scbAntragId, actionId);

        // then
        assertAll(
                () -> StepVerifier.create(contractCreated)
                        .expectNextCount(1)
                        .verifyComplete(),
                () -> verify(apiMetric).incrementStatusCounter(
                            HttpStatus.OK, ApiComponent.SANTANDER, ApiName.CREATE_CONTRACT)
        );
    }

    @Test
    void get500WhenCreatingContractEntry() {
        // given
        santanderClientApiMocks.fake500WhenCreatingContractEntry();

        // when
        var contractCreated = santanderClientApi.createContractEntry("scbAntragId", "actionId");

        // then
        assertAll(
                () -> StepVerifier.create(contractCreated).verifyError(),
                () -> verify(apiMetric).incrementStatusCounter(
                        HttpStatus.INTERNAL_SERVER_ERROR, ApiComponent.SANTANDER, ApiName.CREATE_CONTRACT)
        );

    }

    @Test
    void get400WhenCreatingContractEntry() {
        // given
        santanderClientApiMocks.fake400WhenCreatingContractEntry();

        // when
        var contractCreated = santanderClientApi.createContractEntry("scbAntragId", "actionId");

        // then
        assertAll(
                () -> StepVerifier.create(contractCreated).verifyError(),
                () -> verify(apiMetric).incrementStatusCounter(
                        HttpStatus.BAD_REQUEST, ApiComponent.SANTANDER, ApiName.CREATE_CONTRACT)
        );

    }

    private ScbCapsDocsWSStub.GetContractListResponse mockContract() {
        final var file = new DataHandler(new DataSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(new byte[]{});
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                return new ByteArrayOutputStream(1);
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }
        });
        final var documentWithAdvertisingConsent = new ScbCapsDocsWSStub.DocumentXO();
        documentWithAdvertisingConsent.setType(ScbCapsDocsWSStub.DocType.CONTRACT_QES_WITH_ADVERTISING_AGREEMENT);
        documentWithAdvertisingConsent.setFile(file);
        final var documentWithoutAdvertisingConsent = new ScbCapsDocsWSStub.DocumentXO();
        documentWithoutAdvertisingConsent.setType(ScbCapsDocsWSStub.DocType.CONTRACT_QES_WITHOUT_ADVERTISING_AGREEMENT);
        documentWithoutAdvertisingConsent.setFile(file);
        final var documents = new ScbCapsDocsWSStub.DocumentXO[2];
        documents[0] = documentWithAdvertisingConsent;
        documents[1] = documentWithoutAdvertisingConsent;
        final var contractListResult = new ScbCapsDocsWSStub.GetContractListResult();
        contractListResult.setContractList(documents);
        ScbCapsDocsWSStub.GetContractListResponse getContractListResponse = new ScbCapsDocsWSStub.GetContractListResponse();
        getContractListResponse.setGetContractListResponse(contractListResult);
        return getContractListResponse;
    }

}
