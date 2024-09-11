package de.joonko.loan.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IbanValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIBAN {
    String message() default "IBAN provided is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
