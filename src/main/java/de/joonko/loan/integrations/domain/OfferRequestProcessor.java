package de.joonko.loan.integrations.domain;

import de.joonko.loan.offer.OfferDemandRequest;
import de.joonko.loan.offer.api.model.OffersResponse;
import reactor.core.publisher.Mono;

public interface OfferRequestProcessor {

    Mono<OffersResponse> getOffers(OfferDemandRequest offerDemandRequest);
}
