package de.joonko.loan.integrations.domain.integrationhandler.fts.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Balance {

    private Double limit;
    private Double available;
    private String currency;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    private Double balance;
}
