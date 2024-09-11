package de.joonko.loan.partner.santander;

import de.joonko.loan.acceptoffer.domain.AcceptOfferApiMapper;
import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.acceptoffer.domain.OfferStatus;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SantanderAcceptOfferApiMapper implements AcceptOfferApiMapper<SantanderAcceptOfferRequest, SantanderAcceptOfferResponse> {

    private final SantanderLoanProviderApiMapper santanderLoanProviderApiMapper;

    @Override
    public SantanderAcceptOfferRequest toAcceptOfferRequest(OfferRequest offerRequest) {
        ScbCapsBcoWSStub.GetKreditvertragsangebot getKreditvertragsangebot = santanderLoanProviderApiMapper.toLoanProviderRequest(offerRequest.getLoanDemand(), offerRequest.getDuration());

        return SantanderAcceptOfferRequest.builder()
                .duration(offerRequest.getDuration())
                .getKreditvertragsangebot(getKreditvertragsangebot)
                .build();
    }

    @Override
    public OfferStatus fromAcceptOfferResponse(SantanderAcceptOfferResponse response) {
        OfferStatus offerStatus = new OfferStatus();
        offerStatus.setStatus(LoanApplicationStatus.OFFER_ACCEPTED);
        return offerStatus;
    }
}
