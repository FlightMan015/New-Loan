package de.joonko.loan.integrations.domain.enhancers;

import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.offer.api.model.OfferResponseState;
import de.joonko.loan.offer.api.model.OffersResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class WaitingResponseEnhancer implements ResponseEnhancer {

    @Override
    public Mono<OffersResponse> buildResponseData(OfferRequest offerRequest) {
        return Mono.just(OffersResponse.builder().state(getState()).build());
    }

    @Override
    public OfferResponseState getState() {
        // TODO: This should be WAITING, but as the WAITING state is not implemented in FE, for the time being the state is any which will trigger FE to pull the endpoint again
        return OfferResponseState.CLASSIFYING_TRANSACTIONS;
    }
}
