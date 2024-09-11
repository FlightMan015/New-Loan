package de.joonko.loan.offer.api;


import com.fasterxml.jackson.annotation.JsonFormat;
import de.joonko.loan.acceptoffer.api.Account;
import de.joonko.loan.acceptoffer.api.Balance;
import de.joonko.loan.acceptoffer.api.Turnovers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSnapshot {

    private Account account;
    private Balance balance;
    private List<Turnovers> turnovers;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    private Integer days;
}
