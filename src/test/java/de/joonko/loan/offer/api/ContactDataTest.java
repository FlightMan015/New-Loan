package de.joonko.loan.offer.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class ContactDataTest extends BaseValidatorTest {

    @ParameterizedTest
    @MethodSource("creatRequestWithInvalidContactDetails")
    void validateContactDetails(ContactData request, String errorMessage) {
        validateErrorMessage(request, errorMessage);
    }

    @Nested
    class City{
        @Test
        void shouldAcceptCityWithSpace(){
            ContactData contactData = LoanDemandFixtures.getContactData();
            contactData.setCity(" Some City ");
            validateNoError(contactData);
        }
        @Test
        void shouldAcceptCityWithSpecialCharacter(){
            ContactData contactData = LoanDemandFixtures.getContactData();
            contactData.setCity(" Düsseldorf");
            validateNoError(contactData);
        }
        @Test
        void shouldAcceptCityWithSpaceAtEnd(){
            ContactData contactData = LoanDemandFixtures.getContactData();
            contactData.setCity("SomeCity    ");
            validateNoError(contactData);
        }
    }

    @Nested
    class PhoneNumber {
        @Test
        @DisplayName("Should accept valid phone number")
        void validateValidMobileNumber() {
            ContactData contactData = LoanDemandFixtures.getContactData();
            contactData.setMobile("491625430546");
            validateNoError(contactData);
        }

        @Test
        @DisplayName("Should Not accept phone number with length more than 15")
        void validateMobileNumberWithMoreThan15() {
            ContactData contactData = LoanDemandFixtures.getContactData();
            contactData.setMobile("4917482734210009");
            validateErrorMessage(contactData, "Mobile number should be of length between 12 to 15");
        }

        @Test
        @DisplayName("Should Not accept phone number with length less than 12")
        void validateMobileNumberWithLessThan12() {
            ContactData contactData = LoanDemandFixtures.getContactData();
            contactData.setMobile("49162543054");
            validateErrorMessage(contactData, "Mobile number should be of length between 12 to 15");
        }


    }


    @Nested
    class PostalCode{
        @Test
        void shouldAcceptPostalCodeWithFiveNumbers(){
            ContactData contactData = LoanDemandFixtures.getContactData();
            contactData.setPostCode("01439");
            validateNoError(contactData);
        }
    }

    static Stream<Arguments> creatRequestWithInvalidContactDetails() {
        return Stream.of(
                Arguments.of(getMissingStreetName(), "Street name must not be null"),
                Arguments.of(getLoanDemandRequestStreetNameTooLong(), "Street name is too long"),
                Arguments.of(getMissingHouseNumber(), "House number must not be null"),
                Arguments.of(getHouseNumberTooLong(), "House number is too long"),
                Arguments.of(getMissingPostCode(), "Post code must not be null"),
                Arguments.of(getInvalidPostCode(), "Postal code should be 5 digits"),
                Arguments.of(getMissingCity(), "City must not be null"),
                Arguments.of(getMissingLivingSince(), "LivingSince must not be null"),
                Arguments.of(getMissingEmail(), "Email must not be null"),
                Arguments.of(getInvalidEmail(), "Email is invalid"),
                Arguments.of(getInvalidCity(), "City is not valid"),
                Arguments.of(getLongCity(), "City is too long"),
                Arguments.of(getMissingMobileNumber(), "Mobile number must not be null")

        );
    }


    private static ContactData getMissingLivingSince() {
        ContactData request = LoanDemandFixtures.getContactData();
        request.setLivingSince(null);
        return request;
    }

    private static ContactData getPostCodeNotValid() {
        ContactData request = LoanDemandFixtures.getContactData();
        request.setPostCode("1058A");
        return request;
    }

    private static ContactData getHouseNumberTooLong() {
        ContactData request = LoanDemandFixtures.getContactData();
        request.setHouseNumber(LONG_NAME);
        return request;
    }

    private static ContactData getMissingHouseNumber() {
        ContactData request = LoanDemandFixtures.getContactData();
        request.setHouseNumber(null);
        return request;
    }

    private static ContactData getLoanDemandRequestStreetNameTooLong() {
        ContactData request = LoanDemandFixtures.getContactData();
        request.setStreetName(LONG_NAME);
        return request;
    }

    private static ContactData getMissingStreetName() {
        ContactData request = LoanDemandFixtures.getContactData();
        request.setStreetName(null);
        return request;
    }

    private static ContactData getMissingCity() {
        ContactData missingCity = LoanDemandFixtures.getContactData();
        missingCity.setCity(null);
        return missingCity;
    }

    private static ContactData getMissingPostCode() {
        ContactData missingPostCode = LoanDemandFixtures.getContactData();
        missingPostCode.setPostCode(null);
        return missingPostCode;
    }

    private static ContactData getInvalidPostCode() {
        ContactData missingPostCode = LoanDemandFixtures.getContactData();
        missingPostCode.setPostCode("123");
        return missingPostCode;
    }

    private static ContactData getMissingEmail() {
        ContactData request = LoanDemandFixtures.getContactData();
        request.setEmail(null);
        return request;
    }

    private static ContactData getInvalidEmail() {
        ContactData request = LoanDemandFixtures.getContactData();
        request.setEmail("someWrongEmail");
        return request;
    }

    private static ContactData getInvalidCity() {
        ContactData request = LoanDemandFixtures.getContactData();
        request.setCity("Berlin22");
        return request;
    }

    private static ContactData getLongCity() {
        ContactData request = LoanDemandFixtures.getContactData();
        request.setCity("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return request;
    }

    private static ContactData getMissingMobileNumber() {
        ContactData request = LoanDemandFixtures.getContactData();
        request.setMobile(null);
        return request;
    }
}
