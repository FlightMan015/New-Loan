package de.joonko.loan.offer.domain;

import lombok.Getter;

import java.util.Set;

@Getter
public enum FamilyStatus {

    SINGLE,
    MARRIED,
    WIDOWED,
    DIVORCED,
    LIVING_SEPARATELY,
    LIVING_IN_LONGTERM_RELATIONSHIP;

    public static Set<FamilyStatus> getStatusesFor2AdultsInHousehold() {
        return Set.of(MARRIED, LIVING_IN_LONGTERM_RELATIONSHIP);
    }
}
