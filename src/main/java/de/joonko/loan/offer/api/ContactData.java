package de.joonko.loan.offer.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import de.joonko.loan.common.Regex;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static de.joonko.loan.common.Regex.ZIPCODE_REGEX;

@Data
@Builder(builderClassName = "ContactDataBuilder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(builder = ContactData.ContactDataBuilder.class)
public class ContactData implements Serializable {

    @NotNull(message = "Street name must not be null")
    @Size(min = 1, max = 70, message = "Street name is too long")
    private String streetName;

    @NotNull(message = "House number must not be null")
    @Size(min = 1, max = 70, message = "House number is too long")
    private String houseNumber;

    @NotNull(message = "Post code must not be null")
    @Pattern(regexp = ZIPCODE_REGEX, message = "Postal code should be 5 digits")
    private String postCode;

    @NotNull(message = "City must not be null")
    @Pattern(regexp = "[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð() ,.'-]+", message = "City is not valid")
    @Size(min = 1, max = 70, message = "City is too long")
    private String city;

    @Valid
    @NotNull(message = "LivingSince must not be null")
    private ShortDate livingSince;

    @Valid
    private PreviousAddress previousAddress;

    @Email(message = "Email is invalid")
    @NotNull(message = "Email must not be null")
    private String email;

    @NotNull(message = "Mobile number must not be null")
    @Pattern(regexp = Regex.MOBILE_NUMBER_REGEX, message = "Mobile number should be of length between 12 to 15")
    private String mobile;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ContactDataBuilder {


    }

}
