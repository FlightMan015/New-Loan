package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum BankAccountCategory {
    PRIVATE("private"), BUSINESS("business");


    @JsonValue
    private final String value;

    BankAccountCategory(String value) {
        this.value = value;
    }
}
