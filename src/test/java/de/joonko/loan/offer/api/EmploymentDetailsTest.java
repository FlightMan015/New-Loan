package de.joonko.loan.offer.api;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class EmploymentDetailsTest extends BaseValidatorTest {


    @ParameterizedTest
    @MethodSource("creatRequestWithInvalidEmploymentDetails")
    void validateEmploymentDetails(EmploymentDetails request, String errorMessage) {
        validateErrorMessage(request, errorMessage);
    }

    static Stream<Arguments> creatRequestWithInvalidEmploymentDetails() {
        return Stream.of(
                Arguments.of(getMissingEmploymentType(), "Employment type must not be null"),
                Arguments.of(getMissingEmployerName(), "Employer name must not be null"),
                Arguments.of(getEmployerNameTooLong(), "Employer name is too long"),
                Arguments.of(getMissingEmployerStreetName(), "Employer Street name must not be null"),
                Arguments.of(getEmployerStreetNameTooLong(), "Employer Street name is too long"),
                Arguments.of(getEmployerPostcode(), "Employer postal code must not be null"),
                Arguments.of(getEmployerInvalidPostcode(), "Employer Postal code should be 5 digits"),
                Arguments.of(getMissingEmployerCity(), "Employer city must not be null"),
                Arguments.of(getInvalidCity(), "Employer City is not valid"),
                Arguments.of(getLongCity(), "Employer City is too long"),
                Arguments.of(getMissingEmploymentSince(), "Employment since must not be null"),
                Arguments.of(getEmploymentSinceNotValid(), "Employment since must be older than today")
        );
    }

    private static EmploymentDetails getEmploymentSinceNotValid() {
        EmploymentDetails request = LoanDemandFixtures.getEmploymentDetails();
        request.setEmploymentSince(new ShortDate(5, 2050));
        return request;
    }

    private static EmploymentDetails getMissingEmploymentSince() {
        EmploymentDetails request = LoanDemandFixtures.getEmploymentDetails();
        request.setEmploymentSince(null);
        return request;
    }

    private static EmploymentDetails getInvalidCity() {
        EmploymentDetails request = LoanDemandFixtures.getEmploymentDetails();
        request.setCity("Berlin22");
        return request;
    }

    private static EmploymentDetails getLongCity() {
        EmploymentDetails request = LoanDemandFixtures.getEmploymentDetails();
        request.setCity("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return request;
    }

    private static EmploymentDetails getMissingEmployerCity() {
        EmploymentDetails request = LoanDemandFixtures.getEmploymentDetails();
        request.setCity(null);
        return request;
    }

    private static EmploymentDetails getEmployerPostcode() {
        EmploymentDetails request = LoanDemandFixtures.getEmploymentDetails();
        request.setPostCode(null);
        return request;
    }

    private static EmploymentDetails getEmployerInvalidPostcode() {
        EmploymentDetails request = LoanDemandFixtures.getEmploymentDetails();
        request.setPostCode("1234");
        return request;
    }

    private static EmploymentDetails getEmployerStreetNameTooLong() {
        EmploymentDetails request = LoanDemandFixtures.getEmploymentDetails();
        request.setStreetName(LONG_NAME);
        return request;
    }

    private static EmploymentDetails getMissingEmployerStreetName() {
        EmploymentDetails request = LoanDemandFixtures.getEmploymentDetails();
        request.setStreetName(null);
        return request;
    }

    private static EmploymentDetails getEmployerNameTooLong() {
        EmploymentDetails request = LoanDemandFixtures.getEmploymentDetails();
        request.setEmployerName(LONG_NAME);
        return request;
    }

    private static EmploymentDetails getMissingEmployerName() {
        EmploymentDetails request = LoanDemandFixtures.getEmploymentDetails();
        request.setEmployerName(null);
        return request;
    }

    private static EmploymentDetails getMissingEmploymentType() {
        EmploymentDetails request = LoanDemandFixtures.getEmploymentDetails();
        request.setEmploymentType(null);
        return request;
    }

}
