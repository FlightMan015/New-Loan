package de.joonko.loan.offer.domain;

import lombok.Getter;

@Getter
public enum BankAccountCategory {
    PRIVATE("private"), BUSINESS("business");

    private final String value;

    BankAccountCategory(String value) {
        this.value = value;
    }
}
