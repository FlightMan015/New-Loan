package de.joonko.loan.partner.auxmoney;

import de.joonko.loan.partner.auxmoney.model.BankData;
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
class AuxmoneyAcceptOfferGatewayBankDataRequestValidationTest {

    @Random
    private BankData bankData;

    private static Validator validator;


    @BeforeAll
    static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Should Return Validation Error For Null Iban")
    void shouldValidateNullIban() {
        bankData.setIban(null);
        Set<ConstraintViolation<BankData>> validate = validator.validate(bankData);
        long ibanIsMandatory = validate.stream()
                .filter(it -> it.getMessage().contains("IBAN provided is invalid"))
                .count();
        Assertions.assertEquals(1, ibanIsMandatory);
    }

    @Test
    @DisplayName("Should Return Validation Error For Non-Null but invalid Iban")
    void shouldValidateNonNullButInvalidIban() {
        bankData.setIban("DE78XXXkk");
        Set<ConstraintViolation<BankData>> validate = validator.validate(bankData);
        long ibanIsMandatory = validate.stream()
                .filter(it -> it.getMessage().contains("IBAN provided is invalid"))
                .count();
        Assertions.assertEquals(1, ibanIsMandatory);
    }

    @Test
    @DisplayName("Should not return validation Error For Valid Iban")
    void shouldValidateValidIban() {
        bankData.setIban("DE07500105173474826898");
        Set<ConstraintViolation<BankData>> validate = validator.validate(bankData);
        long ibanIsMandatory = validate.stream()
                .filter(it -> it.getMessage().contains("IBAN provided is invalid"))
                .count();
        Assertions.assertEquals(0, ibanIsMandatory);
    }

    @Test
    @DisplayName("returns validation error for null Bic")
    void shouldValidateNullBic() {
        bankData.setBic(null);
        Set<ConstraintViolation<BankData>> validate = validator.validate(bankData);
        long bicIsMandatory = validate.stream()
                .filter(it -> it.getMessage().contains("Bic is mandatory"))
                .count();
        Assertions.assertEquals(1, bicIsMandatory);
    }

    @Test
    @DisplayName("Should not return validation Error For Valid Bic")
    void shouldValidateValidBic() {
        Set<ConstraintViolation<BankData>> validate = validator.validate(bankData);
        long bicIsMandatory = validate.stream()
                .filter(it -> it.getMessage().contains("Bic is mandatory"))
                .count();
        Assertions.assertEquals(0, bicIsMandatory);
    }
}
