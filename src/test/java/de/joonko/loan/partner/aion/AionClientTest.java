package de.joonko.loan.partner.aion;

import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.filter.LogResponseFilter;
import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;
import de.joonko.loan.partner.aion.model.AionAuthToken;
import de.joonko.loan.partner.aion.model.TransmissionDataType;
import de.joonko.loan.partner.aion.model.offerchoice.OfferChoiceRequest;
import de.joonko.loan.partner.aion.model.offerchoice.OfferChoiceTransmissionData;
import de.joonko.loan.webclient.LocalMockServerRunner;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AionClientTest {

    private AionClient aionClient;
    private AionPropertiesConfig aionPropertiesConfig;
    private static AionClientMapper aionClientMapper;
    private LoanApplicationAuditTrailService loanApplicationAuditTrailService;

    private static WebClient webClient;
    private static LocalMockServerRunner mockServerRunner;
    private static AionClientMocks aionClientMocks;

    private LogResponseFilter logResponseFilter;

    @BeforeAll
    static void beforeAll() {
        aionClientMapper = new AionClientMapper();

        mockServerRunner = new LocalMockServerRunner();
        aionClientMocks = new AionClientMocks(mockServerRunner.getServer());
        webClient = mockServerRunner.getWebClient("aion");
    }

    @BeforeEach
    void setUp() {
        mockServerRunner.resetAll();
        aionPropertiesConfig = mock(AionPropertiesConfig.class);
        loanApplicationAuditTrailService = mock(LoanApplicationAuditTrailService.class);
        logResponseFilter = mock(LogResponseFilter.class);
        aionClient = new AionClient(webClient, aionPropertiesConfig, aionClientMapper, loanApplicationAuditTrailService, logResponseFilter);
    }

    @AfterAll
    static void afterAll() {
        mockServerRunner.stop();
    }

    @Test
    void get200WhenAuthUser() {
        // given
        final var filter = ExchangeFilterFunction.ofResponseProcessor(Mono::just);
        aionClientMocks.fake200WhenAuth();
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.AUTHORIZATION)).thenReturn(filter);
        when(aionPropertiesConfig.getTokenUri()).thenReturn(uriBuilder -> uriBuilder.path("/someBrandId/oauth2/token").build());
        when(aionPropertiesConfig.getAuthClientId()).thenReturn("someClientId");
        when(aionPropertiesConfig.getAuthClientSecret()).thenReturn("someClientSecret");
        when(aionPropertiesConfig.getAuthAudience()).thenReturn("someAuthAudience");


        // when
        var monoAuthToken = aionClient.getToken("8h3928hg");

        // then
        assertAll(
                () -> StepVerifier.create(monoAuthToken).expectNextCount(1).verifyComplete()
        );
    }

    @Test
    void get200WhenSendingOfferChoice() {
        // given
        final var filter = ExchangeFilterFunction.ofResponseProcessor(Mono::just);

        var request = OfferChoiceRequest.builder()
                .name(TransmissionDataType.SELECTED_OFFER)
                .value(OfferChoiceTransmissionData.builder()
                        .selectedOfferId("cb6659be-7e3f-4902-a3b4-da78ec9ef8c3")
                        .timestamp(LocalDateTime.now())
                        .build())
                .build();
        var processId = "db207bb1-9a57-4eb6-9f9b-08e04e52d924";
        var authToken = AionAuthToken.builder()
                .token("token")
                .build();
        aionClientMocks.fake200WhenSendingOfferChoice(authToken.getToken(), processId);
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.AUTHORIZATION)).thenReturn(filter);
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.SEND_OFFER_CHOICE)).thenReturn(filter);

        when(aionPropertiesConfig.getOfferChoiceUri(processId)).thenReturn(uriBuilder -> uriBuilder.path("/someBrandId/credits-channel-app/api/1/processes/" + processId).build());

        // when
        var monoOfferChoice = aionClient.sendOfferChoice(authToken, processId, request);

        // then
        assertAll(
                () -> StepVerifier.create(monoOfferChoice)
                        .consumeNextWith(offerChoice -> assertAll(
                                () -> assertEquals(processId, offerChoice.getProcessId()),
                                () -> assertEquals("9b3cfec4-b9cd-4bb7-8c71-66645c985bca", offerChoice.getRepresentativeId()),
                                () -> assertEquals("agreement", offerChoice.getDraftAgreement().get(0).getFileId()),
                                () -> assertEquals("agreementbase64", offerChoice.getDraftAgreement().get(0).getFileContent()),
                                () -> assertEquals("DRAFT_LOAN_AGREEMENT.pdf", offerChoice.getDraftAgreement().get(0).getFileName()),
                                () -> assertEquals("schedule", offerChoice.getDraftAgreement().get(1).getFileId()),
                                () -> assertEquals("schedulebase64", offerChoice.getDraftAgreement().get(1).getFileContent()),
                                () -> assertEquals("DRAFT_SCHEDULE_FOR_LOAN.pdf", offerChoice.getDraftAgreement().get(1).getFileName()),
                                () -> assertEquals("secci", offerChoice.getDraftAgreement().get(2).getFileId()),
                                () -> assertEquals("seccibase64", offerChoice.getDraftAgreement().get(2).getFileContent()),
                                () -> assertEquals("SECCI_FORM.pdf", offerChoice.getDraftAgreement().get(2).getFileName())
                        )).verifyComplete()
        );
    }

    @Test
    void get400WhenSendingOfferChoice() {
        // given
        final var filter = ExchangeFilterFunction.ofResponseProcessor(Mono::just);
        var request = OfferChoiceRequest.builder()
                .name(TransmissionDataType.SELECTED_OFFER)
                .value(OfferChoiceTransmissionData.builder()
                        .selectedOfferId("cb6659be-7e3f-4902-a3b4-da78ec9ef8c3")
                        .timestamp(LocalDateTime.now())
                        .build())
                .build();
        var processId = "db207bb1-9a57-4eb6-9f9b-08e04e52d924";
        var authToken = AionAuthToken.builder()
                .token("token")
                .build();
        aionClientMocks.fake400WhenSendingOfferChoice(authToken.getToken(), processId);
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.AUTHORIZATION)).thenReturn(filter);
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.SEND_OFFER_CHOICE)).thenReturn(filter);

        when(aionPropertiesConfig.getOfferChoiceUri(processId)).thenReturn(uriBuilder -> uriBuilder.path("/someBrandId/credits-channel-app/api/1/processes/" + processId).build());

        // when
        var monoOfferChoice = aionClient.sendOfferChoice(authToken, processId, request);

        // then
        assertAll(
                () -> StepVerifier.create(monoOfferChoice).verifyError()
        );
    }

    @Test
    void get401WhenSendingOfferChoice() {
        // given
        final var filter = ExchangeFilterFunction.ofResponseProcessor(Mono::just);

        var request = OfferChoiceRequest.builder()
                .name(TransmissionDataType.SELECTED_OFFER)
                .value(OfferChoiceTransmissionData.builder()
                        .selectedOfferId("cb6659be-7e3f-4902-a3b4-da78ec9ef8c3")
                        .timestamp(LocalDateTime.now())
                        .build())
                .build();
        var processId = "db207bb1-9a57-4eb6-9f9b-08e04e52d924";
        var authToken = AionAuthToken.builder()
                .token("token")
                .build();
        aionClientMocks.fake401WhenSendingOfferChoice(authToken.getToken(), processId);
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.AUTHORIZATION)).thenReturn(filter);
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.SEND_OFFER_CHOICE)).thenReturn(filter);

        when(aionPropertiesConfig.getOfferChoiceUri(processId)).thenReturn(uriBuilder -> uriBuilder.path("/someBrandId/credits-channel-app/api/1/processes/" + processId).build());

        // when
        var monoOfferChoice = aionClient.sendOfferChoice(authToken, processId, request);

        // then
        assertAll(
                () -> StepVerifier.create(monoOfferChoice).verifyError()
        );
    }

    @Test
    void get200WhenGettingOfferStatus() {
        // given
        final var filter = ExchangeFilterFunction.ofResponseProcessor(Mono::just);

        var processId = "db207bb1-9a57-4eb6-9f9b-08e04e52d924";
        var authToken = AionAuthToken.builder()
                .token("token")
                .build();
        aionClientMocks.fake200WhenGettingOfferStatus(authToken.getToken(), processId);
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.AUTHORIZATION)).thenReturn(filter);
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.GET_OFFER_STATUS)).thenReturn(filter);

        when(aionPropertiesConfig.getOfferStatusUri(processId)).thenReturn(uriBuilder -> uriBuilder.path("/someBrandId/credits-channel-app/api/1/processes/" + processId).build());

        // when
        var monoOfferChoice = aionClient.getOfferStatus(authToken, processId);

        // then
        assertAll(
                () -> StepVerifier.create(monoOfferChoice)
                        .consumeNextWith(offerChoice -> assertAll(
                                () -> assertEquals(processId, offerChoice.getProcessId()),
                                () -> assertEquals("9b3cfec4-b9cd-4bb7-8c71-66645c985bca", offerChoice.getRepresentativeId()),
                                () -> assertEquals("agreement", offerChoice.getDraftAgreement().get(0).getFileId()),
                                () -> assertEquals("agreementbase64", offerChoice.getDraftAgreement().get(0).getFileContent()),
                                () -> assertEquals("DRAFT_LOAN_AGREEMENT.pdf", offerChoice.getDraftAgreement().get(0).getFileName()),
                                () -> assertEquals("schedule", offerChoice.getDraftAgreement().get(1).getFileId()),
                                () -> assertEquals("schedulebase64", offerChoice.getDraftAgreement().get(1).getFileContent()),
                                () -> assertEquals("DRAFT_SCHEDULE_FOR_LOAN.pdf", offerChoice.getDraftAgreement().get(1).getFileName()),
                                () -> assertEquals("secci", offerChoice.getDraftAgreement().get(2).getFileId()),
                                () -> assertEquals("seccibase64", offerChoice.getDraftAgreement().get(2).getFileContent()),
                                () -> assertEquals("SECCI_FORM.pdf", offerChoice.getDraftAgreement().get(2).getFileName())
                        )).verifyComplete()
        );
    }

    @Test
    void get400WhenGettingOfferStatus() {
        // given
        final var filter = ExchangeFilterFunction.ofResponseProcessor(Mono::just);

        var processId = "db207bb1-9a57-4eb6-9f9b-08e04e52d924";
        var authToken = AionAuthToken.builder()
                .token("token")
                .build();
        aionClientMocks.fake400WhenGettingOfferStatus(authToken.getToken(), processId);
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.AUTHORIZATION)).thenReturn(filter);
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.GET_OFFER_STATUS)).thenReturn(filter);

        when(aionPropertiesConfig.getOfferStatusUri(processId)).thenReturn(uriBuilder -> uriBuilder.path("/someBrandId/credits-channel-app/api/1/processes/" + processId).build());

        // when
        var monoOfferChoice = aionClient.getOfferStatus(authToken, processId);

        // then
        assertAll(
                () -> StepVerifier.create(monoOfferChoice).verifyError()
        );
    }

    @Test
    void get401WhenGettingOfferStatus() {
        // given
        final var filter = ExchangeFilterFunction.ofResponseProcessor(Mono::just);

        var processId = "db207bb1-9a57-4eb6-9f9b-08e04e52d924";
        var authToken = AionAuthToken.builder()
                .token("token")
                .build();
        aionClientMocks.fake401WhenGettingOfferStatus(authToken.getToken(), processId);
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.AUTHORIZATION)).thenReturn(filter);
        when(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.GET_OFFER_STATUS)).thenReturn(filter);

        when(aionPropertiesConfig.getOfferStatusUri(processId)).thenReturn(uriBuilder -> uriBuilder.path("/someBrandId/credits-channel-app/api/1/processes/" + processId).build());

        // when
        var monoOfferChoice = aionClient.getOfferStatus(authToken, processId);

        // then
        assertAll(
                () -> StepVerifier.create(monoOfferChoice).verifyError()
        );
    }

    // todo: what is the response for not existing offer? is it 404?

    // todo: get 404 when getting offer for not existing offer
}
