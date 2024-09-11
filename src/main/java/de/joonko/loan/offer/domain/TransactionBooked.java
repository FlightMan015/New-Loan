package de.joonko.loan.offer.domain;

import lombok.Getter;

@Getter
public enum TransactionBooked {
    BOOKED(1), NOT_BOOKED(0);

    private final int booked;

    TransactionBooked(int booked) {
        this.booked = booked;
    }
}
