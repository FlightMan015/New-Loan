package de.joonko.loan.partner.aion;

import de.joonko.loan.acceptoffer.domain.AcceptOfferApiMapper;
import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.acceptoffer.domain.OfferStatus;
import de.joonko.loan.partner.aion.model.AionAcceptOfferRequest;
import de.joonko.loan.partner.aion.model.AionAcceptOfferResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AionAcceptOfferApiMapper implements AcceptOfferApiMapper<AionAcceptOfferRequest, AionAcceptOfferResponse> {

    @Override
    public AionAcceptOfferRequest toAcceptOfferRequest(OfferRequest offerRequest) {
        return new AionAcceptOfferRequest();
    }

    @Override
    public OfferStatus fromAcceptOfferResponse(AionAcceptOfferResponse response) {
        OfferStatus offerStatus = new OfferStatus();
        offerStatus.setStatus(LoanApplicationStatus.OFFER_ACCEPTED);
        return offerStatus;
    }
}
