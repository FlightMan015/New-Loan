package de.joonko.loan.dac.fts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FtsRawData {
    @JsonProperty("account")
    private Account account;

    @JsonProperty("balance")
    private Balance balance;

    @JsonProperty("turnovers")
    List<Turnover> turnovers;

    @JsonProperty("date")
    public String date;

    @JsonProperty("days")
    public int days;

    @JsonProperty("filters")
    public List<Object> filters;
}



