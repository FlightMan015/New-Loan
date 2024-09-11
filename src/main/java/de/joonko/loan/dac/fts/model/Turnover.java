package de.joonko.loan.dac.fts.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Turnover {

    @JsonProperty("booking_date")
    private String bookingDate;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("purpose")
    private List<String> purpose;

    @JsonProperty("counter_iban")
    private String counterIban;

    @JsonProperty("counter_bic")
    private String counterBic;

    @JsonProperty("counter_holder")
    private String counterHolder;

    @JsonProperty("prebooked")
    private boolean prebooked;

    @JsonProperty("creditor_id")
    private String creditorId;

    @JsonProperty("tags")
    private List<String> tags;
}
