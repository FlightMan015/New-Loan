package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.joonko.loan.partner.solaris.validator.ValidLoanAmount;
import de.joonko.loan.validator.ValidIBAN;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
public class SolarisGetOffersRequest {

    @JsonProperty("partner_reference_number")
    @NotNull(message = "Partner reference Number cannot be null")
    private String partnerReferenceNumber;

    @JsonProperty("requested_loan_amount")
    @NotNull(message = "Loan asked cannot be null or empty")
    @ValidLoanAmount
    @Valid
    private AmountValue requestedLoanAmount;

    @JsonProperty("loan_purpose")
    @NotNull(message = "Loan purpose cannot be null")
    private String loanPurpose;

    @JsonProperty("requested_loan_term")
    @NotNull(message = "Loan duration cannot be null")
    private Integer duration;

    @JsonProperty("credit_record_id")
    private String creditRecordId;

    @JsonProperty("employment_since")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate employmentSince;

    @JsonProperty("employment_status")
    private EmploymentStatus employmentStatus;

    @JsonProperty("employment_until")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate employmentUntil;

    @JsonProperty("is_joint_application")
    private Boolean isJointApplication;

    @JsonProperty("living_situation")
    @NotNull(message = "Living Situation cannot be null")
    private LivingSituation livingSituation;

    @JsonProperty("moved_in_last_two_years")
    @NotNull(message = "Moved in last two years cannot be null")
    private Boolean hasMovedInLastTwoYears;

    @JsonProperty("existing_credit_repayment_excluding_mortgage")
    @Valid
    private AmountValue existingCreditRepaymentExcludingMortgage;

    @JsonProperty("rent")
    @NotNull(message = "Rent paid cannot be null")
    @Valid
    private AmountValue rent;

    @JsonProperty("additional_costs")
    @NotNull(message = "Additional cost cannot be null")
    @Valid
    private AmountValue additionalCosts;

    @JsonProperty("living_situation_amount")
    @NotNull(message = "Living Situation Amount cannot be null")
    @Valid
    private AmountValue livingSituationAmount;

    @JsonProperty("maintenance_obligations_amount")
    @Valid
    private AmountValue maintenanceObligationsAmount;

    @JsonProperty("mortgage")
    @Valid
    private AmountValue mortgage;

    @JsonProperty("net_income_amount")
    @NotNull(message = "Net income amount cannot be null")
    @Valid
    private AmountValue netIncomeAmount;

    @JsonProperty("private_insurance_amount")
    @Valid
    private AmountValue privateInsuranceAmount;

    @JsonProperty("number_of_dependents")
    @NotNull(message = "Number of dependents cannot be null")
    private Integer numberOfDependents;

    @JsonProperty("number_of_kids")
    private Integer numberOfkids;

    @JsonProperty("has_private_insurance")
    private Boolean hasPrivateInsurance;

    @JsonProperty("recipient_iban")
    @ValidIBAN
    @NotNull(message = "Recipient IBAN cannot be null")
    private String recipientIban;

    @JsonProperty("repayment_day_of_month")
    @NotNull(message = "Repayment day of month cannot be null")
    private Integer repaymentDayOfMonth;

    @JsonProperty("solarisbank_generate_contract")
    @NotNull(message = "Solaris generate contract cannot be null")
    private Boolean shouldSolarisBankGenerateContract;

    private String ftsTransactionId;
}
