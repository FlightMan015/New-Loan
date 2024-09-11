package de.joonko.loan.offer.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@AllArgsConstructor
public class Finance {

    private Income income;
    private Expenses expenses;
    private BigDecimal disposableAmount;

    public boolean hasRentalIncome() {
        return income.getRentalIncome()
                .compareTo(BigDecimal.ZERO) > 0;
    }

    public int getRoundUpFreeToSpendAmount() {
        BigDecimal freeToSpend = income.getSumOfAllIncomes()
                .subtract(expenses.getSumOfAllExpenses())
                .setScale(0, RoundingMode.HALF_UP);
        return freeToSpend.compareTo(BigDecimal.ZERO) < 0 ? 0 : freeToSpend.intValue();

    }
}
