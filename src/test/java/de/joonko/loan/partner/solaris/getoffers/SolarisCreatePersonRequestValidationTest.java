package de.joonko.loan.partner.solaris.getoffers;

import de.joonko.loan.partner.solaris.model.SolarisCreatePersonRequest;
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
public class SolarisCreatePersonRequestValidationTest {

    @Random
    private SolarisCreatePersonRequest solarisCreatePersonRequest;

    private static Validator validator;


    @BeforeAll
    static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Should Return Validation Error For Null salutation")
    void shouldReturnErrorForNullSalutation() {
        solarisCreatePersonRequest.setSalutation(null);
        Set<ConstraintViolation<SolarisCreatePersonRequest>> validate = validator.validate(solarisCreatePersonRequest);
        long salutation_is_mandatory = validate.stream()
                .filter(it -> it.getMessage().contains("Salutation cannot be null"))
                .count();
        Assertions.assertEquals(1, salutation_is_mandatory);
    }

    @Test
    @DisplayName("Should not Return Validation Error For valid salutation")
    void shouldNotReturnErrorForValidSalutation() {
        Set<ConstraintViolation<SolarisCreatePersonRequest>> validate = validator.validate(solarisCreatePersonRequest);
        long salutation_is_mandatory = validate.stream()
                .filter(it -> it.getMessage().contains("Salutation cannot be null"))
                .count();
        Assertions.assertEquals(0, salutation_is_mandatory);
    }

    @Test
    @DisplayName("Should Return Validation Error For Null firstName")
    void shouldReturnErrorForNullFirstName() {
        solarisCreatePersonRequest.setFirstName(null);
        Set<ConstraintViolation<SolarisCreatePersonRequest>> validate = validator.validate(solarisCreatePersonRequest);
        long firstName_is_mandatory = validate.stream()
                .filter(it -> it.getMessage().contains("First Name cannot be null"))
                .count();
        Assertions.assertEquals(1, firstName_is_mandatory);
    }

    @Test
    @DisplayName("Should not Return Validation Error For valid firstName")
    void shouldNotReturnErrorForValidFirstName() {
        Set<ConstraintViolation<SolarisCreatePersonRequest>> validate = validator.validate(solarisCreatePersonRequest);
        long firstName_is_mandatory = validate.stream()
                .filter(it -> it.getMessage().contains("First Name cannot be null"))
                .count();
        Assertions.assertEquals(0, firstName_is_mandatory);
    }

    @Test
    @DisplayName("Should Return Validation Error For Null lastName")
    void shouldReturnErrorForNullLastName() {
        solarisCreatePersonRequest.setLastName(null);
        Set<ConstraintViolation<SolarisCreatePersonRequest>> validate = validator.validate(solarisCreatePersonRequest);
        long lastName_is_mandatory = validate.stream()
                .filter(it -> it.getMessage().contains("Last name cannot be null"))
                .count();
        Assertions.assertEquals(1, lastName_is_mandatory);
    }

    @Test
    @DisplayName("Should not Return Validation Error For valid lastName")
    void shouldNotReturnErrorForValidLastName() {
        Set<ConstraintViolation<SolarisCreatePersonRequest>> validate = validator.validate(solarisCreatePersonRequest);
        long lastName_is_mandatory = validate.stream()
                .filter(it -> it.getMessage().contains("Last name cannot be null"))
                .count();
        Assertions.assertEquals(0, lastName_is_mandatory);
    }

    @Test
    @DisplayName("Should Return Validation Error For Null address")
    void shouldReturnErrorForNullAddress() {
        solarisCreatePersonRequest.setAddress(null);
        Set<ConstraintViolation<SolarisCreatePersonRequest>> validate = validator.validate(solarisCreatePersonRequest);
        long address_is_mandatory = validate.stream()
                .filter(it -> it.getMessage().contains("Address cannot be null"))
                .count();
        Assertions.assertEquals(1, address_is_mandatory);
    }

    @Test
    @DisplayName("Should not Return Validation Error For valid address")
    void shouldNotReturnErrorForValidAddress() {
        Set<ConstraintViolation<SolarisCreatePersonRequest>> validate = validator.validate(solarisCreatePersonRequest);
        long address_is_mandatory = validate.stream()
                .filter(it -> it.getMessage().contains("Address cannot be null"))
                .count();
        Assertions.assertEquals(0, address_is_mandatory);
    }

    @Test
    @DisplayName("Should Return Validation Error For Invalid email address")
    void shouldReturnErrorForInvalidEmailAddress() {
        solarisCreatePersonRequest.setEmail("example@");
        Set<ConstraintViolation<SolarisCreatePersonRequest>> validate = validator.validate(solarisCreatePersonRequest);
        long email_is_Invalid = validate.stream()
                .filter(it -> it.getMessage().contains("Email is invalid"))
                .count();
        Assertions.assertEquals(1, email_is_Invalid);
    }

    @Test
    @DisplayName("Should not Return Validation Error For valid email address")
    void shouldNotReturnErrorForValidEmailAddress() {
        solarisCreatePersonRequest.setEmail("example@something.com");
        Set<ConstraintViolation<SolarisCreatePersonRequest>> validate = validator.validate(solarisCreatePersonRequest);
        long email_is_valid = validate.stream()
                .filter(it -> it.getMessage().contains("Email is invalid"))
                .count();
        Assertions.assertEquals(0, email_is_valid);
    }

    @Test
    @DisplayName("Should Return Validation Error For null email address")
    void shouldReturnErrorForNullEmailAddress() {
        solarisCreatePersonRequest.setEmail(null);
        Set<ConstraintViolation<SolarisCreatePersonRequest>> validate = validator.validate(solarisCreatePersonRequest);
        long email_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Email must not be null"))
                .count();
        Assertions.assertEquals(1, email_is_null);
    }

    @Test
    @DisplayName("Should not Return Validation Error For non-null email address")
    void shouldNotReturnErrorForNonNullEmailAddress() {
        solarisCreatePersonRequest.setEmail("something@mail.com");
        Set<ConstraintViolation<SolarisCreatePersonRequest>> validate = validator.validate(solarisCreatePersonRequest);
        long email_is_notNull = validate.stream()
                .filter(it -> it.getMessage().contains("Email must not be null"))
                .count();
        Assertions.assertEquals(0, email_is_notNull);
    }

    @Test
    @DisplayName("Should Return Validation Error For null mobileNumber")
    void shouldReturnErrorForNullMobileNumber() {
        solarisCreatePersonRequest.setMobileNumber(null);
        Set<ConstraintViolation<SolarisCreatePersonRequest>> validate = validator.validate(solarisCreatePersonRequest);
        long mobile_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Mobile number must not be null"))
                .count();
        Assertions.assertEquals(1, mobile_is_null);
    }

    @Test
    @DisplayName("Should not Return Validation Error For valid mobileNumber")
    void shouldNotReturnErrorForvalidMobileNumber() {
        Set<ConstraintViolation<SolarisCreatePersonRequest>> validate = validator.validate(solarisCreatePersonRequest);
        long mobile_is_null = validate.stream()
                .filter(it -> it.getMessage().contains("Mobile number must not be null"))
                .count();
        Assertions.assertEquals(0, mobile_is_null);
    }

}
