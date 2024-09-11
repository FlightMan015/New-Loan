package de.joonko.loan.partner.aion.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.joonko.loan.dac.fts.model.Account;
import de.joonko.loan.dac.fts.model.Balance;
import de.joonko.loan.dac.fts.model.Turnover;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PSD2RawDataTransmissionData implements TransmissionData {

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
