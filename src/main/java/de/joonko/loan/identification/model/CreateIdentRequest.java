package de.joonko.loan.identification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CreateIdentRequest {

    @NotBlank(message = "applicationId must not be null")
    private String applicationId;
    @NotBlank(message = "loanOfferId must not be null")
    private String loanOfferId;
    private Integer duration;

    @NotBlank(message = "First name must not be null")
    private String firstName;

    @NotBlank(message = "Last name must not be null")
    private String lastName;

    @NotBlank(message = "Gender must not be null")
    private String gender;

    @NotBlank(message = "Birthday must not be null")
    private String birthday;
    private String birthplace;
    private String street;
    private String houseNumber;

    @NotBlank(message = "City phone must not be null")
    private String city;

    @NotBlank(message = "ZipCode phone must not be null")
    private String zipCode;

    @NotBlank(message = "Country phone must not be null")
    private String country;

    private String nationality;

    @NotBlank(message = "mobile phone must not be null")
    private String mobilePhone;

    @NotBlank(message = "email must not be null")
    private String email;

    @NotBlank(message = "language must not be null")
    private String language;

    @NotBlank(message = "loanProvider must not be null")
    private String loanProvider;

    private boolean advertisingConsent;

    public String getMobilePhone() {
        return Optional.ofNullable(mobilePhone)
                .map(mobile -> mobile.startsWith("+") ? mobile : mobile.replaceFirst("^0*49", "+49"))
                .orElse("");
    }
}
