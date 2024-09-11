package de.joonko.loan.partner.auxmoney.model;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum EmploymentStatus {
    UNLIMITED(1), TEMPORARY(2), TRIAL_PERIOD(3);

    @JsonValue
    private final int status;

    EmploymentStatus(int status) {
        this.status = status;
    }
}
