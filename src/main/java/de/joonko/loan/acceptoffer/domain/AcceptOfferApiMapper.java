package de.joonko.loan.acceptoffer.domain;

public interface AcceptOfferApiMapper<I, O> {

    I toAcceptOfferRequest(OfferRequest offerRequest);

    OfferStatus fromAcceptOfferResponse(O response);
}
