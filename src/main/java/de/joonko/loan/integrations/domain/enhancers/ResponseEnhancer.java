package de.joonko.loan.integrations.domain.enhancers;

import de.joonko.loan.integrations.model.OfferRequest;

import de.joonko.loan.offer.api.model.OfferResponseState;
import de.joonko.loan.offer.api.model.OffersResponse;
import reactor.core.publisher.Mono;

public interface ResponseEnhancer<T> {
    Mono<OffersResponse<T>> buildResponseData(OfferRequest offerRequest);
    OfferResponseState getState();
}
