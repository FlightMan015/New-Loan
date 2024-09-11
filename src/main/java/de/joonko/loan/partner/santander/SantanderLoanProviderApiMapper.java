package de.joonko.loan.partner.santander;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.offer.domain.LoanProviderApiMapper;
import de.joonko.loan.partner.santander.mapper.SantanderGetOfferRequestMapper;
import de.joonko.loan.partner.santander.mapper.SantanderGetOfferResponseMapper;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Component
public class SantanderLoanProviderApiMapper implements LoanProviderApiMapper<ScbCapsBcoWSStub.GetKreditvertragsangebot, List<ScbCapsBcoWSStub.GetKreditvertragsangebotResponse>> {

    @Autowired
    SantanderGetOfferRequestMapper requestMapper;

    @Autowired
    SantanderGetOfferResponseMapper responseMapper;

    @Override
    public ScbCapsBcoWSStub.GetKreditvertragsangebot toLoanProviderRequest(final LoanDemand loanDemand, final LoanDuration loanDuration) {
        ScbCapsBcoWSStub.GetKreditvertragsangebot getKreditvertragsangebot = requestMapper.toSantanderGetOfferRequest(loanDemand);
        getKreditvertragsangebot.getGetKreditvertragsangebot().getKreditantrag().getFinanzierung().setLaufzeitInMonaten(BigInteger.valueOf(loanDuration.getValue()));
        return getKreditvertragsangebot;
    }

    @Override
    public List<LoanOffer> fromLoanProviderResponse(final List<ScbCapsBcoWSStub.GetKreditvertragsangebotResponse> response) {
        return responseMapper.toLoanProviderResponse(response);
    }
}
