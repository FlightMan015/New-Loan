package de.joonko.loan.partner.aion.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferDetails {

    @JsonProperty("id")
    private String id;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("currency")
    @Builder.Default
    private String currency = "EUR";

    @JsonProperty("maturity")
    private Integer maturity;

    @JsonProperty("annualPercentageRate")
    private BigDecimal annualPercentageRate;

    @JsonProperty("nominalInterestRate")
    private BigDecimal nominalInterestRate;

    @JsonProperty("monthlyInstalmentAmount")
    private BigDecimal monthlyInstalmentAmount;

    @JsonProperty("totalRepaymentAmount")
    private BigDecimal totalRepaymentAmount;
}
