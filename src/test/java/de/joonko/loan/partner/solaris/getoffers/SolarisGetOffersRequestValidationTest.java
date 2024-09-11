package de.joonko.loan.partner.solaris.getoffers;

import de.joonko.loan.partner.solaris.model.SolarisGetOffersRequest;
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
public class SolarisGetOffersRequestValidationTest {

    @Random
    private SolarisGetOffersRequest solarisGetOffersRequest;

    private static Validator validator;


    @BeforeAll
    static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Should Return Validation Error For Null partnerReferenceNumber")
    void shouldReturnErrorForNullPartnerRefNumber() {
        solarisGetOffersRequest.setPartnerReferenceNumber(null);
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long partnerReferenceNumber_is_mandatory = validate.stream()
                .filter(it -> it.getMessage().contains("Partner reference Number cannot be null"))
                .count();
        Assertions.assertEquals(1, partnerReferenceNumber_is_mandatory);
    }

    @Test
    @DisplayName("Should not Return Validation Error For valid partnerReferenceNumber")
    void shouldNotReturnErrorForValidPartnerRefNumber() {
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long partnerReferenceNumber_is_mandatory = validate.stream()
                .filter(it -> it.getMessage().contains("Partner reference Number cannot be null"))
                .count();
        Assertions.assertEquals(0, partnerReferenceNumber_is_mandatory);
    }

    @Test
    @DisplayName("Should Return Validation Error For Null requestedLoanAmount")
    void shouldReturnErrorForNullRequestedLoanAmount() {
        solarisGetOffersRequest.setRequestedLoanAmount(null);
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long requestedLoanAmount_is_mandatory = validate.stream()
                .filter(it -> it.getMessage().contains("Loan asked cannot be null or empty"))
                .count();
        Assertions.assertEquals(1, requestedLoanAmount_is_mandatory);
    }

    @Test
    @DisplayName("Should Return Validation Error For invalid requestedLoanAmount")
    void shouldReturnErrorForInvalidlRequestedLoanAmount() {
        solarisGetOffersRequest.getRequestedLoanAmount().setValue(0);
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long requestedLoanAmount_is_valid = validate.stream()
                .filter(it -> it.getMessage().contains("Amount should be greater than 0"))
                .count();
        Assertions.assertEquals(1, requestedLoanAmount_is_valid);
    }

    @Test
    @DisplayName("Should not Return Validation Error For valid requestedLoanAmount")
    void shouldNotReturnErrorForValidlRequestedLoanAmount() {

        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long requestedLoanAmount_is_valid = validate.stream()
                .filter(it -> it.getMessage().contains("Amount should be greater than 0"))
                .count();
        Assertions.assertEquals(0, requestedLoanAmount_is_valid);
    }

    @Test
    @DisplayName("Should Return Validation Error For null duration")
    void shouldReturnErrorForNullDuration() {
        solarisGetOffersRequest.setDuration(null);
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long duration_is_invalid = validate.stream()
                .filter(it -> it.getMessage().contains("Loan duration cannot be null"))
                .count();
        Assertions.assertEquals(1, duration_is_invalid);
    }

    @Test
    @DisplayName("Should not Return Validation Error For valid duration")
    void shouldNotReturnErrorForValidDuration() {
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long duration_is_invalid = validate.stream()
                .filter(it -> it.getMessage().contains("Loan duration cannot be null"))
                .count();
        Assertions.assertEquals(0, duration_is_invalid);
    }

    @Test
    @DisplayName("Should Return Validation Error For null Iban")
    void shouldReturnErrorForInValidIban() {
        solarisGetOffersRequest.setRecipientIban(null);
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long iban_is_invalid = validate.stream()
                .filter(it -> it.getMessage().contains("IBAN provided is invalid"))
                .count();
        Assertions.assertEquals(1, iban_is_invalid);
    }

    @Test
    @DisplayName("Should not Return Validation Error For valid Iban")
    void shouldNotReturnErrorForValidIban() {
        solarisGetOffersRequest.setRecipientIban("DE58500105173951166596");
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long iban_is_invalid = validate.stream()
                .filter(it -> it.getMessage().contains("IBAN provided is invalid"))
                .count();
        Assertions.assertEquals(0, iban_is_invalid);
    }

    @Test
    @DisplayName("Should Return Validation Error For null loanPurpose")
    void shouldReturnErrorForNullLoanPurpose() {
        solarisGetOffersRequest.setLoanPurpose(null);
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long loanPurpose_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Loan purpose cannot be null"))
                .count();
        Assertions.assertEquals(1, loanPurpose_is_null);
    }

    @Test
    @DisplayName("Should not Return Validation Error For valid loanPurpose")
    void shouldNotReturnErrorForValidLoanPurpose() {
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long loanPurpose_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Loan purpose cannot be null"))
                .count();
        Assertions.assertEquals(0, loanPurpose_is_null);
    }

    @Test
    @DisplayName("Should Return Validation Error For null livingSituation")
    void shouldReturnErrorForNullLivingSituation() {
        solarisGetOffersRequest.setLivingSituation(null);
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long livingSituation_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Living Situation cannot be null"))
                .count();
        Assertions.assertEquals(1, livingSituation_is_null);
    }

    @Test
    @DisplayName("Should Not Return Validation Error For valid livingSituation")
    void shouldNotReturnErrorForvalidLivingSituation() {
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long livingSituation_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Living Situation cannot be null"))
                .count();
        Assertions.assertEquals(0, livingSituation_is_null);
    }

    @Test
    @DisplayName("Should Return Validation Error For null hasMovedInLastTwoYears")
    void shouldReturnErrorForNullHasMovedInLastTwoYears() {
        solarisGetOffersRequest.setHasMovedInLastTwoYears(null);
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long hasMovedInLastTwoYears_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Moved in last two years cannot be null"))
                .count();
        Assertions.assertEquals(1, hasMovedInLastTwoYears_is_null);
    }

    @Test
    @DisplayName("Should not Return Validation Error For valid hasMovedInLastTwoYears")
    void shouldNotReturnErrorForValidHasMovedInLastTwoYears() {
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long hasMovedInLastTwoYears_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Moved in last two years cannot be null"))
                .count();
        Assertions.assertEquals(0, hasMovedInLastTwoYears_is_null);
    }

    @Test
    @DisplayName("Should Return Validation Error For null rent")
    void shouldReturnErrorForNullRent() {
        solarisGetOffersRequest.setRent(null);
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long rent_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Rent paid cannot be null"))
                .count();
        Assertions.assertEquals(1, rent_is_null);
    }

    @Test
    @DisplayName("Should not eturn Validation Error For valid rent")
    void shouldNotReturnErrorForValidRent() {
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long rent_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Rent paid cannot be null"))
                .count();
        Assertions.assertEquals(0, rent_is_null);
    }

    @Test
    @DisplayName("Should return Validation Error For null additionalCosts")
    void shouldReturnErrorForNullAdditionalCosts() {
        solarisGetOffersRequest.setAdditionalCosts(null);
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long additionalCosts_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Additional cost cannot be null"))
                .count();
        Assertions.assertEquals(1, additionalCosts_is_null);
    }

    @Test
    @DisplayName("Should not return Validation Error For valid additionalCosts")
    void shouldNotReturnErrorForValidAdditionalCosts() {
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long additionalCosts_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Additional cost cannot be null"))
                .count();
        Assertions.assertEquals(0, additionalCosts_is_null);
    }

    @Test
    @DisplayName("Should return Validation Error For null livingSituationAmount")
    void shouldReturnErrorForNullLivingSituationAmount() {
        solarisGetOffersRequest.setLivingSituationAmount(null);
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long livingSituationAmount_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Living Situation Amount cannot be null"))
                .count();
        Assertions.assertEquals(1, livingSituationAmount_is_null);
    }

    @Test
    @DisplayName("Should not return Validation Error For valid livingSituationAmount")
    void shouldNotReturnErrorForValidLivingSituationAmount() {
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long livingSituationAmount_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Living Situation Amount cannot be null"))
                .count();
        Assertions.assertEquals(0, livingSituationAmount_is_null);
    }

    @Test
    @DisplayName("Should return Validation Error For null netIncomeAmount")
    void shouldReturnErrorForNullNetIncomeAmount() {
        solarisGetOffersRequest.setNetIncomeAmount(null);
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long netIncomeAmount_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Net income amount cannot be null"))
                .count();
        Assertions.assertEquals(1, netIncomeAmount_is_null);
    }

    @Test
    @DisplayName("Should not return Validation Error For valid netIncomeAmount")
    void shouldNotReturnErrorForValidNetIncomeAmount() {
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long netIncomeAmount_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Net income amount cannot be null"))
                .count();
        Assertions.assertEquals(0, netIncomeAmount_is_null);
    }

    @Test
    @DisplayName("Should return Validation Error For null numberOfDependents")
    void shouldReturnErrorForNullNumberOfDependents() {
        solarisGetOffersRequest.setNumberOfDependents(null);
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long numberOfDependents_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Number of dependents cannot be null"))
                .count();
        Assertions.assertEquals(1, numberOfDependents_is_null);
    }

    @Test
    @DisplayName("Should not return Validation Error For valid numberOfDependents")
    void shouldNotReturnErrorForValidNumberOfDependents() {
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long numberOfDependents_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Number of dependents cannot be null"))
                .count();
        Assertions.assertEquals(0, numberOfDependents_is_null);
    }

    @Test
    @DisplayName("Should return Validation Error For null repaymentDayOfMonth")
    void shouldReturnErrorForNullRepaymentDayOfMonth() {
        solarisGetOffersRequest.setRepaymentDayOfMonth(null);
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long repaymentDayOfMonth_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Repayment day of month cannot be null"))
                .count();
        Assertions.assertEquals(1, repaymentDayOfMonth_is_null);
    }

    @Test
    @DisplayName("Should not return Validation Error For valid repaymentDayOfMonth")
    void shouldNotReturnErrorForValidRepaymentDayOfMonth() {
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long repaymentDayOfMonth_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Repayment day of month cannot be null"))
                .count();
        Assertions.assertEquals(0, repaymentDayOfMonth_is_null);
    }

    @Test
    @DisplayName("Should return Validation Error For null shouldSolarisBankGenerateContract")
    void shouldReturnErrorForNullShouldSolarisBankGenerateContract() {
        solarisGetOffersRequest.setShouldSolarisBankGenerateContract(null);
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long shouldSolarisBankGenerateContract_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Solaris generate contract cannot be null"))
                .count();
        Assertions.assertEquals(1, shouldSolarisBankGenerateContract_is_null);
    }

    @Test
    @DisplayName("Should not return Validation Error For valid shouldSolarisBankGenerateContract")
    void shouldNotReturnErrorForValidShouldSolarisBankGenerateContract() {
        Set<ConstraintViolation<SolarisGetOffersRequest>> validate = validator.validate(solarisGetOffersRequest);
        long shouldSolarisBankGenerateContract_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Solaris generate contract cannot be null"))
                .count();
        Assertions.assertEquals(0, shouldSolarisBankGenerateContract_is_null);
    }

}
