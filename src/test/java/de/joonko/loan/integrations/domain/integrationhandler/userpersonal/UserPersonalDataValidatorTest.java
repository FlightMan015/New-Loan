package de.joonko.loan.integrations.domain.integrationhandler.userpersonal;

import de.joonko.loan.offer.api.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserPersonalDataValidatorTest {

    private static UserPersonalDataValidator validator;

    @BeforeAll
    static void beforeAll() {
        validator = new UserPersonalDataValidator();
    }

    @Test
    void passValidation() {
        // given
        UserPersonalData userPersonalData = getMinimalValidUserPersonalData();

        // when
        var passedValidation = validator.test(userPersonalData);

        // then
        assertTrue(passedValidation);
    }

    @Test
    void failValidation() {
        // given
        UserPersonalData userPersonalData = new UserPersonalData();

        // when
        var passedValidation = validator.test(userPersonalData);

        // then
        assertFalse(passedValidation);
    }

    private UserPersonalData getMinimalValidUserPersonalData() {
        return UserPersonalData.builder()
                .userUuid("userId")
                .bonifyUserId(123L)
                .personalDetails(PersonalDetails.builder()
                        .gender(Gender.MALE)
                        .firstName("Joonko")
                        .lastName("Finleap")
                        .familyStatus(FamilyStatus.MARRIED)
                        .birthDate(LocalDate.now()
                                .minusYears(25)
                                .minusDays(150))
                        .nationality(Nationality.DE)
                        .placeOfBirth("SOMEWHERE")
                        .numberOfChildren(0)
                        .housingType(HousingType.OWNER)
                        .numberOfCreditCard(1)
                        .countryOfBirth("DE")
                        .build())
                .contactData(ContactData.builder()
                        .streetName("Street Name")
                        .houseNumber("12")
                        .postCode("12345")
                        .city("City")
                        .livingSince(ShortDate.builder()
                                .month(01)
                                .year(2010)
                                .build())
                        .email("someone@joonko.io")
                        .mobile("491748273421011")
                        .build())
                .build();
    }
}
