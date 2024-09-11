package de.joonko.loan.offer.api.validator;

import de.joonko.loan.offer.api.BaseValidatorTest;
import de.joonko.loan.offer.api.LoanDemandFixtures;
import de.joonko.loan.offer.api.LoanDemandRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EmploymentDetailsValidatorTest extends BaseValidatorTest {
    @Nested
    class ConditionalEmploymentDetailsTest {
        @Test
        @DisplayName("Should ignore employerName ,address and since if category is other ")
        void validateConditionalEmploymentDetailsOtherCategory() {
            LoanDemandRequest otherAsEmploymentDetails = getOtherAsEmploymentDetails();
            validateNoError(otherAsEmploymentDetails);
        }

        @Test
        @DisplayName("Should validate employerName ,address and since if category is  ")
        void validateConditionalEmploymentDetailsRegularEmployed() {
            LoanDemandRequest otherAsEmploymentDetails = getRegularEmployedAsEmploymentDetails();
            validateNumberOfErrors(otherAsEmploymentDetails, 6);
        }
    }

    private static LoanDemandRequest getOtherAsEmploymentDetails() {
        LoanDemandRequest missingContactData = LoanDemandFixtures.getLoanDemandRequest();
        missingContactData.getEmploymentDetails()
                .setEmploymentType(de.joonko.loan.offer.api.EmploymentType.OTHER);
        return missingContactData;
    }

    private static LoanDemandRequest getRegularEmployedAsEmploymentDetails() {
        LoanDemandRequest missingCity = LoanDemandFixtures.getLoanDemandRequest();
        missingCity.getEmploymentDetails()
                .setEmploymentType(de.joonko.loan.offer.api.EmploymentType.REGULAR_EMPLOYED);
        missingCity.getEmploymentDetails()
                .setCity(null);
        missingCity.getEmploymentDetails()
                .setEmployerName(null);
        missingCity.getEmploymentDetails()
                .setPostCode(null);
        missingCity.getEmploymentDetails()
                .setEmploymentSince(null);
        missingCity.getEmploymentDetails()
                .setStreetName(null);
        return missingCity;
    }
}
