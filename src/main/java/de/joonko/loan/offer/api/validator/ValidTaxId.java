package de.joonko.loan.offer.api.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TaxIdValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTaxId {

    String message() default "Invalid Tax ID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
