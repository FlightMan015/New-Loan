package de.joonko.loan.partner.swk;

import com.google.common.base.Strings;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.offer.domain.LoanProviderApiMapper;
import de.joonko.loan.partner.swk.mapper.SwkApplyForCreditRequestMapper;
import de.joonko.loan.partner.swk.mapper.SwkCreditOfferResponseMapper;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SwkLoanProviderApiMapper implements LoanProviderApiMapper<CreditApplicationServiceStub.ApplyForCredit, List<CreditApplicationServiceStub.CreditOffer>> {

    @Autowired
    SwkApplyForCreditRequestMapper swkApplyForCreditRequestMapper;

    @Autowired
    SwkCreditOfferResponseMapper swkCreditOfferResponseMapper;

    @Value("${TWEAKED_IBAN_IN_LOAN_DEMAND_REQUEST:#{NULL}}")
    private String tweakedIBAN;

    @Override
    public CreditApplicationServiceStub.ApplyForCredit toLoanProviderRequest(LoanDemand loanDemand, LoanDuration loanDuration) {
        CreditApplicationServiceStub.ApplyForCredit applyForCredit = swkApplyForCreditRequestMapper.toApplyForCredit(loanDemand);
        applyForCredit.getRequest().setDuration(loanDuration.getValue());
        if (!Strings.isNullOrEmpty(tweakedIBAN)) {
            log.info("Env variable TWEAKED_IBAN_IN_LOAN_DEMAND_REQUEST is only meant for test environments. Modifying IBAN for consors with {}", tweakedIBAN);
            setFakeAccountDetails(applyForCredit);
        }
        return applyForCredit;
    }

    private void setFakeAccountDetails(CreditApplicationServiceStub.ApplyForCredit applyForCredit) {
        applyForCredit.getRequest().getCollectionAccount().setAccountNumber(tweakedIBAN);
        applyForCredit.getRequest().getPaymentAccount().setAccountNumber(tweakedIBAN);
    }

    @Override
    public List<LoanOffer> fromLoanProviderResponse(List<CreditApplicationServiceStub.CreditOffer> creditOffer) {
        return swkCreditOfferResponseMapper.fromLoanProviderResponse(creditOffer);
    }


}
