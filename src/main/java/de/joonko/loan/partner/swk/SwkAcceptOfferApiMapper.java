package de.joonko.loan.partner.swk;

import de.joonko.loan.acceptoffer.domain.AcceptOfferApiMapper;
import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.acceptoffer.domain.OfferStatus;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SwkAcceptOfferApiMapper implements AcceptOfferApiMapper<SwkAcceptOfferRequest, SwkAcceptOfferResponse> {

    private final SwkLoanProviderApiMapper mapper;
    @Override
    public SwkAcceptOfferRequest toAcceptOfferRequest(OfferRequest offerRequest) {
        CreditApplicationServiceStub.ApplyForCredit applyForCredit = mapper.toLoanProviderRequest(offerRequest.getLoanDemand(), offerRequest.getDuration());
        applyForCredit.getRequest()
                .setDuration(offerRequest.getDuration().value);

        return SwkAcceptOfferRequest.builder()
                .offerId(offerRequest.getLoanOfferId())
                .applicationId(offerRequest.getApplicationId())
                .applyForCredit(applyForCredit)
                .build();
    }

    @Override
    public OfferStatus fromAcceptOfferResponse(SwkAcceptOfferResponse response)
    {
        OfferStatus offerStatus = new OfferStatus();
        offerStatus.setStatus(LoanApplicationStatus.OFFER_ACCEPTED);
        return offerStatus;
    }
}
