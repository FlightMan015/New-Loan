package de.joonko.loan.integrations.domain.integrationhandler.loandemand;

import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.offer.domain.LoanDemand;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface LoanDemandProviderService {
    Mono<Set<LoanDemand>> getLoanDemandFromOfferRequest(OfferRequest userRequest);

    Mono<LoanDemand> savePrechecksToRequestAndPublish(LoanDemand loanDemand);
}
