package de.joonko.loan.offer.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static de.joonko.loan.offer.domain.IncomeModel.basicIncome;
import static de.joonko.loan.offer.domain.IncomeModel.income;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IncomeTest {

    @Test
    @DisplayName("Should sum all the incomes")
    void sum() {
        Income income = basicIncome();
        assertEquals(BigDecimal.valueOf(60.18), income.getSumOfAllIncomes());
    }
}
