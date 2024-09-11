package de.joonko.loan.partner.auxmoney.getoffers;

import de.joonko.loan.partner.auxmoney.model.BorrowerContactData;
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
class AuxmoneyContactDataValidation {

    private static Validator validator;
    @Random
    private BorrowerContactData borrowerContactData;

    @BeforeAll
    static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }


    @ParameterizedTest
    @DisplayName("Should Return Validation Error For Missing  mandatory values")
    @ValueSource(strings = {"Street Name is mandatory",
            "Street Number is mandatory",
            "ZipCode is mandatory",
            "City is mandatory",
            "Telephone is mandatory",
            "Email is mandatory"
    })
    void shouldValidateNullValues(String errorMessage) {
        borrowerContactData.setStreetName(null);
        borrowerContactData.setStreetNumber(null);
        borrowerContactData.setZipCode(null);
        borrowerContactData.setCity(null);
        borrowerContactData.setTelephone(null);
        borrowerContactData.setEmail(null);
        Set<ConstraintViolation<BorrowerContactData>> validate = validator.validate(borrowerContactData);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains(errorMessage))
                             .count();
        Assertions.assertEquals(1, count);
    }

    @ParameterizedTest
    @DisplayName("Should Not Return Validation Error if mandatory values are exsits")
    @ValueSource(strings = {"Street Name is mandatory",
            "Street Number is mandatory",
            "ZipCode is mandatory",
            "City is mandatory",
            "Telephone is mandatory",
            "Email is mandatory"
    })
    void shouldValidateNonNullValues(String errorMessage) {

        Set<ConstraintViolation<BorrowerContactData>> validate = validator.validate(borrowerContactData);
        long count = validate.stream()
                             .filter(it -> it.getMessage().contains(errorMessage))
                             .count();
        Assertions.assertEquals(0, count);
    }

}
