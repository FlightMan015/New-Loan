package de.joonko.loan.userdata.api.model;

import de.joonko.loan.offer.api.FamilyStatus;
import de.joonko.loan.offer.api.Gender;
import de.joonko.loan.offer.api.Nationality;
import lombok.*;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserPersonal {
    private boolean valid;

    private Gender gender;
    private String firstName;
    private String lastName;
    private FamilyStatus familyStatus;
    private LocalDate birthDate;
    private Nationality nationality;
    private String placeOfBirth;
    private String countryOfBirth;
}
