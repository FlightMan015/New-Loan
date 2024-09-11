package de.joonko.loan.offer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDetails {
    private Gender gender;
    private FamilyStatus familyStatus;
    private LocalDate birthDate;
    private Integer numberOfCreditCard;
    private String firstName;
    private String lastName;
    private Boolean mainEarner;
    private HousingType housingType;
    private Nationality nationality;
    private Integer numberOfChildren;
    private Integer numberOfDependants;
    private Finance finance;
    private String placeOfBirth;
    private String countryOfBirth;
    private String taxId;

    public boolean hasRealEstate() {
        return HousingType.OWNER.equals(housingType) || finance.hasRentalIncome();
    }


}
