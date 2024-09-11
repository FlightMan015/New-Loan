package de.joonko.loan.integrations.domain.integrationhandler.userpersonal;

import de.joonko.loan.integrations.model.DistributionChannel;
import de.joonko.loan.offer.api.ContactData;
import de.joonko.loan.offer.api.CreditDetails;
import de.joonko.loan.offer.api.EmploymentDetails;
import de.joonko.loan.offer.api.Expenses;
import de.joonko.loan.offer.api.Income;
import de.joonko.loan.offer.api.PersonalDetails;
import de.joonko.loan.offer.api.validator.ValidContactData;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserPersonalData {

    @NotNull
    private String userUuid;
    private Long bonifyUserId;
    private Boolean verifiedViaBankAccount;
    private DistributionChannel distributionChannel;
    private String tenantId;

    private CreditDetails creditDetails;

    @Valid
    @NotNull(message = "Personal details must not be null")
    private PersonalDetails personalDetails;

    @NotNull(message = "Contact data must not be null")
    @ValidContactData
    private ContactData contactData;

    @Valid
    private EmploymentDetails employmentDetails;

    private Income income;

    private Expenses expenses;
}
