package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.solaris.model.AmountValue;
import de.joonko.loan.partner.solaris.model.SolarisGetOffersResponse;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolarisGetOffersResponseMapperTest extends BaseMapperTest {

    @Autowired
    SolarisGetOffersResponseMapper solarisGetOffersResponseMapper;

    @Random
    SolarisGetOffersResponse solarisGetOffersResponse;

    @Test
    @DisplayName("Should map solaris amount to LoanOffer.amount")
    void amount() {
        solarisGetOffersResponse.getOffer().setLoanAmount(AmountValue.builder().value(100000).build());
        solarisGetOffersResponse.setLoanDecision("approved");
        List<LoanOffer> offerList = solarisGetOffersResponseMapper.fromLoanProviderResponse(List.of(solarisGetOffersResponse));
        assertEquals(1, offerList.size());
        assertEquals(1000, offerList.get(0).getAmount());

    }

    @Test
    @DisplayName("Should map Loan DEUTSCHE_FINANZ_SOZIETÄT to DEUTSCHE_FINANZ_SOZIETÄT")
    void loadProvider() {

        solarisGetOffersResponse.setLoanDecision("approved");
        List<LoanOffer> offers = solarisGetOffersResponseMapper.fromLoanProviderResponse(List.of(solarisGetOffersResponse));
        assertEquals(1, offers.size());
        assertEquals(Bank.DEUTSCHE_FINANZ_SOZIETÄT.label, offers.get(0)
                .getLoanProvider()
                .getName());
    }

    @Test
    @DisplayName("Should map solaris loanTerm to LoanOffer.duration")
    void loanTerm() {

        solarisGetOffersResponse.setLoanDecision("approved");
        List<LoanOffer> offerList = solarisGetOffersResponseMapper.fromLoanProviderResponse(List.of(solarisGetOffersResponse));
        assertEquals(1, offerList.size());
        assertEquals(solarisGetOffersResponse.getOffer().getLoanTerm(), offerList.get(0).getDurationInMonth());

    }

    @Test
    @DisplayName("Should map solaris effectiveInterestRate with multply 100 to LoanOffer.effectiveInterestRate")
    void effectiveInterestRate() {
        solarisGetOffersResponse.setLoanDecision("approved");
        List<LoanOffer> offerList = solarisGetOffersResponseMapper.fromLoanProviderResponse(List.of(solarisGetOffersResponse));
        assertEquals(1, offerList.size());
        assertEquals(BigDecimal.valueOf(solarisGetOffersResponse.getOffer().getEffectiveInterestRate() * 100), offerList.get(0).getEffectiveInterestRate());

    }

    @Test
    @DisplayName("Should map solaris intertestRate with multply 100 to LoanOffer.nominalInterestRate")
    void nominalInterestRate() {
        solarisGetOffersResponse.setLoanDecision("approved");
        List<LoanOffer> offerList = solarisGetOffersResponseMapper.fromLoanProviderResponse(List.of(solarisGetOffersResponse));
        assertEquals(1, offerList.size());
        assertEquals(BigDecimal.valueOf(solarisGetOffersResponse.getOffer().getIntertestRate() * 100), offerList.get(0).getNominalInterestRate());

    }

    @Test
    @DisplayName("Should map solaris monthlyInstallment to LoanOffer.monthlyRate")
    void monthlyRate() {
        solarisGetOffersResponse.setLoanDecision("approved");
        List<LoanOffer> offerList = solarisGetOffersResponseMapper.fromLoanProviderResponse(List.of(solarisGetOffersResponse));
        assertEquals(1, offerList.size());
        assertEquals(BigDecimal.valueOf(solarisGetOffersResponse.getOffer().getMonthlyInstallment().getValue()/100.00), offerList.get(0).getMonthlyRate());

    }

    @Test
    @DisplayName("Should map solaris approximateTotalLoanExpenses to LoanOffer.totalPayment")
    void totalPayment() {
        solarisGetOffersResponse.getOffer().setApproximateTotalLoanExpenses(AmountValue.builder().value(100000).build());
        solarisGetOffersResponse.setLoanDecision("approved");
        List<LoanOffer> offerList = solarisGetOffersResponseMapper.fromLoanProviderResponse(List.of(solarisGetOffersResponse));
        assertEquals(1, offerList.size());
        assertEquals(BigDecimal.valueOf(1000.0), offerList.get(0).getTotalPayment());
    }
}
