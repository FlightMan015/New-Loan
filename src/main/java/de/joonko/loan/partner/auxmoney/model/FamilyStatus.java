package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum FamilyStatus {

    LEDIG(1), VERHEIRATET(2), VERWITWET(3), GESCHIEDEN(4), GETRENNT_LEBEND(5);

    @JsonValue
    private final int status;

    @JsonCreator
    public static FamilyStatus fromNumber(int number) {
        for (FamilyStatus fs : FamilyStatus.values()) {
            if (fs.status == number) {
                return fs;
            }
        }
        return null;
    }

    FamilyStatus(int status) {
        this.status = status;
    }
}
