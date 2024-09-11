package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.joonko.loan.offer.domain.ClassificationProvider;
import de.joonko.loan.offer.domain.Currency;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {
    private String name;
    private Double amount;
    private String iban;
    private String bic;
    @JsonProperty("bank_name")
    private String bankName;

    private Currency currency;

    @JsonProperty("booking_date")
    private LocalDate bookingDate;

    private String purpose;
    private String type;
    private TransactionBooked booked;
    private TransactionMarkedForPosting visited;

    @JsonProperty("classification_bank")
    private String classificationBank;

    @JsonProperty("classification_own")
    private String classificationOwn;

    @JsonProperty("booking_text")
    private String bookingText;

    private Integer saldo;

    @JsonProperty("classification_provider")
    private ClassificationProvider classificationProvider;


}
