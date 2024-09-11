package de.joonko.loan.offer.domain;

import java.util.List;

public interface LoanProviderApiMapper<I, O> {

    I toLoanProviderRequest(LoanDemand loanDemand, LoanDuration loanDuration);

    List<LoanOffer> fromLoanProviderResponse(O response);
}
