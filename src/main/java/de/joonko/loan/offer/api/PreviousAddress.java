package de.joonko.loan.offer.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.io.Serializable;

import static de.joonko.loan.common.Regex.ZIPCODE_REGEX;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreviousAddress implements Serializable {
    @NotNull(message = "PreviousAddress Street name must not be null")
    @Size(min = 1, max = 70, message = "PreviousAddress Street name is too long")
    private String streetName;

    @NotNull(message = "PreviousAddress Post code must not be null")
    @Pattern(regexp = ZIPCODE_REGEX, message = "PreviousAddress Post code should be 5 digits")
    private String postCode;

    @NotNull(message = "PreviousAddress City must not be null")
    @Pattern(regexp = "[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð() ,.'-]+", message = "PreviousAddress City is not valid")
    @Size(min = 1, max = 70, message = "PreviousAddress City is too long")
    private String city;

    @NotNull(message = "PreviousAddress Country must not be null")
    private Nationality country;

    @Size(min = 1, max = 70, message = "House number is too long")
    private String houseNumber;

    @NotNull(message = "livingSince must not be null")
    @Valid
    private ShortDate livingSince;

}
