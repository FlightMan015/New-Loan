package de.joonko.loan.partner.solaris.model;

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

    public int value;

    LoanDuration(int value) {
        this.value = value;
    }
}
