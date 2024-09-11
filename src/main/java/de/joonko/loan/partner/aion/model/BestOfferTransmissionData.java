package de.joonko.loan.partner.aion.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class BestOfferTransmissionData implements TransmissionData {

    @JsonProperty("requestedLoanAmount")
    private BigDecimal requestedLoanAmount;

    @JsonProperty("requestedLoanCurrency")
    @Builder.Default
    private String requestedLoanCurrency = "EUR";

    @JsonProperty("offers")
    private List<BestOfferValue> offers;
}
