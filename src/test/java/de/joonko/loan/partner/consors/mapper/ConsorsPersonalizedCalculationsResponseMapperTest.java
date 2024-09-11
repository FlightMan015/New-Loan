package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.consors.model.FinancialCalculation;
import de.joonko.loan.partner.consors.model.PersonalizedCalculationsResponse;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsorsPersonalizedCalculationsResponseMapperTest extends BaseMapperTest {


    @Autowired
    private ConsorsPersonalizedCalculationsResponseMapper consorsPersonalizedCalculationsResponseMapper;


    @Test
    @DisplayName("Should map each element from PersonalizedCalculationsResponse.FinancialCalculations.FinancialCalculation to One Offer")
    void fromLoanProviderResponse(@Random PersonalizedCalculationsResponse personalizedCalculationsResponse) {
        List<LoanOffer> loanOffers = consorsPersonalizedCalculationsResponseMapper.fromLoanProviderResponse(personalizedCalculationsResponse);
        assertEquals(personalizedCalculationsResponse.getFinancialCalculations()
                .getFinancialCalculation()
                .size(), loanOffers.size());
    }

    @Test
    @DisplayName("Should map null PersonalizedCalculationsResponse to empty Offer list")
    void fromLoanProviderResponse() {
        List<LoanOffer> loanOffers = consorsPersonalizedCalculationsResponseMapper.fromLoanProviderResponse(null);
        assertEquals(0, loanOffers.size());
    }

    @Test
    @DisplayName("Should map null PersonalizedCalculationsResponse.FinancialCalculations  to empty Offer list")
    void fromLoanProviderResponseNullFinancialCalculations() {
        List<LoanOffer> loanOffers = consorsPersonalizedCalculationsResponseMapper.fromLoanProviderResponse(PersonalizedCalculationsResponse.builder()
                .build());
        assertEquals(0, loanOffers.size());
    }

    @Test
    @DisplayName("Should map null PersonalizedCalculationsResponse.FinancialCalculations.financialCalculation  to empty Offer list")
    void fromLoanProviderResponseNullFinancialCalculationsList(@Random PersonalizedCalculationsResponse personalizedCalculationsResponse) {
        personalizedCalculationsResponse.getFinancialCalculations()
                .setFinancialCalculation(null);
        List<LoanOffer> loanOffers = consorsPersonalizedCalculationsResponseMapper.fromLoanProviderResponse(personalizedCalculationsResponse);
        assertEquals(0, loanOffers.size());
    }

    @Test
    @DisplayName("Should map creditAmount  to amount")
    void amount(@Random PersonalizedCalculationsResponse personalizedCalculationsResponse, @Random FinancialCalculation financialCalculation) {
        financialCalculation.setCreditAmount(1010);
        personalizedCalculationsResponse.getFinancialCalculations()
                .setFinancialCalculation(List.of(financialCalculation));
        List<LoanOffer> loanOffers = consorsPersonalizedCalculationsResponseMapper.fromLoanProviderResponse(personalizedCalculationsResponse);
        assertEquals(1, loanOffers.size());
        assertEquals(1010, loanOffers.get(0)
                .getAmount());
    }

    @Test
    @DisplayName("Should map duration  to durationInMonth")
    void duration(@Random PersonalizedCalculationsResponse personalizedCalculationsResponse, @Random FinancialCalculation financialCalculation) {
        financialCalculation.setDuration(101);
        personalizedCalculationsResponse.getFinancialCalculations()
                .setFinancialCalculation(List.of(financialCalculation));
        List<LoanOffer> loanOffers = consorsPersonalizedCalculationsResponseMapper.fromLoanProviderResponse(personalizedCalculationsResponse);
        assertEquals(1, loanOffers.size());
        assertEquals(101, loanOffers.get(0)
                .getDurationInMonth());
    }

    @Test
    @DisplayName("Should map EffectiveRate duration  to effectiveInterestRate")
    void effectiveRate(@Random PersonalizedCalculationsResponse personalizedCalculationsResponse, @Random FinancialCalculation financialCalculation) {
        financialCalculation.setEffectiveRate(101.00);
        personalizedCalculationsResponse.getFinancialCalculations()
                .setFinancialCalculation(List.of(financialCalculation));
        List<LoanOffer> loanOffers = consorsPersonalizedCalculationsResponseMapper.fromLoanProviderResponse(personalizedCalculationsResponse);
        assertEquals(1, loanOffers.size());
        assertEquals(101, loanOffers.get(0)
                .getEffectiveInterestRate()
                .longValue());
    }

    @Test
    @DisplayName("Should map Nominal Rate duration  to nominalInterestRate")
    void nominalRate(@Random PersonalizedCalculationsResponse personalizedCalculationsResponse, @Random FinancialCalculation financialCalculation) {
        financialCalculation.setNominalRate(101.00);
        personalizedCalculationsResponse.getFinancialCalculations()
                .setFinancialCalculation(List.of(financialCalculation));
        List<LoanOffer> loanOffers = consorsPersonalizedCalculationsResponseMapper.fromLoanProviderResponse(personalizedCalculationsResponse);
        assertEquals(1, loanOffers.size());
        assertEquals(101, loanOffers.get(0)
                .getNominalInterestRate()
                .longValue());
    }

    @Test
    @DisplayName("Should map Monthly Rate Rate duration  to monthlyRate")
    void monthly(@Random PersonalizedCalculationsResponse personalizedCalculationsResponse, @Random FinancialCalculation financialCalculation) {
        financialCalculation.setMonthlyRate(999.00);
        personalizedCalculationsResponse.getFinancialCalculations()
                .setFinancialCalculation(List.of(financialCalculation));
        List<LoanOffer> loanOffers = consorsPersonalizedCalculationsResponseMapper.fromLoanProviderResponse(personalizedCalculationsResponse);
        assertEquals(1, loanOffers.size());
        assertEquals(999, loanOffers.get(0)
                .getMonthlyRate()
                .longValue());
    }

    @Test
    @DisplayName("Should map totalPayment Rate Rate duration  to totalPayment")
    void totalPayment(@Random PersonalizedCalculationsResponse personalizedCalculationsResponse, @Random FinancialCalculation financialCalculation) {
        financialCalculation.setTotalPayment(999.00);
        personalizedCalculationsResponse.getFinancialCalculations()
                .setFinancialCalculation(List.of(financialCalculation));
        List<LoanOffer> loanOffers = consorsPersonalizedCalculationsResponseMapper.fromLoanProviderResponse(personalizedCalculationsResponse);
        assertEquals(1, loanOffers.size());
        assertEquals(999, loanOffers.get(0)
                .getTotalPayment()
                .longValue());
    }

    @Test
    @DisplayName("Should map loanProvider  to Consors")
    void loanProvider(@Random PersonalizedCalculationsResponse personalizedCalculationsResponse, @Random FinancialCalculation financialCalculation) {

        personalizedCalculationsResponse.getFinancialCalculations()
                .setFinancialCalculation(List.of(financialCalculation));
        List<LoanOffer> loanOffers = consorsPersonalizedCalculationsResponseMapper.fromLoanProviderResponse(personalizedCalculationsResponse);
        assertEquals(1, loanOffers.size());
        assertEquals(Bank.CONSORS.label, loanOffers.get(0)
                .getLoanProvider()
                .getName());
    }


}
