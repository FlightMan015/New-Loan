package de.joonko.loan.userdata.domain.validator;

import de.joonko.loan.userdata.domain.model.UserData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserDataValidator {

    public UserData validateAndGet(@NotNull final UserData userData) {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            boolean validPersonal = false;
            boolean validContact = false;
            boolean validEmployment = false;
            boolean validHousing = false;
            boolean validCredit = false;
            Set<ConstraintViolation> validations = new HashSet<>();

            if (userData.getUserPersonal() != null) {
                var sectionValidations = validator.validate(userData.getUserPersonal());
                if (sectionValidations.isEmpty()) {
                    validPersonal = true;
                    userData.getUserPersonal().setValid(true);
                } else {
                    validations.addAll(sectionValidations);
                }
            }

            if (userData.getUserContact() != null) {
                var sectionValidations = validator.validate(userData.getUserContact());
                if (sectionValidations.isEmpty()) {
                    validContact = true;
                    userData.getUserContact().setValid(true);
                } else {
                    validations.addAll(sectionValidations);
                }
            }

            if (userData.getUserEmployment() != null) {
                var sectionValidations = validator.validate(userData.getUserEmployment());
                if (sectionValidations.isEmpty()) {
                    validEmployment = true;
                    userData.getUserEmployment().setValid(true);
                } else {
                    validations.addAll(sectionValidations);
                }
            }

            if (userData.getUserHousing() != null) {
                var sectionValidations = validator.validate(userData.getUserHousing());
                if (sectionValidations.isEmpty()) {
                    validHousing = true;
                    userData.getUserHousing().setValid(true);
                } else {
                    validations.addAll(sectionValidations);
                }
            }

            if (userData.getUserCredit() != null) {
                var sectionValidations = validator.validate(userData.getUserCredit());
                if (sectionValidations.isEmpty()) {
                    validCredit = true;
                    userData.getUserCredit().setValid(true);
                } else {
                    validations.addAll(sectionValidations);
                }
            }

            log.debug("User data validation: personal: {}, contact: {}, employment: {}, housing: {}, credit: {}, " +
                    "caused by: {}", validPersonal, validContact, validEmployment, validHousing, validCredit, validations);
        }

        return userData;
    }
}
