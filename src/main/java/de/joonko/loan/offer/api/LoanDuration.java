package de.joonko.loan.offer.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum LoanDuration {
    SIX(6),
    TWELVE(12),
    EIGHTEEN(18),
    TWENTY_FOUR(24),
    THIRTY_SIX(36),
    FORTY_EIGHT(48),
    SIXTY(60),
    SEVENTY_TWO(72),
    EIGHTY_FOUR(84),
    NINETY_SIX(96),
    ONE_HUNDRED_TWENTY(120);

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
