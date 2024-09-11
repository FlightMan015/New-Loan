package de.joonko.loan.partner.auxmoney.getoffers;

import de.joonko.loan.partner.auxmoney.model.PersonalData;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@ExtendWith(RandomBeansExtension.class)
class AuxmoneyPersonalDataValidationTest {

    private static Validator validator;


    @BeforeAll
    static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Random
    private PersonalData personalData;


    @ParameterizedTest
    @DisplayName("Should Return Validation Error For Missing  mandatory values")
    @ValueSource(strings = {"Address is Mandatory",
            "Family Status is mandatory",
            "Birth Date is mandatory",
            "Has Credit card is mandatory",
            "Has ral eastate is mandatory",
            "Forename is mandatory",
            "Nationality is mandatory",
            "Has ec card is mandatory",
            "Surname is mandatory",
            "Main earner is mandatory"
    })
    void shouldValidateNullValues(String errorMessage) {
        personalData.setAddress(null);
        personalData.setFamilyStatus(null);
        personalData.setBirthDate(null);
        personalData.setHasCreditCard(null);
        personalData.setHasRealEstate(null);
        personalData.setForename(null);
        personalData.setNationality(null);
        personalData.setHasEcCard(null);
        personalData.setSurname(null);
        personalData.setMainEarner(null);
        Set<ConstraintViolation<PersonalData>> validate = validator.validate(personalData);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains(errorMessage))
                             .count();
        Assertions.assertEquals(1, count);
    }

    @ParameterizedTest
    @DisplayName("Should Not Return Validation Error If mandatory values are present")
    @ValueSource(strings = {"Address is Mandatory",
            "Family Status is mandatory",
            "Birth Date is mandatory",
            "Has Credit card is mandatory",
            "Has ral eastate is mandatory",
            "Forename is mandatory",
            "Nationlity is mandatory",
            "Has ec card is mandatory",
            "Surname is mandatory",
            "Main earner is mandatory"
    })
    void shouldValidateNotNullValues(String errorMessage) {
        Set<ConstraintViolation<PersonalData>> validate = validator.validate(personalData);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains(errorMessage))
                             .count();
        Assertions.assertEquals(0, count);
    }

}
