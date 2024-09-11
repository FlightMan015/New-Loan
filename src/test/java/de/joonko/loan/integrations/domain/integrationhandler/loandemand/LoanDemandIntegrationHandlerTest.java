package de.joonko.loan.integrations.domain.integrationhandler.loandemand;

import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.integrations.domain.integrationhandler.IntegrationHandler;
import de.joonko.loan.integrations.domain.integrationhandler.testData.LoanDemandIntegrationHandlerTestData;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.integrations.model.UserState;
import de.joonko.loan.metric.LoanDemandMetric;
import de.joonko.loan.metric.OffersStateMetric;
import de.joonko.loan.offer.domain.LoanDemandGateway;
import de.joonko.loan.offer.domain.LoanDemandGatewayFilter;
import de.joonko.loan.user.states.UserStateService;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDemandService;
import de.joonko.loan.partner.fake.FakeLoanDemandGateway;
import de.joonko.loan.user.states.UserStatesStore;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(RandomBeansExtension.class)
class LoanDemandIntegrationHandlerTest {

    private IntegrationHandler integrationHandler;

    private LoanDemandIntegrationHandlerFilter handlerFilter;
    private LoanDemandProviderService loanDemandProviderService;
    private LoanDemandService loanDemandService;
    private LoanOfferStoreService loanOfferStoreService;
    private UserStateService userStateService;
    private OffersStateMetric offersStateMetric;
    private LoanDemandMetric loanDemandMetric;
    private LoanDemandGatewayFilter loanDemandGatewayFilter;

    private LoanDemandIntegrationHandlerTestData testData;
    private FakeLoanDemandGateway fakeLoanDemandGateway;
    private static final String APPLICATION_ID = "5f561054e3fa3d3d1a7ef3f4";
    private static final String RECOMMENDED_APPLICATION_ID = "j903fh2j03jc32038r023";

    @BeforeEach
    void setUp() {
        testData = new LoanDemandIntegrationHandlerTestData();

        handlerFilter = new LoanDemandIntegrationHandlerFilter();
        loanDemandService = mock(LoanDemandService.class);
        loanOfferStoreService = mock(LoanOfferStoreService.class);
        loanDemandProviderService = mock(LoanDemandProviderService.class);
        userStateService = mock(UserStateService.class);
        offersStateMetric = mock(OffersStateMetric.class);
        loanDemandMetric = mock(LoanDemandMetric.class);
        fakeLoanDemandGateway = mock(FakeLoanDemandGateway.class);
        loanDemandGatewayFilter = mock(LoanDemandGatewayFilter.class);

        integrationHandler = new LoanDemandIntegrationHandler(handlerFilter, loanDemandProviderService, loanDemandService,
                loanOfferStoreService, userStateService, offersStateMetric, loanDemandMetric, loanDemandGatewayFilter);
    }

    @Test
    void doNotTriggerGettingOffersWhenNotValidState(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder().build());

        // when
        var triggered = integrationHandler.triggerMutation(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(triggered).expectNextCount(0).verifyComplete(),
                () -> verifyNoInteractions(loanDemandProviderService)
        );

    }

    @Test
    void getErrorWhenFailedGettingLoanDemand(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(testData.getValidUserState());

        when(loanDemandProviderService.getLoanDemandFromOfferRequest(offerRequest)).thenReturn(Mono.error(new IllegalStateException("Failed building loan demand request")));
        when(userStateService.addOffersStates(eq(offerRequest.getUserUUID()), anySet())).thenReturn(Mono.just(new UserStatesStore()));
        // when
        var triggered = integrationHandler.triggerMutation(offerRequest);

        // then
        StepVerifier.create(triggered).verifyError();
    }

    @Test
    void getOffersForSingleLoanDemand(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(testData.getValidUserState());
        LoanDemand loanDemand = testData.getLoanDemand(APPLICATION_ID, offerRequest.getUserUUID(), null);
        final Set<LoanDemandGateway> gateways = Set.of(fakeLoanDemandGateway);

        when(loanDemandProviderService.getLoanDemandFromOfferRequest(offerRequest)).thenReturn(Mono.just(Set.of(loanDemand)));
        when(loanDemandGatewayFilter.filterValidGatewaysForLoanDemand(loanDemand)).thenReturn(gateways);
        when(loanDemandProviderService.savePrechecksToRequestAndPublish(loanDemand)).thenReturn(Mono.just(loanDemand));
        when(loanDemandService.getLoanOffersForProviders(eq(loanDemand), anySet())).thenReturn(testData.getLoanOffers());
        when(userStateService.addOffersStates(eq(offerRequest.getUserUUID()), any())).thenReturn(Mono.just(testData.getUserStatesStore(APPLICATION_ID, offerRequest.getRequestedAmount())));
        when(userStateService.saveUpdatedOffersStates(eq(offerRequest.getUserUUID()), any())).thenReturn(Mono.just(testData.getUserStatesStore(APPLICATION_ID, offerRequest.getRequestedAmount())));
        when(loanOfferStoreService.saveAll(any(), any(), any(), any())).thenReturn(Flux.empty());

        // when
        var triggered = integrationHandler.triggerMutation(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(triggered).expectNextCount(0).verifyComplete(),
                () -> verify(loanDemandProviderService).getLoanDemandFromOfferRequest(offerRequest),
                () -> verify(userStateService).addOffersStates(any(), any()),
                () -> verify(loanDemandGatewayFilter).filterValidGatewaysForLoanDemand(loanDemand),
                () -> verify(loanDemandProviderService).savePrechecksToRequestAndPublish(loanDemand),
                () -> verify(loanDemandService).getLoanOffersForProviders(eq(loanDemand), anySet()),
                () -> verify(loanOfferStoreService).saveAll(any(), any(), any(), any()),
                () -> verify(offersStateMetric).addOffersStateTimer(eq(offerRequest.getUserUUID()), any(OffsetDateTime.class)),
                () -> verify(loanDemandMetric).incrementCounterForEachLoanProvider(anyString(), eq(false)),
                () -> verify(userStateService).saveUpdatedOffersStates(eq(offerRequest.getUserUUID()), any())
        );
    }

    @Test
    void getOffersForMultipleLoanDemands(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(testData.getValidUserState());
        LoanDemand loanDemand = testData.getLoanDemand(APPLICATION_ID, offerRequest.getUserUUID(), null);
        LoanDemand recommendedLoanDemand = testData.getLoanDemand(RECOMMENDED_APPLICATION_ID, offerRequest.getUserUUID(), APPLICATION_ID);
        final Set<LoanDemandGateway> gateways = Set.of(fakeLoanDemandGateway);

        when(loanDemandProviderService.getLoanDemandFromOfferRequest(offerRequest)).thenReturn(Mono.just(Set.of(loanDemand, recommendedLoanDemand)));
        when(loanDemandGatewayFilter.filterValidGatewaysForLoanDemand(any(LoanDemand.class))).thenReturn(gateways);
        when(loanDemandProviderService.savePrechecksToRequestAndPublish(any(LoanDemand.class))).thenReturn(Mono.just(loanDemand));
        when(loanDemandService.getLoanOffersForProviders(any(LoanDemand.class), anySet())).thenReturn(testData.getLoanOffers());
        when(userStateService.addOffersStates(eq(offerRequest.getUserUUID()), any())).thenReturn(Mono.just(testData.getUserStatesStore(APPLICATION_ID, offerRequest.getRequestedAmount(), RECOMMENDED_APPLICATION_ID)));
        when(userStateService.saveUpdatedOffersStates(eq(offerRequest.getUserUUID()), any())).thenReturn(Mono.just(testData.getUserStatesStore(APPLICATION_ID, offerRequest.getRequestedAmount(), RECOMMENDED_APPLICATION_ID)));
        when(loanOfferStoreService.saveAll(any(), any(), any(), any())).thenReturn(Flux.empty());

        // when
        var triggered = integrationHandler.triggerMutation(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(triggered).expectNextCount(0).verifyComplete(),
                () -> verify(loanDemandProviderService).getLoanDemandFromOfferRequest(offerRequest),
                () -> verify(userStateService).addOffersStates(any(), any()),
                () -> verify(loanDemandGatewayFilter).filterValidGatewaysForLoanDemand(loanDemand),
                () -> verify(loanDemandProviderService, times(2)).savePrechecksToRequestAndPublish(any()),
                () -> verify(loanDemandService, times(2)).getLoanOffersForProviders(any(LoanDemand.class), anySet()),
                () -> verify(loanOfferStoreService, times(2)).saveAll(any(), any(), any(), any()),
                () -> verify(offersStateMetric, times(2)).addOffersStateTimer(eq(offerRequest.getUserUUID()), any(OffsetDateTime.class)),
                () -> verify(loanDemandMetric, times(2)).incrementCounterForEachLoanProvider(anyString(), anyBoolean()),
                () -> verify(userStateService).saveUpdatedOffersStates(eq(offerRequest.getUserUUID()), any())
        );
    }

    @Test
    void gettingZeroOffersShouldStillUpdateOffersState(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(testData.getValidUserState());
        LoanDemand loanDemand = testData.getLoanDemand(APPLICATION_ID, offerRequest.getUserUUID(), null);
        final Set<LoanDemandGateway> gateways = Set.of(fakeLoanDemandGateway);

        when(loanDemandProviderService.getLoanDemandFromOfferRequest(offerRequest)).thenReturn(Mono.just(Set.of(loanDemand)));
        when(loanDemandGatewayFilter.filterValidGatewaysForLoanDemand(loanDemand)).thenReturn(gateways);
        when(loanDemandProviderService.savePrechecksToRequestAndPublish(loanDemand)).thenReturn(Mono.just(loanDemand));
        when(loanDemandService.getLoanOffersForProviders(eq(loanDemand), anySet())).thenReturn(Flux.empty());
        when(userStateService.addOffersStates(eq(offerRequest.getUserUUID()), any())).thenReturn(Mono.just(testData.getUserStatesStore(APPLICATION_ID, offerRequest.getRequestedAmount())));
        when(userStateService.saveUpdatedOffersStates(eq(offerRequest.getUserUUID()), any())).thenReturn(Mono.just(testData.getUserStatesStore(APPLICATION_ID, offerRequest.getRequestedAmount())));

        // when
        var triggered = integrationHandler.triggerMutation(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(triggered).expectNextCount(0).verifyComplete(),
                () -> verify(loanDemandProviderService).getLoanDemandFromOfferRequest(offerRequest),
                () -> verify(userStateService).addOffersStates(any(), any()),
                () -> verify(loanDemandGatewayFilter).filterValidGatewaysForLoanDemand(loanDemand),
                () -> verify(loanDemandProviderService).savePrechecksToRequestAndPublish(loanDemand),
                () -> verify(loanDemandService).getLoanOffersForProviders(eq(loanDemand), anySet()),
                () -> verifyNoInteractions(loanOfferStoreService),
                () -> verify(offersStateMetric).addOffersStateTimer(eq(offerRequest.getUserUUID()), any(OffsetDateTime.class)),
                () -> verify(loanDemandMetric).incrementCounterForEachLoanProvider(anyString(), eq(false)),
                () -> verify(userStateService).saveUpdatedOffersStates(eq(offerRequest.getUserUUID()), any())
        );
    }

}
