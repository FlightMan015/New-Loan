package de.joonko.loan.integrations.domain;

import de.joonko.loan.integrations.domain.enhancers.ResponseEnhancerImpl;
import de.joonko.loan.integrations.domain.integrationhandler.IntegrationHandler;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.integrations.model.OffersState;
import de.joonko.loan.integrations.model.UserState;
import de.joonko.loan.offer.OfferDemandRequest;
import de.joonko.loan.offer.api.model.OffersResponse;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.stubbing.answers.AnswersWithDelay;
import org.mockito.internal.stubbing.answers.Returns;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(RandomBeansExtension.class)
class OfferRequestProcessorTest {

    private OfferRequestProcessor offerRequestProcessor;

    private UserStateReducer userStateReducer;
    private IntegrationHandler userPersonalDataIntegrationHandler;
    private IntegrationHandler userTransactionalDataIntegrationHandler;
    private ResponseEnhancerImpl responseEnhancer;

    private static final Long BONIFY_USER_ID = 7236987363L;

    @BeforeEach
    void setUp() {
        responseEnhancer = mock(ResponseEnhancerImpl.class);
        userStateReducer = mock(UserStateReducer.class);
        userPersonalDataIntegrationHandler = mock(IntegrationHandler.class);
        userTransactionalDataIntegrationHandler = mock(IntegrationHandler.class);
        List<IntegrationHandler> integrationHandlers = List.of(userPersonalDataIntegrationHandler, userTransactionalDataIntegrationHandler);

        offerRequestProcessor = new OfferRequestProcessorImpl(responseEnhancer, userStateReducer, integrationHandlers);
    }

    @Test
    void getOfferResponseWhenOneIntegrationHandlerReturnsError(@Random OfferDemandRequest offerDemandRequest) {
        // given
        offerDemandRequest.setInetAddress(Optional.empty());
        OfferRequest offerRequest = new OfferRequest(offerDemandRequest, BONIFY_USER_ID);
        offerRequest.setUserState(UserState.builder()
                .offersState(OffersState.OFFERS_EXIST).build());
        when(userStateReducer.deriveUserState(offerDemandRequest)).thenReturn(Mono.just(offerRequest));
        when(userPersonalDataIntegrationHandler.triggerMutation(offerRequest)).thenReturn(Mono.empty());
        when(userTransactionalDataIntegrationHandler.triggerMutation(offerRequest)).thenReturn(Mono.error(new IllegalStateException("Fake error")));
        when(responseEnhancer.buildResponseData(offerRequest)).thenReturn(Mono.just(mock(OffersResponse.class)));

        // when
        var actualOfferResponse = offerRequestProcessor.getOffers(offerDemandRequest);

        // then
        assertAll(
                () -> StepVerifier.create(actualOfferResponse).expectNextCount(1).verifyComplete(),
                () -> verify(userPersonalDataIntegrationHandler, timeout(1000)).triggerMutation(offerRequest),
                () -> verify(userTransactionalDataIntegrationHandler, timeout(1000)).triggerMutation(offerRequest),
                () -> verify(responseEnhancer).buildResponseData(offerRequest)
        );
    }

    @Test
    void getErrorAfterFailingGettingOfferRequest(@Random OfferDemandRequest offerDemandRequest) {
        // given
        when(userStateReducer.deriveUserState(offerDemandRequest)).thenReturn(Mono.error(IllegalStateException::new));

        // when
        var actualOfferResponse = offerRequestProcessor.getOffers(offerDemandRequest);

        // then
        StepVerifier.create(actualOfferResponse)
                .verifyError(IllegalStateException.class);
    }

    @Test
    void getOfferResponseAndDoNotWaitForMutationsToFinishProcessing(@Random OfferDemandRequest offerDemandRequest) {
        // given
        offerDemandRequest.setInetAddress(Optional.empty());
        OfferRequest offerRequest = new OfferRequest(offerDemandRequest, BONIFY_USER_ID);
        offerRequest.setUserState(UserState.builder()
                .offersState(OffersState.OFFERS_EXIST).build());
        when(userStateReducer.deriveUserState(offerDemandRequest)).thenReturn(Mono.just(offerRequest));
        doAnswer(new AnswersWithDelay(3000, new Returns(Mono.empty()))).when(userPersonalDataIntegrationHandler).triggerMutation(offerRequest);
        when(userTransactionalDataIntegrationHandler.triggerMutation(offerRequest)).thenReturn(Mono.error(new IllegalStateException("Fake error")));
        when(responseEnhancer.buildResponseData(offerRequest)).thenReturn(Mono.just(mock(OffersResponse.class)));


        // when
        var actualOfferResponse = offerRequestProcessor.getOffers(offerDemandRequest);

        // then
        assertAll(
                () -> StepVerifier.create(actualOfferResponse).expectNextCount(1).verifyComplete(),
                () -> verify(userPersonalDataIntegrationHandler, timeout(1000)).triggerMutation(offerRequest),
                () -> verify(userTransactionalDataIntegrationHandler, timeout(1000)).triggerMutation(offerRequest),
                () -> verify(responseEnhancer).buildResponseData(offerRequest)
        );
    }
}
