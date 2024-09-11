package de.joonko.loan.offer.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static de.joonko.loan.offer.domain.DisposableAmountCalculator.calculateDisposableAmount;
import static de.joonko.loan.offer.domain.ExpenseModel.expense;
import static de.joonko.loan.offer.domain.IncomeModel.income;

public class DisposableAmountCalculatorTest {

    @Test
    void calculate_disposable_amount() {
        final var income = income(); // 4866
        final var expenses = expense(); // 2546.4
        final var numberOfChildren = 2;
        final var numberOfCars = 1;

        final var result = calculateDisposableAmount(income, expenses, numberOfChildren, numberOfCars);


        Assertions.assertEquals(0, result.compareTo(BigDecimal.valueOf(1619.6)));
    }
}
