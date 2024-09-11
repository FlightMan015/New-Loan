package de.joonko.loan.integrations.domain.integrationhandler.userpersonal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.*;
import java.util.Set;
import java.util.function.Predicate;

@Slf4j
@Component
public class UserPersonalDataValidator implements Predicate<UserPersonalData> {
    @Override
    public boolean test(UserPersonalData userPersonalData) {
        try {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<UserPersonalData>> validate = validator.validate(userPersonalData);
            if (!validate.isEmpty()) {
                log.info("Validation failed for userId: {}, caused by: {}", userPersonalData.getUserUuid(), validate);
                return false;
            } else {
                return true;
            }
        } catch (ValidationException ex) {
            log.info("Validation failed for for userId {}, caused by: {}", userPersonalData.getUserUuid(), ex);
            return false;
        }
    }
}
