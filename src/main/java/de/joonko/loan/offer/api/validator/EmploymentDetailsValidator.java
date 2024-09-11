package de.joonko.loan.offer.api.validator;

import de.joonko.loan.offer.api.EmploymentDetails;
import de.joonko.loan.offer.api.EmploymentType;
import lombok.extern.slf4j.Slf4j;

import javax.validation.*;
import java.util.Set;

@Slf4j
public class EmploymentDetailsValidator implements ConstraintValidator<ValidEmploymentDetails, EmploymentDetails> {

    public boolean isValid(EmploymentDetails employmentDetails, ConstraintValidatorContext context) {
        if (null == employmentDetails) {
            return false;
        }
        if (employmentDetails.getEmploymentType()
                .equals(EmploymentType.OTHER)) {
            return true;
        } else if (employmentDetails.getEmploymentType()
                .equals(EmploymentType.REGULAR_EMPLOYED)) {
            Validator validator = Validation.buildDefaultValidatorFactory()
                    .getValidator();
            Set<ConstraintViolation<EmploymentDetails>> validate = validator.validate(employmentDetails);
            validate.stream()
                    .map(it -> it.getMessage())
                    .forEach(it -> {
                        context.buildConstraintViolationWithTemplate(it)
                                .addConstraintViolation();
                    });
            return validate.isEmpty();
        }
        return false;

    }
}
