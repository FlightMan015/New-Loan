package de.joonko.loan.partner.swk.model;

public enum LoanDuration {
    TWELVE(12),
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

    LoanDuration(int value) {
        this.value = value;
    }
}
