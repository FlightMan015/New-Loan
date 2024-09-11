package de.joonko.loan.partner.solaris.validator;

import de.joonko.loan.partner.solaris.model.AmountValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AmountValidator implements ConstraintValidator<ValidLoanAmount, AmountValue> {
    @Override
    public boolean isValid(AmountValue amountValue, ConstraintValidatorContext context) {
        return amountValue !=null && amountValue.getValue() !=null && amountValue.getValue() != 0;
    }
}
