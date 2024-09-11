package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PaymentCollectionDay {

    FIRST_OF_MONTH(1), FIFTEENTH_OF_MONTH(15);

    PaymentCollectionDay(int day) {
        this.day = day;
    }

    @JsonValue
    final int day;
}
