package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BorrowerContactData {
    @JsonProperty("living_since")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate livingSince;

    @JsonProperty("city")
    @NotBlank(message = "City is mandatory")
    private String city;

    @JsonProperty("street_number")
    @NotBlank(message = "Street Number is mandatory")
    private String streetNumber;

    @JsonProperty("mobile_telephone")
    private String mobileTelephone;

    @NotBlank(message = "Telephone is mandatory")
    private String telephone;

    @JsonProperty("street_name")
    @NotBlank(message = "Street Name is mandatory")
    private String streetName;
    @JsonProperty("zip_code")
    @NotBlank(message = "ZipCode is mandatory")
    private String zipCode;

    @JsonProperty("email")
    @NotBlank(message = "Email is mandatory")
    private String email;


}
