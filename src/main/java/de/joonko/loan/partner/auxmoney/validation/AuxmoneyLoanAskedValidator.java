package de.joonko.loan.partner.auxmoney.validation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static de.joonko.loan.util.AuxmoneyConstant.*;

@Slf4j
public class AuxmoneyLoanAskedValidator implements ConstraintValidator<AuxmoneyLoanAsked, Integer> {

    public boolean isValid(Integer loanAsked, ConstraintValidatorContext context) {
        log.debug("Validating Loan Amount For {}", loanAsked);
        return MINIMUM_LOAN_AMOUNT_PREDICATE
                .and(MAX_LOAN_AMOUNT_PREDICATE)
                .and(INCREMENTS_OF_100)
                .test(loanAsked);
    }
}
