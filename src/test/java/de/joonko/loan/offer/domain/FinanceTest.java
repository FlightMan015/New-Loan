package de.joonko.loan.offer.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinanceTest {


    @Nested
    class FreeToSpend {

        @Test
        @DisplayName("free to spend should equals total income minus total expense")
        void freeToSpend() {
            Income income = getIncome(BigDecimal.valueOf(300), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            Expenses expense = getExpenses(BigDecimal.valueOf(200), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            Finance finance = new Finance(income, expense, BigDecimal.TEN);
            assertEquals(100, finance.getRoundUpFreeToSpendAmount());
        }

        @Test
        @DisplayName("free to spend should be zero if total expense is more than total income")
        void negativeIncome() {
            Income income = getIncome(BigDecimal.valueOf(100), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            Expenses expense = getExpenses(BigDecimal.valueOf(200), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            Finance finance = new Finance(income, expense, BigDecimal.TEN);
            assertEquals(0, finance.getRoundUpFreeToSpendAmount());
        }

        @Test
        @DisplayName("free to spend should be rounded to full euro - Round up")
        void rounding() {
            Income income = getIncome(BigDecimal.valueOf(100.50), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            Expenses expense = getExpenses(BigDecimal.valueOf(0), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            Finance finance = new Finance(income, expense, BigDecimal.TEN);
            assertEquals(101, finance.getRoundUpFreeToSpendAmount());
        }

        @Test
        @DisplayName("free to spend should be rounded to full euro - Round down")
        void roundingDown() {
            Income income = getIncome(BigDecimal.valueOf(100.49), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            Expenses expense = getExpenses(BigDecimal.valueOf(0), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            Finance finance = new Finance(income, expense, BigDecimal.TEN);
            assertEquals(100, finance.getRoundUpFreeToSpendAmount());
        }

    }

    @NotNull
    private Income getIncome(BigDecimal rent, BigDecimal alimonyPayments, BigDecimal childBenefits, BigDecimal netIncome, BigDecimal otherRevenue, BigDecimal pensionBenefits) {
        return Income.builder()
                .rentalIncome(rent)
                .alimonyPayments(alimonyPayments)
                .childBenefits(childBenefits)
                .netIncome(netIncome)
                .otherRevenue(otherRevenue)
                .pensionBenefits(pensionBenefits)
                .build();
    }

    private Expenses getExpenses(BigDecimal rent, BigDecimal alimonyPayments, BigDecimal insuranceAndSavings, BigDecimal loanInstalments, BigDecimal mortgages, BigDecimal privateHealthInsurance) {
        return Expenses.builder()
                .alimony(alimonyPayments)
                .insuranceAndSavings(insuranceAndSavings)
                .loanInstalments(loanInstalments)
                .mortgages(mortgages)
                .privateHealthInsurance(privateHealthInsurance)
                .rent(rent)
                .build();


    }


}

