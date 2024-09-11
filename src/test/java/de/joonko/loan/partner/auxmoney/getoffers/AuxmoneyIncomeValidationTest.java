package de.joonko.loan.partner.auxmoney.getoffers;

import de.joonko.loan.partner.auxmoney.model.Income;
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
class AuxmoneyIncomeValidationTest {

    private static Validator validator;
    @BeforeAll
    static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Random
    private Income income;

    @ParameterizedTest
    @DisplayName("Should Return Validation Error For Missing  mandatory values")
    @ValueSource(strings = {"Child Benifits should be equal or grater than 0",
            "Total should be equal or grater than 0",
            "Other should be equal or grater than 0",
            "Net Income should be equal or grater than 0"
    })
    void shouldValidateNonZeroValues(String errorMessage) {
        income.setBenefit(-1);
        income.setNet(-1);
        income.setOther(-1);
        income.setTotal(-1);
        Set<ConstraintViolation<Income>> validate = validator.validate(income);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains(errorMessage))
                             .count();
        Assertions.assertEquals(1, count);
    }

    @ParameterizedTest
    @DisplayName("Should Not Return Validation Error For Missing  mandatory values")
    @ValueSource(strings = {"Child Benifits should be equal or grater than 0",
            "Total should be equal or grater than 0",
            "Other should be equal or grater than 0",
            "Net Income should be equal or grater than 0"
    })
    void shouldValidatePositiveValues(String errorMessage) {
        income.setBenefit(1);
        income.setNet(0);
        income.setOther(11);
        income.setTotal(1);
        Set<ConstraintViolation<Income>> validate = validator.validate(income);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains(errorMessage))
                             .count();
        Assertions.assertEquals(0, count);
    }
}
