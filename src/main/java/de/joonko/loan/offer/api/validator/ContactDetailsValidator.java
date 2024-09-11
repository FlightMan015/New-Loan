package de.joonko.loan.offer.api.validator;

import de.joonko.loan.offer.api.ContactData;
import de.joonko.loan.offer.api.PreviousAddress;
import lombok.extern.slf4j.Slf4j;

import javax.validation.*;
import java.util.Set;

import static de.joonko.loan.offer.api.ApiConstraintConstants.MINIMUM_DURATION_STAY_IN_MONTH;

@Slf4j
public class ContactDetailsValidator implements ConstraintValidator<ValidContactData, ContactData> {
    Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();

    public boolean isValid(ContactData contactData, ConstraintValidatorContext context) {
        if (null == contactData || !validateCurrentAddress(contactData, context)) {
            return false;
        }
        boolean isPreviousAddressValidationNeeded = contactData.getLivingSince()
                .isOlderThanMonth(MINIMUM_DURATION_STAY_IN_MONTH);
        if (!isPreviousAddressValidationNeeded) {
            return validatePreviousAddress(contactData, context);
        }
        return true;
    }

    public boolean validatePreviousAddress(ContactData contactData, ConstraintValidatorContext context) {
        if (null == contactData.getPreviousAddress()) {
            context.buildConstraintViolationWithTemplate("PreviousAddress must not be null if living since is not older than 24 months")
                    .addConstraintViolation();
            return false;

        }
        Set<ConstraintViolation<PreviousAddress>> previousAddressValidate = validator.validate(contactData.getPreviousAddress());
        updateContext(context, previousAddressValidate);
        return previousAddressValidate.isEmpty();
    }

    private void updateContext(ConstraintValidatorContext context, Set<ConstraintViolation<PreviousAddress>> previousAddressValidate) {
        previousAddressValidate.stream()
                .map(it -> it.getMessage())
                .forEach(it -> {
                    context.buildConstraintViolationWithTemplate(it)
                            .addConstraintViolation();
                });
    }

    private boolean validateCurrentAddress(ContactData contactData, ConstraintValidatorContext context) {
        Set<ConstraintViolation<ContactData>> validate = validator.validate(contactData);
        updateContextForContactData(context, validate);
        return validate.isEmpty();

    }

    private void updateContextForContactData(ConstraintValidatorContext context, Set<ConstraintViolation<ContactData>> validate) {
        validate.stream()
                .map(it -> it.getMessage())
                .forEach(it -> {
                    context.buildConstraintViolationWithTemplate(it)
                            .addConstraintViolation();
                });
    }
}
