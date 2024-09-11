package de.joonko.loan.offer.domain;

import de.joonko.loan.acceptoffer.domain.Account;
import de.joonko.loan.acceptoffer.domain.Balance;
import de.joonko.loan.acceptoffer.domain.Turnovers;
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
    private LocalDate date;
    private Integer days;
}
