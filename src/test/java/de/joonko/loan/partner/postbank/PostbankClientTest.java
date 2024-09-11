package de.joonko.loan.partner.postbank;

import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.filter.LogResponseFilter;
import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;
import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequest;
import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequestContract;
import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequestCredit;
import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequestSoapBody;
import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequestSoapEnvelope;
import de.joonko.loan.webclient.LocalMockServerRunner;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostbankClientTest {

    private PostbankClient postbankClient;
    private PostbankPropertiesConfig postbankPropertiesConfig;
    private LoanApplicationAuditTrailService loanApplicationAuditTrailService;

    private static WebClient webClient;
    private static LocalMockServerRunner mockServerRunner;
    private static PostbankClientMocks postbankClientMocks;
    private LogResponseFilter logResponseFilter;

    @BeforeAll
    static void beforeAll() {
        mockServerRunner = new LocalMockServerRunner();
        postbankClientMocks = new PostbankClientMocks(mockServerRunner.getServer());

        webClient = mockServerRunner.getWebClient("postbank");
    }

    @BeforeEach
    void setUp() {
        mockServerRunner.resetAll();
        postbankPropertiesConfig = mock(PostbankPropertiesConfig.class);
        loanApplicationAuditTrailService = mock(LoanApplicationAuditTrailService.class);
        logResponseFilter = mock(LogResponseFilter.class);

        postbankClient = new PostbankClient(webClient, postbankPropertiesConfig, loanApplicationAuditTrailService, logResponseFilter);
    }

    @AfterAll
    static void afterAll() {
        mockServerRunner.stop();
    }


    @Test
    void get200WhenAskingForLoan() {
        // given
        final var filter = ExchangeFilterFunction.ofResponseProcessor(Mono::just);
        final var applicationId = "620526ec10f9eb41caa0f594";
        final var request = LoanDemandPostbankRequestSoapEnvelope.builder()
                .body(LoanDemandPostbankRequestSoapBody.builder()
                        .contract(LoanDemandPostbankRequestContract.builder()
                                .credit(LoanDemandPostbankRequestCredit.builder()
                                        .request(LoanDemandPostbankRequest.builder()
                                                .applicationId(applicationId)
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();

        // when
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.POSTBANK, ApiName.APPLY_FOR_LOAN)).thenReturn(filter);
        postbankClientMocks.fake200WhenAskingForLoan(applicationId);
        when(postbankPropertiesConfig.getLoanDemandUri()).thenReturn(uriBuilder -> uriBuilder.path("/kreditantrag").build());
        var response = postbankClient.requestLoanOffers(request, applicationId);

        // then
        assertAll(
                () -> StepVerifier.create(response)
                        .consumeNextWith(res -> assertAll(
                                () -> assertEquals(applicationId, res.getApplicationId()),
                                () -> assertEquals("5743578", res.getContractNumber()),
                                () -> assertEquals(1, res.getStatus().getState()),
                                () -> assertEquals(2, res.getStatus().getMessages().size()),
                                () -> assertNull(res.getStatus().getError()),
                                () -> assertEquals("Auszahlungsdatum wurde korrigiert!", res.getStatus().getMessages().get(0).getMessage()),
                                () -> assertEquals("dac-info: Verarbeitung der DAC-Dateien abgeschlossen.", res.getStatus().getMessages().get(1).getMessage())
                        )).verifyComplete()
        );
    }

    @Test
    void get400WhenAskingForLoan() {
        // given
        final var filter = ExchangeFilterFunction.ofResponseProcessor(Mono::just);
        final var applicationId = "123";
        final var request = LoanDemandPostbankRequestSoapEnvelope.builder().build();

        // when
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.POSTBANK, ApiName.APPLY_FOR_LOAN)).thenReturn(filter);
        postbankClientMocks.fake400WhenAskingForLoan();
        when(postbankPropertiesConfig.getLoanDemandUri()).thenReturn(uriBuilder -> uriBuilder.path("/kreditantrag").build());
        var response = postbankClient.requestLoanOffers(request, applicationId);

        // then
        assertAll(
                () -> StepVerifier.create(response).verifyError()
        );
    }
}