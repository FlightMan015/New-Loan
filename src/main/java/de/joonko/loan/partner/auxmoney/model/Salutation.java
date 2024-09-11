package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Salutation {
    HERR(1), FRAU(2);

    @JsonValue
    private final int salutationId;

    @JsonCreator
    public static Salutation fromNumber(int number) {
        for (Salutation s : Salutation.values()) {
            if (s.salutationId == number) {
                return s;
            }
        }
        return null;
    }

    Salutation(int salutationId) {
        this.salutationId = salutationId;
    }
}
