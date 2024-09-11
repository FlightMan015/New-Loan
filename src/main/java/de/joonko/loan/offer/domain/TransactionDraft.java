package de.joonko.loan.offer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDraft {

    private Double amount;
    private String iban;
    private String bic;
    private LocalDate bookingDate;
    private String bookingText;
    private String purpose;
    private String currency;
    private String partnerName;
}
