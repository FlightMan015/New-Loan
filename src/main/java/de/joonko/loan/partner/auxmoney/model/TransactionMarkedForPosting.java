package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TransactionMarkedForPosting {
    ZERO(0), ONE(1);

    @JsonValue
    private final int value;

    TransactionMarkedForPosting(int i) {
        this.value = i;
    }

    @JsonCreator
    public static TransactionMarkedForPosting fromNumber(int number) {
        for (TransactionMarkedForPosting b : TransactionMarkedForPosting.values()) {
            if (b.value == number) {
                return b;
            }
        }
        return null;
    }
}
