package de.joonko.loan.offer.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static de.joonko.loan.offer.domain.ExpenseModel.basicExpense;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpenseTest {

    @Test
    @DisplayName("Should sum all the expenses")
    void sum() {
        Expenses expenses = basicExpense();
        assertEquals(BigDecimal.valueOf(42), expenses.getSumOfAllExpenses());
    }
}
