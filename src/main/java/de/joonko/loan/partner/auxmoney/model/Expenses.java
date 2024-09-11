package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expenses {

    @Min(value = 0, message = "Other should be equal or grater than 0")
    private int other;

    @JsonProperty("support_expenses")
    private int supportExpenses;
    @JsonProperty("living_expenses")
    private int livingExpenses;

    @JsonProperty("debt_expenses")
    @Min(value = 0, message = "Debt Expenses should be equal or grater than 0")
    private int debtExpenses;

    @JsonProperty("insurance_and_savings")
    @Min(value = 0, message = "Insurance and savings should be equal or grater than 0")
    private int insuranceAndSavings;
    @JsonProperty("rent_and_mortgage")
    @Min(value = 0, message = "Rent and mortgage should be equal or grater than 0")
    private int rentAndMortgage;
    @JsonProperty("total_expenses")
    @Min(value = 0, message = "Total expenese should be equal or grater than 0")
    private int totalExpenses;
    @JsonProperty("memberships")
    @Min(value = 0, message = "Memberships should be equal or grater than 0")
    private int memberships;


}
			
