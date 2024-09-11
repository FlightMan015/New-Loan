package de.joonko.loan.offer.domain;

import de.joonko.loan.offer.api.LoanDemandRequest;

import java.util.Set;

public interface LoanRecommendationEngine {

    Set<LoanDemandRequest> recommend(final LoanDemandRequest loanDemandRequest);
}
