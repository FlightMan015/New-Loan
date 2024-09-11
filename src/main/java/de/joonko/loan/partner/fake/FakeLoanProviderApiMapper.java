package de.joonko.loan.partner.fake;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.offer.domain.LoanProviderApiMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FakeLoanProviderApiMapper implements LoanProviderApiMapper<LoanDemand, List<LoanOffer>> {
    @Override
    public LoanDemand toLoanProviderRequest(LoanDemand loanDemand, LoanDuration loanDuration) {
        return loanDemand;
    }

    @Override
    public List<LoanOffer> fromLoanProviderResponse(List<LoanOffer> offers) {
        return offers;
    }


}
