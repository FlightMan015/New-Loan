package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum LoanCategory {

    OTHER(0),
    CAR_LOAN(1),
    FURNITURE_RENOVATION_MOVE(2),
    VACATION(3),
    PC_HIFI_TV_VIDEO(4),
    BUSINESS_STARTUP(5),
    BALANCING_CURRENT_ACCOUNT_DISPO(6);

    @JsonValue
    private int value;

    LoanCategory(int value) {
        this.value = value;
    }
}
