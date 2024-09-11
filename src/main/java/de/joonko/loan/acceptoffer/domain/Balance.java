package de.joonko.loan.acceptoffer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Balance {
    private Double limit;
    private Double available;
    private String currency;
    private LocalDate date;
    private Double balance;
}
