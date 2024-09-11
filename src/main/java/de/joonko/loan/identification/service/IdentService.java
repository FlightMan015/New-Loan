package de.joonko.loan.identification.service;

import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.CreateIdentResponse;
import de.joonko.loan.identification.model.IdentificationProvider;
import reactor.core.publisher.Mono;

public interface IdentService {
    Mono<CreateIdentResponse> createIdent(CreateIdentRequest createIdentRequest);

    Mono<String> getIdentStatus(String externalIdentId);

    IdentificationProvider getProvider();
}
