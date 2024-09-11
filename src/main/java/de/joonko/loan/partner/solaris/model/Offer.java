package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Offer {

    private String id;

    @JsonProperty("monthly_installment")
    private AmountValue monthlyInstallment;

    @JsonProperty("loan_term")
    private Integer loanTerm;

    @JsonProperty("loan_amount")
    private AmountValue loanAmount;

    @JsonProperty("interest_rate")
    private Double intertestRate;

    @JsonProperty("effective_interest_rate")
    private Double effectiveInterestRate;

    @JsonProperty("approximate_total_loan_expenses")
    private AmountValue approximateTotalLoanExpenses;
}
