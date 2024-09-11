package de.joonko.loan.offer.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.joonko.loan.offer.api.validator.ValidContactData;
import de.joonko.loan.offer.api.validator.ValidEmploymentDetails;
import de.joonko.loan.offer.domain.Precheck;
import de.joonko.loan.user.service.persistence.domain.ConsentData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Document
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class LoanDemandRequest implements Serializable {

    @Id
    private String _id;

    @Indexed
    private String userUUID;

    @Indexed
    private String applicationId;

    private String parentApplicationId;

    private Integer loanAsked;

    private String fundingPurpose;

    private Boolean isRequestedBonifyLoans;

    private String requestIp;

    private String requestCountryCode;

    @Valid
    @NotNull(message = "Personal details must not be null")
    private PersonalDetails personalDetails;


    @NotNull(message = "Employment details must not be null")
    @ValidEmploymentDetails
    private EmploymentDetails employmentDetails;

    @Valid
    private CreditDetails creditDetails;

    @Valid
    @NotNull(message = "Income details must not be null")
    private Income income;

    @Valid
    @NotNull(message = "Expense details must not be null")
    private Expenses expenses;

    private BigDecimal disposableIncome;

    @Valid
    @NotNull(message = "Account details must not be null")
    private AccountDetails accountDetails;


    @NotNull(message = "Contact data must not be null")
    @ValidContactData
    private ContactData contactData;

    @NotNull(message = "FTS transaction id must not be null")
    private String ftsTransactionId;

    @NotNull(message = "DAC id must not be null")
    private String dacId;

    @Valid
    @NotNull(message = "CustomDACData details must not be null")
    private CustomDACData customDACData;

    @Valid
    @NotNull(message = "List of Consents must not be null")
    @Builder.Default
    private List<ConsentData> consents = List.of();

    @Valid
    @Builder.Default
    private Set<Precheck> preChecks = Set.of();

    @CreatedDate
    private LocalDateTime insertTS;

    @JsonIgnore
    public LoanDemandRequest acknowledgeRentAndMortgages() {
        if (this.expenses != null) this.expenses.acknowledgeRentAndMortgages();
        return this;
    }

    @JsonIgnore
    public LoanDemandRequest acknowledgeNetIncome() {
        if (this.income != null) this.income.acknowledgeNetIncome();
        return this;
    }
}
