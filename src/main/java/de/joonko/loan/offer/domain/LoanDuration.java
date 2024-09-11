package de.joonko.loan.offer.domain;

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
    ONE_HUNDRED_EIGHT(108),
    ONE_HUNDRED_TWENTY(120);

    public int value;

    public static LoanDuration fromNumber(Integer number) {
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
