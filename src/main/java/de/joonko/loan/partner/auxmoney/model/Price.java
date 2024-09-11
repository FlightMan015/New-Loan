package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Price implements Serializable {
    private Integer duration;

    private Integer loan;

    @JsonProperty("total_credit_amount")
    private Double totalCreditAmount;

    private Double rate;

    @JsonProperty("insurance_fee")
    private Double insuranceFee;

    private Double interest;

    @JsonProperty("installment_amount")
    private Double installmentAmount;

    @JsonProperty("commission_amount")
    private Double commissionAmount;

    @JsonProperty("price_id")
    private Integer priceId;

    @JsonProperty("loan_asked")
    private Integer loanAsked;

    @JsonProperty("eff_rate")
    private Double effRate;

    @JsonProperty("interest_amount")
    private Double interestAmount;


}
