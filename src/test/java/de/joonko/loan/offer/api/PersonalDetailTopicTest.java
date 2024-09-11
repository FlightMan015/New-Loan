package de.joonko.loan.offer.api;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.stream.Stream;

public class PersonalDetailTopicTest extends BaseValidatorTest {

    @ParameterizedTest
    @MethodSource("creatInvalidPersonalDetails")
    void validatePersonalDetails(PersonalDetails personalDetails, String errorMessage) {
        validateErrorMessage(personalDetails, errorMessage);
    }

    static Stream<Arguments> creatInvalidPersonalDetails() {
        return Stream.of(
                Arguments.of(getPersonalDetailsWithMissingGender(), "Gender must not be null"),
                Arguments.of(getMissingFirstName(), "First name must not be null"),
                Arguments.of(getFirstNameInvalidFormat(), "First name is in invalid format"),
                Arguments.of(getFirstNameTooLong(), "First name is too long"),
                Arguments.of(getMissingLastName(), "Last name must not be null"),
                Arguments.of(getLastNameInvalidFormat(), "Last name is in invalid format"),
                Arguments.of(getLastNameTooLong(), "Last name is too long"),
                Arguments.of(getPersonalDetailsWithMissingDateOfBirth(), "Birth date must not be null"),
                Arguments.of(getDateOfBirthLessThan18Years(), "Age not in valid range"),
                Arguments.of(getDateOfBirthMoreThan100Years(), "Age not in valid range"),
                Arguments.of(getPersonalDetailsWithNationality(), "Nationality must not be null"),
                Arguments.of(getPersonalDetailsMissingFamilyStatus(), "Family status must not be null"),
                Arguments.of(getPersonalDetailsWithMissingPlaceOfBirth(), "Place of Birth must not be null"),
                Arguments.of(getMissingHousingType(), "HousingType must not be null"),
                Arguments.of(getMissingNumberOfChildren(), "Number of Children must not be null")
        );
    }

    private static PersonalDetails getMissingFirstName() {
        PersonalDetails missingFirstName = LoanDemandFixtures.getPersonalDetails();
        missingFirstName
                .setFirstName(null);
        return missingFirstName;
    }

    private static PersonalDetails getMissingLastName() {
        PersonalDetails missingLastName = LoanDemandFixtures.getPersonalDetails();
        missingLastName
                .setLastName(null);
        return missingLastName;
    }

    @NotNull
    private static PersonalDetails getPersonalDetailsWithMissingGender() {
        PersonalDetails personalDetails = LoanDemandFixtures.getPersonalDetails();
        personalDetails.setGender(null);
        return personalDetails;
    }

    @NotNull
    private static PersonalDetails getPersonalDetailsWithMissingPlaceOfBirth() {
        PersonalDetails personalDetails = LoanDemandFixtures.getPersonalDetails();
        personalDetails.setPlaceOfBirth(null);
        return personalDetails;
    }

    @NotNull
    private static PersonalDetails getPersonalDetailsWithNationality() {
        PersonalDetails personalDetails = LoanDemandFixtures.getPersonalDetails();
        personalDetails.setNationality(null);
        return personalDetails;
    }

    private static PersonalDetails getPersonalDetailsWithMissingDateOfBirth() {
        PersonalDetails missingDateOfBirth = LoanDemandFixtures.getPersonalDetails();
        missingDateOfBirth
                .setBirthDate(null);
        return missingDateOfBirth;
    }

    private static PersonalDetails getPersonalDetailsMissingFamilyStatus() {
        PersonalDetails missingFamilyStatus = LoanDemandFixtures.getPersonalDetails();
        missingFamilyStatus
                .setFamilyStatus(null);
        return missingFamilyStatus;
    }

    private static PersonalDetails getMissingHousingType() {
        PersonalDetails missingHousingType = LoanDemandFixtures.getPersonalDetails();
        missingHousingType
                .setHousingType(null);
        return missingHousingType;
    }

    private static PersonalDetails getLastNameTooLong() {
        PersonalDetails request = LoanDemandFixtures.getPersonalDetails();

        request
                .setLastName(LONG_NAME);
        return request;
    }

    private static PersonalDetails getLastNameInvalidFormat() {
        PersonalDetails request = LoanDemandFixtures.getPersonalDetails();
        request
                .setLastName("Joonko!");
        return request;
    }

    private static PersonalDetails getFirstNameTooLong() {
        PersonalDetails request = LoanDemandFixtures.getPersonalDetails();
        request
                .setFirstName(LONG_NAME);
        return request;
    }

    private static PersonalDetails getFirstNameInvalidFormat() {
        PersonalDetails request = LoanDemandFixtures.getPersonalDetails();
        request
                .setFirstName("Joonko(");
        return request;
    }

    private static PersonalDetails getDateOfBirthLessThan18Years() {
        PersonalDetails request = LoanDemandFixtures.getPersonalDetails();
        request
                .setBirthDate(LocalDate.now()
                        .minusYears(10)
                        .minusDays(250));
        return request;
    }

    private static PersonalDetails getMissingNumberOfChildren() {
        PersonalDetails request = LoanDemandFixtures.getPersonalDetails();
        request
                .setNumberOfChildren(null);
        return request;
    }

    private static PersonalDetails getDateOfBirthMoreThan100Years() {
        PersonalDetails request = LoanDemandFixtures.getPersonalDetails();
        request
                .setBirthDate(LocalDate.now()
                        .minusYears(102)
                        .minusDays(250));
        return request;
    }

}
