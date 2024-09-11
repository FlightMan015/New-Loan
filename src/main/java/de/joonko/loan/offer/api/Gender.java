package de.joonko.loan.offer.api;

import static java.util.Optional.ofNullable;

public enum Gender {

    MALE("male"), FEMALE("female");
    private String gender;

    Gender(String gender) {
        this.gender = gender;
    }

    public static Gender fromString(String genderAsString) {
        try {
            return ofNullable(genderAsString).map(String::toUpperCase)
                    .map(Gender::valueOf)
                    .orElse(null);
        } catch (Exception eg) {
            return null;
        }
    }


}
