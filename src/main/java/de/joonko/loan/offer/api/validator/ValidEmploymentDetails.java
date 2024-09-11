package de.joonko.loan.offer.api.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmploymentDetailsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmploymentDetails {
    String message() default "Employment Details are invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
