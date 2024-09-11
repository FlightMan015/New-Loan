package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum LoanDuration {

    TWELVE(12),
    TWENTY_FOUR(24),
    THIRTY_SIX(36),
    FORTY_EIGHT(48),
    SIXTY(60),
    SEVENTY_TWO(72),
    EIGHTY_FOUR(84);

    @JsonValue
    private int value;

    @JsonCreator
    public static LoanDuration fromNumber(int number) {
        for (LoanDuration b : LoanDuration.values()) {
            if (b.value == number) {
                return b;
            }
        }
        return null;
    }

    LoanDuration(int value) {
        this.value = value;
    }
}
