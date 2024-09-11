package de.joonko.loan.partner.postbank;

import de.joonko.loan.acceptoffer.domain.AcceptOfferApiMapper;
import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.acceptoffer.domain.OfferStatus;
import de.joonko.loan.partner.postbank.model.PostbankAcceptOfferRequest;
import de.joonko.loan.partner.postbank.model.PostbankAcceptOfferResponse;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PostbankAcceptOfferApiMapper implements AcceptOfferApiMapper<PostbankAcceptOfferRequest, PostbankAcceptOfferResponse> {

    @Override
    public PostbankAcceptOfferRequest toAcceptOfferRequest(OfferRequest offerRequest) {
        return new PostbankAcceptOfferRequest();
    }

    @Override
    public OfferStatus fromAcceptOfferResponse(PostbankAcceptOfferResponse response) {
        OfferStatus offerStatus = new OfferStatus();
        offerStatus.setStatus(LoanApplicationStatus.OFFER_ACCEPTED);
        return offerStatus;
    }
}
