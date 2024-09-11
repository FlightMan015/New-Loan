package de.joonko.loan.partner.postbank.model.request;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountType {
    TRANSFER_ACCOUNT("auszahlung"),
    LOAN_PAY_BACK_ACCOUNT("lastschrift"),
    OTHER_LOAN_ACCOUNT("abloesung");

    private String label;

    @JsonValue
    public String getLabel() {
        return label;
    }

    AccountType(String label) {
        this.label = label;
    }
}
