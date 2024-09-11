package de.joonko.loan.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.IBANValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class IbanValidator implements ConstraintValidator<ValidIBAN, String> {

    public boolean isValid(String iban, ConstraintValidatorContext context) {
        log.debug("Validating Iban {}", iban);
        IBANValidator ibanValidator = IBANValidator.getInstance();

        return ibanValidator.isValid(iban);

    }
}
