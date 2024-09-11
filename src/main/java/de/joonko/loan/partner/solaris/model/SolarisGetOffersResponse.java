package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolarisGetOffersResponse {


    private String id;
    private String personId;
    private Offer offer;

    @JsonProperty("loan_decision")
    private String loanDecision;

    @JsonProperty("disposable_income")
    private AmountValue disposableIncome;

    @JsonProperty("customer_category")
    private String customerCategory;

}
