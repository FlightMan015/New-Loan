package de.joonko.loan.offer.api.model;

import de.joonko.loan.offer.api.*;
import de.joonko.loan.offer.api.validator.ValidContactData;
import de.joonko.loan.offer.api.validator.ValidEmploymentDetails;
import de.joonko.loan.offer.api.validator.ValidTaxId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPersonalDetails {

    @Valid
    private PersonalDetails personalDetails;

    @ValidEmploymentDetails
    private EmploymentDetails employmentDetails;

    @Valid
    private CreditDetails creditDetails;

    @ValidTaxId
    private String taxId;

    @Valid
    private Expenses expenses;

    @Valid
    private Income income;

    @ValidContactData
    private ContactData contactData;

    public boolean additionalFieldsForHighAmountArePresent() {
        return creditDetails != null &&
                creditDetails.getIsCurrentDelayInInstallmentsDeclared() != null &&
                creditDetails.getCreditCardLimitDeclared() != null &&
                expenses != null &&
                expenses.getMonthlyLoanInstallmentsDeclared() != null &&
                expenses.getMonthlyLifeCost() != null &&
                income != null &&
                income.getIncomeDeclared() != null;
    }
}
