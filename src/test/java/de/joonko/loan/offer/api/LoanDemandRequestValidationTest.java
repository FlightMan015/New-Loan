package de.joonko.loan.offer.api;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

class LoanDemandRequestValidationTest extends BaseValidatorTest {

    @ParameterizedTest
    @MethodSource("creatRequestWithMissingDetails")
    void validateNotNull(LoanDemandRequest request, String errorMessage) {
        validateErrorMessage(request, errorMessage);
    }

    private static LoanDemandRequest getLoanDemandRequestMissingPersonalDetails() {
        LoanDemandRequest missingPersonalDetails = LoanDemandFixtures.getLoanDemandRequest();
        missingPersonalDetails.setPersonalDetails(null);
        return missingPersonalDetails;
    }

    private static LoanDemandRequest getMissingContactData() {
        LoanDemandRequest missingContactData = LoanDemandFixtures.getLoanDemandRequest();
        missingContactData.setContactData(null);
        return missingContactData;
    }

    private static LoanDemandRequest getMissingEmploymentDetails() {
        LoanDemandRequest missingContactData = LoanDemandFixtures.getLoanDemandRequest();
        missingContactData.setEmploymentDetails(null);
        return missingContactData;
    }


    static Stream<Arguments> creatRequestWithMissingDetails() {
        return Stream.of(
                Arguments.of(getLoanDemandRequestMissingPersonalDetails(), "Personal details must not be null"),
                Arguments.of(getMissingEmploymentDetails(), "Employment details must not be null"),
                Arguments.of(getMissingContactData(), "Contact data must not be null")
        );
    }


    static Stream<Arguments> creatRequestWithInvalidLoanAmountData() {

        return Stream.of(
                Arguments.of(getLoanDemandRequestMissingLoanAsked(), "LoanAsked must not be null"),
                Arguments.of(getLoanDemandRequestLoanAmountGreaterThan100000(), "LoanAsked must be <= 100000"),
                Arguments.of(getLoanDemandRequestLoanAmountLessThan1000(), "LoanAsked must be >= 1000"),
                Arguments.of(getLoanDemandRequestLoanAmountNotMultipleOf500(), "LoanAsked must be multiples of 500")
        );
    }


    @NotNull
    private static LoanDemandRequest getLoanDemandRequestMissingLoanAsked() {
        LoanDemandRequest request = LoanDemandFixtures.getLoanDemandRequest();
        request.setLoanAsked(null);
        return request;
    }

    @NotNull
    private static LoanDemandRequest getLoanDemandRequestLoanAmountNotMultipleOf500() {
        LoanDemandRequest request = LoanDemandFixtures.getLoanDemandRequest();
        request.setLoanAsked(2300);
        return request;
    }

    @NotNull
    private static LoanDemandRequest getLoanDemandRequestLoanAmountLessThan1000() {
        LoanDemandRequest request = LoanDemandFixtures.getLoanDemandRequest();
        request.setLoanAsked(500);
        return request;
    }

    @NotNull
    private static LoanDemandRequest getLoanDemandRequestLoanAmountGreaterThan100000() {
        LoanDemandRequest request = LoanDemandFixtures.getLoanDemandRequest();
        request.setLoanAsked(100500);
        return request;
    }
}
