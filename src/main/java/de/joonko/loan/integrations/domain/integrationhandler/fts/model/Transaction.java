package de.joonko.loan.integrations.domain.integrationhandler.fts.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Transaction {

    private Double amount;
    private String iban;
    private String bic;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate bookingDate;
    private String bookingText;
    private String purpose;
    private String currency;
    private String partnerName;

}
