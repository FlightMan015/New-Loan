package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.swk.SwkDefaults;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SwkCreditOfferResponseMapperTest extends BaseMapperTest {

    @Autowired
    SwkCreditOfferResponseMapper swkCreditOfferResponseMapper;

    @Random
    CreditApplicationServiceStub.CreditOffer creditOffer;

    @Test
    void fromLoanProviderResponse() {
        List<LoanOffer> offerList = swkCreditOfferResponseMapper.fromLoanProviderResponse(List.of(creditOffer));
        assertEquals(1, offerList.size());
        assertNotNull(offerList.get(0).getAmount());
        assertNotNull(offerList.get(0).getDurationInMonth());
        assertNotNull(offerList.get(0).getEffectiveInterestRate());
        assertNotNull(offerList.get(0).getNominalInterestRate());
        assertNotNull(offerList.get(0).getMonthlyRate());
        assertNotNull(offerList.get(0).getTotalPayment());
    }

    @Test
    void amount() {
        List<LoanOffer> offerList = swkCreditOfferResponseMapper.fromLoanProviderResponse(List.of(creditOffer));
        assertEquals((int) creditOffer.getNetCreditAmount(), offerList.get(0).getAmount());
    }

    @Test
    void duration() {
        creditOffer.setDuration(Integer.valueOf(SwkDefaults.SWK_CREDIT_DURATION));
        List<LoanOffer> offerList = swkCreditOfferResponseMapper.fromLoanProviderResponse(List.of(creditOffer));
        assertEquals(Integer.valueOf(SwkDefaults.SWK_CREDIT_DURATION), offerList.get(0).getDurationInMonth());
    }

    @Test
    void effectiveInterestRate() {
        List<LoanOffer> offerList = swkCreditOfferResponseMapper.fromLoanProviderResponse(List.of(creditOffer));
        assertEquals(BigDecimal.valueOf(creditOffer.getEffectiveInterest()), offerList.get(0).getEffectiveInterestRate());
    }

    @Test
    void nominalInterestRate() {
        List<LoanOffer> offerList = swkCreditOfferResponseMapper.fromLoanProviderResponse(List.of(creditOffer));
        assertEquals(BigDecimal.valueOf(creditOffer.getNominalInterest()), offerList.get(0).getNominalInterestRate());
    }

    @Test
    void montlyRate() {
        creditOffer.setFirstInstallmentAmount(140.33);
        List<LoanOffer> offerList = swkCreditOfferResponseMapper.fromLoanProviderResponse(List.of(creditOffer));
        assertEquals(BigDecimal.valueOf(creditOffer.getFirstInstallmentAmount()), offerList.get(0).getMonthlyRate());
    }

    @Test
    void totalPayment() {
        List<LoanOffer> offerList = swkCreditOfferResponseMapper.fromLoanProviderResponse(List.of(creditOffer));
        assertEquals(BigDecimal.valueOf(creditOffer.getTotalCreditAmount()), offerList.get(0).getTotalPayment());
    }

    @Test
    void loanProvider() {
        List<LoanOffer> offerList = swkCreditOfferResponseMapper.fromLoanProviderResponse(List.of(creditOffer));
        assertEquals(new LoanProvider(Bank.SWK_BANK.label), offerList.get(0).getLoanProvider());
    }


}
