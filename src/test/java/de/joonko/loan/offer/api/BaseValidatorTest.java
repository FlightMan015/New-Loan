package de.joonko.loan.offer.api;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public abstract class BaseValidatorTest {
    protected static Validator validator;
    protected static String LONG_NAME = "Joonajsdghaksbjdhajksdhknasjhdkajsnajkshdjashdkjnhajksdhkajshdkjhaskjdh"; // 71 chars

    @BeforeAll
    public static void setup() {
        validator = Validation.buildDefaultValidatorFactory()
                .getValidator();
    }

    protected void validateErrorMessage(Object object, String s) {
        Set<ConstraintViolation<Object>> validate = validator.validate(object);
        boolean hasErrorMessage = validate.stream()
                .map(it -> it.getMessage())
                .anyMatch(it -> it.equalsIgnoreCase(s));
        String msg = validate.iterator().next().getMessage();
        Assert.assertTrue("Expecting error message " + s, hasErrorMessage);

    }

    protected void validateNoError(Object object) {
        Set<ConstraintViolation<Object>> validate = validator.validate(object);
        Assert.assertTrue("Found Errors" + validate.toString(), validate.isEmpty());
    }

    protected void validateNumberOfErrors(Object object, int numberOfErrors) {
        Set<ConstraintViolation<Object>> validate = validator.validate(object);
        Assert.assertEquals(numberOfErrors, validate.size());
    }
}
