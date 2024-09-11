package de.joonko.loan.offer.domain;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;

import java.math.BigDecimal;

class FinanceFixtures {

    static LoanOffer getLoanOfferWithMonthlyRate(int monthlyRate) {
        return new LoanOffer(0, 48, BigDecimal.ZERO, BigDecimal.ZERO
                , BigDecimal.valueOf(monthlyRate), BigDecimal.ZERO, new LoanProvider(Bank.MOUNTAIN_BANK.label));
    }

    static LoanOffer getLoanOfferWithMonthlyRateAmountAndDuration(double monthlyRate, int amount, int durationInMonth) {
        return new LoanOffer(amount, durationInMonth, BigDecimal.ZERO, BigDecimal.ZERO
                , BigDecimal.valueOf(monthlyRate), BigDecimal.valueOf(monthlyRate).multiply(BigDecimal.valueOf(durationInMonth)), new LoanProvider(Bank.MOUNTAIN_BANK.label));
    }

    static LoanOffer getLoanOfferWithMonthlyRateAmountAndDuration(double monthlyRate, int amount, int durationInMonth, double totalPayment) {
        return new LoanOffer(amount, durationInMonth, BigDecimal.ZERO, BigDecimal.ZERO
                , BigDecimal.valueOf(monthlyRate), BigDecimal.valueOf(totalPayment), new LoanProvider(Bank.MOUNTAIN_BANK.label));
    }

    static Finance getFinanceFreeToSpend(int freeToSpend) {
        return new Finance(getIncome(freeToSpend), getExpenses(), BigDecimal.valueOf(freeToSpend));
    }

    static private Expenses getExpenses() {
        return Expenses.builder()
                .alimony(BigDecimal.ZERO)
                .insuranceAndSavings(BigDecimal.ZERO)
                .loanInstalments(BigDecimal.ZERO)
                .mortgages(BigDecimal.ZERO)
                .privateHealthInsurance(BigDecimal.ZERO)
                .rent(BigDecimal.ZERO)
                .build();
    }

    private static Income getIncome(int freeToSpend) {
        return Income.builder()
                .rentalIncome(BigDecimal.valueOf(freeToSpend))
                .alimonyPayments(BigDecimal.ZERO)
                .childBenefits(BigDecimal.ZERO)
                .netIncome(BigDecimal.ZERO)
                .otherRevenue(BigDecimal.ZERO)
                .pensionBenefits(BigDecimal.ZERO)
                .build();
    }
}
