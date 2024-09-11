package de.joonko.loan.acceptoffer.domain;

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
public class Turnovers {
    private LocalDate bookingDate;
    private Double amount;
    private String currency;
    private List<String> purpose;
    private String counterIban;
    private String counterBic;
    private String counterHolder;
    private Boolean preBooked;
    private List<String> tags;

}
