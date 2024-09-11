package de.joonko.loan.integrations.domain.integrationhandler;

import de.joonko.loan.integrations.model.OfferRequest;

import reactor.core.publisher.Mono;

public interface IntegrationHandler {

    Mono<Void> triggerMutation(OfferRequest offerRequest);
}
