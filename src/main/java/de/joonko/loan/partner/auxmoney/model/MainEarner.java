package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MainEarner {
    NO(0), YES(1);

    @JsonValue
    private final int earner;

    MainEarner(int earner) {
        this.earner = earner;
    }
}
