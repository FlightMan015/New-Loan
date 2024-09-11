package de.joonko.loan.offer.api;

import static java.util.Optional.ofNullable;

public enum FamilyStatus {
    SINGLE,
    MARRIED,
    WIDOWED,
    DIVORCED,
    LIVING_SEPARATELY,
    LIVING_IN_LONGTERM_RELATIONSHIP;


    public static FamilyStatus fromString(String familyStatus) {
        try {
            return ofNullable(familyStatus).map(String::toUpperCase)
                    .map(FamilyStatus::valueOf)
                    .orElse(null);
        } catch (Exception ex) {
            return null;
        }
    }
}
