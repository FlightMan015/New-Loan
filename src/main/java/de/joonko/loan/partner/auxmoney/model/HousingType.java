package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum HousingType {
    OWNERSHIP(1),
    RENT(2),
    WITH_THE_PARENTS(3);


    @JsonValue
    private final int type;

    @JsonCreator
    public static HousingType fromNumber(int number) {
        for (HousingType b : HousingType.values()) {
            if (b.type == number) {
                return b;
            }
        }
        return null;
    }

    HousingType(int type) {
        this.type = type;
    }
}
