package de.joonko.loan.offer.api;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import de.joonko.loan.offer.api.validator.ValidTaxId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PersonalDetails implements Serializable {

    private final static String NAME_FORMAT = "^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ,.'-]+$";

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

    @AssertTrue(message = "Age not in valid range")
    public boolean isBirthDateValid() {
        return getBirthDate() == null || (getBirthDate().isBefore(LocalDate.now().minusYears(18)) && getBirthDate().isAfter(LocalDate.now().minusYears(100)));
    }

    @NotNull(message = "Nationality must not be null")
    private Nationality nationality;

    @NotNull(message = "Place of Birth must not be null")
    private String placeOfBirth;

    @NotNull(message = "Country of Birth must not be null")
    private String countryOfBirth;

    @NotNull(message = "Number of Children must not be null")
    @Min(0)
    @Max(9)
    @Builder.Default
    private Integer numberOfChildren = 0;

    @NotNull(message = "Number of Dependants must not be null")
    @Min(0)
    @Builder.Default
    private Integer numberOfDependants = 0;

    @NotNull(message = "HousingType must not be null")
    private HousingType housingType;

    private Integer numberOfCreditCard;

    @ValidTaxId
    private String taxId;
}
