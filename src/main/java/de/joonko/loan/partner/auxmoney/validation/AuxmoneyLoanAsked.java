package de.joonko.loan.partner.auxmoney.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AuxmoneyLoanAskedValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuxmoneyLoanAsked {
    String message() default "Loan Amount should be at least 1,000 and in increments of 100";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
