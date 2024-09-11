package de.joonko.loan.offer.domain;

import lombok.Getter;

@Getter
public enum TransactionMarkedForPosting {
    ZERO(0), ONE(1);

    private final int value;

    TransactionMarkedForPosting(int i) {
        this.value = i;
    }

    public static TransactionMarkedForPosting fromNumber(int number) {
        for (TransactionMarkedForPosting b : TransactionMarkedForPosting.values()) {
            if (b.value == number) {
                return b;
            }
        }
        return null;
    }
}
