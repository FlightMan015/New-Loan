package de.joonko.loan.partner.auxmoney.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.joonko.loan.offer.domain.ClassificationProvider;
import de.joonko.loan.offer.domain.Currency;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DigitalAccountStatements {
    private String owner;

    @JsonProperty("balance_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate balanceDate;

    @JsonProperty("balance")
    private Double balance;

    @JsonProperty("dac_source")
    private ClassificationProvider dacSource;

    private String iban;

    private String name;

    private Currency currency;

    private BankAccountCategory category;

    private String type;

    private List<Transaction> transactions;

    private String bic;
}
