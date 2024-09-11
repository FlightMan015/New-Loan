package de.joonko.loan.offer.api.validator;

import de.joonko.loan.offer.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class ContactDetailsValidatorTest extends BaseValidatorTest {
    @Nested
    class ConditionalValidationContactDataTest {
        @Test
        @DisplayName("Should ignore previous address if current address is older then older than 24 months")
        void shouldIgnorePreviousAddress() {
            LoanDemandRequest loanDemandRequest = LoanDemandFixtures.getLoanDemandRequest();
            LocalDate twoYearOldDate = LocalDate.now()
                    .minusMonths(24);

            loanDemandRequest.getContactData()
                    .setLivingSince(ShortDate.builder()
                            .month(twoYearOldDate.getMonth()
                                    .getValue())
                            .year(twoYearOldDate.getYear())
                            .build());
            loanDemandRequest.getContactData()
                    .setPreviousAddress(null);
            loanDemandRequest.getPersonalDetails().setPlaceOfBirth("SOMEWHERE");
            validateNoError(loanDemandRequest);

        }

        @Test
        @DisplayName("Should validate previous address if current address is Not older then older than 24 months")
        void shouldValidatePreviousAddress() {
            LoanDemandRequest loanDemandRequest = LoanDemandFixtures.getLoanDemandRequest();
            LocalDate twoYearOldDate = LocalDate.now()
                    .minusMonths(23);

            loanDemandRequest.getContactData()
                    .setLivingSince(ShortDate.builder()
                            .month(twoYearOldDate.getMonth()
                                    .getValue())
                            .year(twoYearOldDate.getYear())
                            .build());
            loanDemandRequest.getContactData()
                    .setPreviousAddress(null);
            validateErrorMessage(loanDemandRequest, "PreviousAddress must not be null if living since is not older than 24 months");
        }

        @Test
        @DisplayName("Should validate previous address all fields if current address is Not older then older than 24 months")
        void shouldValidatePreviousAddressFields() {
            LoanDemandRequest loanDemandRequest = LoanDemandFixtures.getLoanDemandRequest();
            LocalDate twoYearOldDate = LocalDate.now()
                    .minusMonths(23);

            loanDemandRequest.getContactData()
                    .setLivingSince(ShortDate.builder()
                            .month(twoYearOldDate.getMonth()
                                    .getValue())
                            .year(twoYearOldDate.getYear())
                            .build());
            PreviousAddress previousAddress = LoanDemandFixtures.getPreviousAddress();
            previousAddress.setCity(null);
            loanDemandRequest.getContactData()
                    .setPreviousAddress(previousAddress);
            validateErrorMessage(loanDemandRequest, "PreviousAddress City must not be null");
        }


    }
}
