package de.joonko.loan.partner.solaris.acceptoffer;

import de.joonko.loan.partner.solaris.model.SolarisAcceptOfferRequest;
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
public class SolarisAcceptOfferRequestValidationTest {

    @Random
    private SolarisAcceptOfferRequest solarisAcceptOfferRequest;

    private static Validator validator;

    @BeforeAll
    static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Should Return Validation Error For Null loanAsked")
    void shouldReturnErrorForNullAccountSnapshot() {
        solarisAcceptOfferRequest.setLoanAsked(null);
        Set<ConstraintViolation<SolarisAcceptOfferRequest>> validate = validator.validate(solarisAcceptOfferRequest);
        long loanAsked_is_mandatory = validate.stream()
                .filter(it -> it.getMessage().contains("loanAsked is Mandatory"))
                .count();
        Assertions.assertEquals(1, loanAsked_is_mandatory);
    }

    @Test
    @DisplayName("Should not Return Validation Error For valid loanAsked")
    void shouldNotReturnErrorForValidAccountSnapshot() {
        Set<ConstraintViolation<SolarisAcceptOfferRequest>> validate = validator.validate(solarisAcceptOfferRequest);
        long loanAsked_is_mandatory = validate.stream()
                .filter(it -> it.getMessage().contains("loanAsked is Mandatory"))
                .count();
        Assertions.assertEquals(0, loanAsked_is_mandatory);
    }

    @Test
    @DisplayName("Should Return Validation Error For Null duration")
    void shouldReturnErrorForNullDuration() {
        solarisAcceptOfferRequest.setDuration(null);
        Set<ConstraintViolation<SolarisAcceptOfferRequest>> validate = validator.validate(solarisAcceptOfferRequest);
        long duration_is_mandatory = validate.stream()
                .filter(it -> it.getMessage().contains("duration is Mandatory"))
                .count();
        Assertions.assertEquals(1, duration_is_mandatory);
    }

    @Test
    @DisplayName("Should not Return Validation Error For valid duration")
    void shouldNotReturnErrorForValidDuration() {
        Set<ConstraintViolation<SolarisAcceptOfferRequest>> validate = validator.validate(solarisAcceptOfferRequest);
        long duration_is_mandatory = validate.stream()
                .filter(it -> it.getMessage().contains("duration is Mandatory"))
                .count();
        Assertions.assertEquals(0, duration_is_mandatory);
    }
}
