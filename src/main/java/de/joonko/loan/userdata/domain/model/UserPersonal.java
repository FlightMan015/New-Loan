package de.joonko.loan.userdata.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import de.joonko.loan.offer.api.FamilyStatus;
import de.joonko.loan.offer.api.Gender;
import de.joonko.loan.offer.api.Nationality;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class UserPersonal {
    private boolean valid;

    private static final String NAME_FORMAT = "^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ,.'-]+$";

    @NotNull(message = "Gender must not be null")
    private Gender gender;

    @NotNull(message = "First name must not be null")
    @Pattern(regexp = NAME_FORMAT, message = "First name is in invalid format")
    @Size(min = 1, max = 70, message = "First name is too long")
    private String firstName;

    @NotNull(message = "Last name must not be null")
    @Pattern(regexp = NAME_FORMAT, message = "Last name is in invalid format")
    @Size(min = 1, max = 70, message = "Last name is too long")
    private String lastName;

    @NotNull(message = "Family status must not be null")
    private FamilyStatus familyStatus;

    @NotNull(message = "Birth date must not be null")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotNull(message = "Nationality must not be null")
    private Nationality nationality;

    @NotNull(message = "Place of Birth must not be null")
    private String placeOfBirth;

    @NotNull(message = "Country of Birth must not be null")
    private String countryOfBirth;
}
