package de.joonko.loan.offer.domain;

import java.math.BigDecimal;

public class ExpenseModel {

    public static Expenses basicExpense() {
        return Expenses.builder()
                .alimony(BigDecimal.ONE)
                .insuranceAndSavings(BigDecimal.TEN)
                .loanInstalments(BigDecimal.ONE)
                .mortgages(BigDecimal.TEN)
                .privateHealthInsurance(BigDecimal.TEN)
                .rent(BigDecimal.TEN)
                .build();
    }

    public static Expenses expense() {
        return Expenses.builder()
                .alimony(BigDecimal.ZERO)
                .insuranceAndSavings(BigDecimal.valueOf(200))
                .loanInstalments(BigDecimal.valueOf(46.4))
                .mortgages(BigDecimal.valueOf(1200))
                .acknowledgedMortgages(BigDecimal.valueOf(1200))
                .privateHealthInsurance(BigDecimal.ZERO)
                .rent(BigDecimal.valueOf(950))
                .acknowledgedRent(BigDecimal.valueOf(950))
                .vehicleInsurance(BigDecimal.valueOf(150))
                .build();
    }
}
