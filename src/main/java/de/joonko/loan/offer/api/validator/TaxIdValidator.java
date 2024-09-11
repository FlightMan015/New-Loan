package de.joonko.loan.offer.api.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TaxIdValidator implements
        ConstraintValidator<ValidTaxId, String> {

    @Override
    public void initialize(ValidTaxId taxId) {
    }

    @Override
    public boolean isValid(String taxId,
                           ConstraintValidatorContext cxt) {
        return (taxId == null) ||
                (taxId.matches("(\\d{11})"));
    }
}
