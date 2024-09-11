package de.joonko.loan.integrations.domain;

import de.joonko.loan.integrations.domain.enhancers.ResponseEnhancerImpl;
import de.joonko.loan.integrations.domain.integrationhandler.IntegrationHandler;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.offer.OfferDemandRequest;
import de.joonko.loan.offer.api.model.OffersResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class OfferRequestProcessorImpl implements OfferRequestProcessor {

    private ResponseEnhancerImpl dataEnhancer;

    private UserStateReducer userStateReducer;

    private List<IntegrationHandler> integrationHandlers;

    @Override
    public Mono<OffersResponse> getOffers(OfferDemandRequest offerDemandRequest) {
        return userStateReducer.deriveUserState(offerDemandRequest)
                .doOnNext(this::triggerMutations)
                .flatMap(dataEnhancer::buildResponseData);
    }

    private void triggerMutations(OfferRequest offerReq) {
        Flux.fromIterable(integrationHandlers)
                .parallel()
                .runOn(Schedulers.elastic())
                .flatMap(handler -> handler.triggerMutation(offerReq))
                .doOnError(e -> log.error("Error in integration handler for userId: {}", offerReq.getUserUUID(), e))
                .subscribe();
    }
}
