package de.joonko.loan.partner.auxmoney.getoffers;

import de.joonko.loan.partner.auxmoney.model.AuxmoneyGetOffersRequest;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@ExtendWith(RandomBeansExtension.class)
class AuxmoneyGetOffersRequestValidationTest {

    @Random
    private AuxmoneyGetOffersRequest auxmoneyGetOffersRequest;

    private static Validator validator;


    @BeforeAll
    static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Should Return Validation Error For Null Duration")
    void shouldValidateLoanDuration() {
        auxmoneyGetOffersRequest.setDuration(null);
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long duration_is_mandatory = validate.stream()
                                             .filter(it -> it.getMessage().contains("Duration is mandatory"))
                                             .count();
        Assertions.assertEquals(1, duration_is_mandatory);

    }

    @Test
    @DisplayName("Should Not Return Validation Error For Valid Duration")
    void shouldNotReturnValidationError() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long duration_is_mandatory = validate.stream()
                                             .filter(it -> it.getMessage().contains("Duration is mandatory"))
                                             .count();
        Assertions.assertEquals(0, duration_is_mandatory);

    }

    @Test
    @DisplayName("Should Return Validation Error For Null Category")
    void shouldValidateLoanCategory() {
        auxmoneyGetOffersRequest.setCategory(null);
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long duration_is_mandatory = validate.stream()
                                             .filter(it -> it.getMessage().contains("Category is mandatory"))
                                             .count();
        Assertions.assertEquals(1, duration_is_mandatory);

    }

    @Test
    @DisplayName("Should Not Return Validation Error For Valid Category")
    void shouldNotReturnValidationErrorForValidCategory() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long duration_is_mandatory = validate.stream()
                                             .filter(it -> it.getMessage().contains("Category is mandatory"))
                                             .count();
        Assertions.assertEquals(0, duration_is_mandatory);

    }

    @Test
    @DisplayName("Should Return Validation Error For Null Collection Day")
    void shouldValidateCollectionDay() {
        auxmoneyGetOffersRequest.setCollectionDay(null);
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains("Collection day  is mandatory"))
                             .count();
        Assertions.assertEquals(1, count);

    }

    @Test
    @DisplayName("Should Not Return Validation Error For Valid Collection day")
    void shouldNotReturnValidationErrorForValidCollectionDay() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains("Collection day  is mandatory"))
                             .count();
        Assertions.assertEquals(0, count);

    }

    @Test
    @DisplayName("Should Return Validation Error For Null PERSONAL_DATA ")
    void shouldValidateNullPersonalDataCollectionDay() {
        auxmoneyGetOffersRequest.setPersonalData(null);
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains("Personal Data is Mandatory"))
                             .count();
        Assertions.assertEquals(1, count);

    }

    @Test
    @DisplayName("Should Not Return Validation Error For Valid Personal data")
    void shouldNotReturnValidationErrorForValidPersonalData() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains("Personal Data is Mandatory"))
                             .count();
        Assertions.assertEquals(0, count);

    }

    @Test
    @DisplayName("Should Return Validation Error For Null contact Data ")
    void shouldValidateNullContactDataCollectionDay() {
        auxmoneyGetOffersRequest.setContactData(null);
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains("Contact Data is Mandatory "))
                             .count();
        Assertions.assertEquals(1, count);
    }

    @Test
    @DisplayName("Should Not Return Validation Error For Valid contact data")
    void shouldNotReturnValidationErrorForValidContactData() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains("Contact Data is Mandatory "))
                             .count();
        Assertions.assertEquals(0, count);

    }
    @Test
    @DisplayName("Should Return Validation Error For Null Income Data ")
    void shouldValidateNullIncomeData() {
        auxmoneyGetOffersRequest.setIncome(null);
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains("Income Details are mandatory"))
                             .count();
        Assertions.assertEquals(1, count);
    }

    @Test
    @DisplayName("Should Not Return Validation Error For Valid Income data")
    void shouldNotReturnValidationErrorForValidIncomeData() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains("Income Details are mandatory"))
                             .count();
        Assertions.assertEquals(0, count);
    }
    @Test
    @DisplayName("Should Return Validation Error For Null expenses Data ")
    void shouldValidateNullExpensesData() {
        auxmoneyGetOffersRequest.setExpenses(null);
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains("Expenses Details are mandatory"))
                             .count();
        Assertions.assertEquals(1, count);
    }

    @Test
    @DisplayName("Should Not Return Validation Error For expenses data")
    void shouldNotReturnValidationErrorForValidExpenseData() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains("Expenses Details are mandatory"))
                             .count();
        Assertions.assertEquals(0, count);
    }

    @Test
    @DisplayName("Should Return Validation Error For Loan Amount Less Than 1000")
    void shouldReturnValidationErrorForLoanAountLessThan1000() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        auxmoneyGetOffersRequest.setLoanAsked(999);
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long duration_is_mandatory = validate.stream()
                                             .filter(it -> it.getMessage().contains("Loan Amount should be at least 1,000 and in increments of 100"))
                                             .count();
        Assertions.assertEquals(1, duration_is_mandatory);

    }

    @Test
    @DisplayName("Should Return Validation Error For Loan Amount more Than 50000")
    void shouldReturnValidationErrorForLoanAountMoreThan50000() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        auxmoneyGetOffersRequest.setLoanAsked(50001);
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long duration_is_mandatory = validate.stream()
                                             .filter(it -> it.getMessage().contains("Loan Amount should be at least 1,000 and in increments of 100"))
                                             .count();
        Assertions.assertEquals(1, duration_is_mandatory);

    }

    @Test
    @DisplayName("Should Return Validation Error For Loan Amount Not in Multiple of 100")
    void shouldReturnValidationErrorForLoanAountNotInMultipleOf100() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        auxmoneyGetOffersRequest.setLoanAsked(1050);
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long duration_is_mandatory = validate.stream()
                                             .filter(it -> it.getMessage().contains("Loan Amount should be at least 1,000 and in increments of 100"))
                                             .count();
        Assertions.assertEquals(1, duration_is_mandatory);

    }

    @Test
    @DisplayName("Should Not Return Validation Error For Valid Loan Amount")
    void shouldNotReturnValidationErrorForValidLoanAmount() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        auxmoneyGetOffersRequest.setLoanAsked(1000);
        Set<ConstraintViolation<AuxmoneyGetOffersRequest>> validate = validator.validate(auxmoneyGetOffersRequest);
        long duration_is_mandatory = validate.stream()
                                             .filter(it -> it.getMessage().contains("Loan Amount should be at least 1,000 and in increments of 100"))
                                             .count();
        Assertions.assertEquals(0, duration_is_mandatory);

    }


}
