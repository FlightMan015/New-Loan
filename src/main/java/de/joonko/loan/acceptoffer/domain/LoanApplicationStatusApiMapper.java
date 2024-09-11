package de.joonko.loan.acceptoffer.domain;

public interface LoanApplicationStatusApiMapper<I, O> {

    I toLoanApplicationStatusRequest(OfferRequest offerRequest);

    LoanApplicationStatus fromLoanApplicationStatusResponse(O response);
}
