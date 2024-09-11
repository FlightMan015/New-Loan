package de.joonko.loan.partner.creditPlus;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.offer.domain.LoanProviderApiMapper;
import de.joonko.loan.partner.creditPlus.mapper.CreditPlusResponseMapper;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreditPlusLoanProviderApiMapper implements LoanProviderApiMapper<EfinComparerServiceStub.CreateCreditOfferDacE, List<EfinComparerServiceStub.Contract>> {

    @Autowired
    CreditPlusRequestMapper creditPlusRequestMapper;

    @Autowired
    CreditPlusResponseMapper creditPlusResponseMapper;

    @Override
    public EfinComparerServiceStub.CreateCreditOfferDacE toLoanProviderRequest(LoanDemand loanDemand, LoanDuration loanDuration) {
        EfinComparerServiceStub.CreateCreditOfferDacE createCreditOfferDacE = creditPlusRequestMapper.toCreateCreditOfferDacE(loanDemand);
        createCreditOfferDacE.getCreateCreditOfferDac().getEfinComparerCreditOffer().setDuration(loanDuration.value);
        return createCreditOfferDacE;
    }

    @Override
    public List<LoanOffer> fromLoanProviderResponse(List<EfinComparerServiceStub.Contract> response) {
        return creditPlusResponseMapper.fromLoanProviderResponse(response);
    }
}
