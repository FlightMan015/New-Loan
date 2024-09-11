package de.joonko.loan.offer.domain;

import lombok.Getter;

@Getter
public enum EmploymentType {

    REGULAR_EMPLOYED,
    OTHER;

    public static EmploymentType fakeValue() {
        return REGULAR_EMPLOYED;
    }
}
