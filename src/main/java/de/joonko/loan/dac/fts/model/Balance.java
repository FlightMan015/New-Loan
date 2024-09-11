package de.joonko.loan.dac.fts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Balance {

    @JsonProperty("balance")
    private int balance;

    @JsonProperty("limit")
    private int limit;

    @JsonProperty("available")
    private int available;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("date")
    private String date;
}

