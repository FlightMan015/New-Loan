package de.joonko.loan.offer.api;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class PreviousAddressTest extends BaseValidatorTest {

    @ParameterizedTest
    @MethodSource("creatRequestWithInvalidPreviousAddress")
    void validateContactDetails(PreviousAddress request, String errorMessage) {
        validateErrorMessage(request, errorMessage);
    }

    static Stream<Arguments> creatRequestWithInvalidPreviousAddress() {
        return Stream.of(
                Arguments.of(getMissingStreetName(), "PreviousAddress Street name must not be null"),
                Arguments.of(geStreetNameTooLong(), "PreviousAddress Street name is too long"),
                Arguments.of(getMissingPostCode(), "PreviousAddress Post code must not be null"),
                Arguments.of(getInvalidPostCode(), "PreviousAddress Post code should be 5 digits"),
                Arguments.of(getMissingCity(), "PreviousAddress City must not be null"),
                Arguments.of(getLongCity(), "PreviousAddress City is too long"),
                Arguments.of(getCityNotValid(), "PreviousAddress City is not valid"),
                Arguments.of(getCountryNotValid(), "PreviousAddress Country must not be null")
        );
    }

    private static PreviousAddress getCityNotValid() {
        PreviousAddress previousAddress = LoanDemandFixtures.getPreviousAddress();
        previousAddress.setCity("DSFDSF3");
        return previousAddress;
    }

    private static PreviousAddress getLongCity() {
        PreviousAddress request = LoanDemandFixtures.getPreviousAddress();
        request.setCity("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return request;
    }

    private static PreviousAddress getMissingCity() {
        PreviousAddress previousAddress = LoanDemandFixtures.getPreviousAddress();
        previousAddress.setCity(null);
        return previousAddress;
    }

    private static PreviousAddress getMissingPostCode() {
        PreviousAddress previousAddress = LoanDemandFixtures.getPreviousAddress();
        previousAddress.setPostCode(null);
        return previousAddress;
    }
    private static PreviousAddress getInvalidPostCode() {
        PreviousAddress previousAddress = LoanDemandFixtures.getPreviousAddress();
        previousAddress.setPostCode("123");
        return previousAddress;
    }

    private static PreviousAddress geStreetNameTooLong() {
        PreviousAddress previousAddress = LoanDemandFixtures.getPreviousAddress();
        previousAddress.setStreetName(LONG_NAME);
        return previousAddress;
    }

    private static PreviousAddress getMissingStreetName() {
        PreviousAddress previousAddress = LoanDemandFixtures.getPreviousAddress();
        previousAddress.setStreetName(null);
        return previousAddress;
    }

    private static PreviousAddress getCountryNotValid() {
        PreviousAddress previousAddress = LoanDemandFixtures.getPreviousAddress();
        previousAddress.setCountry(null);
        return previousAddress;
    }


}
