package de.joonko.loan.partner.creditPlus;

import de.joonko.loan.acceptoffer.domain.AcceptOfferApiMapper;
import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.acceptoffer.domain.OfferStatus;
import org.springframework.stereotype.Component;

@Component
public class CreditPlusAcceptOfferMapper implements AcceptOfferApiMapper<CreditPlusAcceptOfferRequest, CreditPlusAcceptOfferResponse> {
    @Override
    public CreditPlusAcceptOfferRequest toAcceptOfferRequest(OfferRequest offerRequest) {
        return CreditPlusAcceptOfferRequest.builder()
                .offerId(offerRequest.getLoanOfferId())
                .duration(offerRequest.getDuration().value)
                .loanApplicationId(offerRequest.getApplicationId())
                .build();
    }

    @Override
    public OfferStatus fromAcceptOfferResponse(CreditPlusAcceptOfferResponse response) {
        OfferStatus offerStatus = new OfferStatus();
        offerStatus.setStatus(LoanApplicationStatus.OFFER_ACCEPTED);
        return offerStatus;
    }
}
