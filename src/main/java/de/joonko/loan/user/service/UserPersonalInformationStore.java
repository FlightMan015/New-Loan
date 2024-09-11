package de.joonko.loan.user.service;

import de.joonko.loan.offer.api.FamilyStatus;
import de.joonko.loan.offer.api.Gender;
import de.joonko.loan.offer.api.Nationality;
import de.joonko.loan.offer.api.ShortDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

import lombok.Data;

@Data
@Document
public class UserPersonalInformationStore {

    @Id
    private String userUUID;

    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private LocalDate birthDate;
    private String mobilePhone;

    // data
    private String addressCity;
    private String addressHouseNumber;
    private String addressStreet;
    private String addressZipCode;
    private ShortDate addressLivingSinceDate;

    private Nationality nationality;
    private String placeOfBirth;
    private String countryOfBirth;
    private FamilyStatus familyStatus;
    private Integer numberOfChildren;
    private Integer numberOfDependants;

}
