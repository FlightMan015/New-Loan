package de.joonko.loan.partner.auxmoney;

import de.joonko.loan.acceptoffer.domain.AcceptOfferApiMapper;
import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.acceptoffer.domain.OfferStatus;
import de.joonko.loan.partner.auxmoney.model.AuxmoneyAcceptOfferRequest;
import de.joonko.loan.partner.auxmoney.model.AuxmoneyAcceptOfferResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuxmoneyAcceptOfferApiMapper implements AcceptOfferApiMapper<AuxmoneyAcceptOfferRequest, AuxmoneyAcceptOfferResponse> {


    private AuxmoneyAcceptOfferRequestMapper requestMapper;


    @Override
    public AuxmoneyAcceptOfferRequest toAcceptOfferRequest(OfferRequest offerRequest) {
        return requestMapper.toAuxmoneyAcceptOfferRequest(offerRequest);
    }

    @Override
    public OfferStatus fromAcceptOfferResponse(AuxmoneyAcceptOfferResponse response) {
        OfferStatus offerStatus = new OfferStatus();
        offerStatus.setStatus(LoanApplicationStatus.OFFER_ACCEPTED);
        return offerStatus;
    }
}
