package de.joonko.loan.user.service;

import de.joonko.loan.offer.api.*;
import de.joonko.loan.user.service.persistence.domain.ConsentData;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Document
public class UserAdditionalInformationStore {

    @Id
    private String userUUID;

    @Valid
    private PersonalDetails personalDetails;
    @Valid
    private EmploymentDetails employmentDetails;

    @Valid
    private CreditDetails creditDetails;

    @Valid
    private Expenses expenses;
    @Valid
    private Income income;
    @Valid
    private ContactData contactData;
    @Valid
    @NotNull
    @Builder.Default
    private List<ConsentData> consentData = List.of();
}
