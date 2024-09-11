package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TransactionBooked {
    BOOKED(1), NOT_BOOKED(0);

    @JsonValue
    private final int booked;

    TransactionBooked(int booked) {
        this.booked = booked;
    }
}
