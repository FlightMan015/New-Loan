package de.joonko.loan.partner.auxmoney.getoffers;

import de.joonko.loan.partner.auxmoney.model.Expenses;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@ExtendWith(RandomBeansExtension.class)
class AuxmoneyExpensesValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Random
    private Expenses expenses;

    @ParameterizedTest
    @DisplayName("Should Return Validation Error For Missing  mandatory values")
    @ValueSource(strings = {
            "Other should be equal or grater than 0",
            "Debt Expenses should be equal or grater than 0",
            "Insurance and savings should be equal or grater than 0",
            "Rent and mortgage should be equal or grater than 0",
            "Total expenese should be equal or grater than 0",
            "Memberships should be equal or grater than 0"
    })
    void shouldValidateNonZeroValues(String errorMessage) {
        expenses.setOther(-1);
        expenses.setDebtExpenses(-1);
        expenses.setInsuranceAndSavings(-1);
        expenses.setRentAndMortgage(-1);
        expenses.setTotalExpenses(-1);
        expenses.setMemberships(-1);
        Set<ConstraintViolation<Expenses>> validate = validator.validate(expenses);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains(errorMessage))
                             .count();
        Assertions.assertEquals(1, count);
    }
    @ParameterizedTest
    @DisplayName("Should Not Return Validation Error For Missing  mandatory values")
    @ValueSource(strings = {
            "Other should be equal or grater than 0",
            "Debt Expenses should be equal or grater than 0",
            "Insurance and savings should be equal or grater than 0",
            "Rent and mortgage should be equal or grater than 0",
            "Total expenese should be equal or grater than 0",
            "Memberships should be equal or grater than 0"
    })
    void shouldValidatePositiveValues(String errorMessage) {
        expenses.setOther(0);
        expenses.setDebtExpenses(1);
        expenses.setInsuranceAndSavings(1);
        expenses.setRentAndMortgage(1);
        expenses.setTotalExpenses(1);
        expenses.setMemberships(1);
        Set<ConstraintViolation<Expenses>> validate = validator.validate(expenses);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains(errorMessage))
                             .count();
        Assertions.assertEquals(0, count);
    }
}
