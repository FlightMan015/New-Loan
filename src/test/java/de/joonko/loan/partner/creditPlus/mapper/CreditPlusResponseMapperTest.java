package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreditPlusResponseMapperTest extends BaseMapperTest {

    @Autowired
    CreditPlusResponseMapper mapper;

    @Random
    EfinComparerServiceStub.Contract contract;

    @Test
    void fromLoanProviderResponse() {
        List<LoanOffer> loanOfferList = mapper.fromLoanProviderResponse(List.of(contract));
        LoanOffer loanOffer = loanOfferList.get(0);
        assertEquals(loanOffer.getAmount(), contract.getAmount().intValue());
        assertEquals(loanOffer.getMonthlyRate(), contract.getRate());
        assertEquals(loanOffer.getTotalPayment(), contract.getFullAmount());
        assertEquals(loanOffer.getDurationInMonth(), contract.getDuration());
        assertEquals(loanOffer.getEffectiveInterestRate(), contract.getInterest());
        assertEquals(loanOffer.getLoanProvider(), new LoanProvider(Bank.CREDIT_PLUS.label));
        assertEquals(loanOffer.getNominalInterestRate(), contract.getNominalInterest());
    }
}