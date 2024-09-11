package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {

    @JsonProperty("line_1")
    @NotNull(message = "Address Line1 cannot be null")
    private  String line1;

    @JsonProperty("line_2")
    private  String line2;

    @JsonProperty("postal_code")
    @NotNull(message = "Address Postal code cannot be null")
    private  String postalCode;

    @JsonProperty("city")
    @NotNull(message = "Address city cannot be null")
    private  String city;

    @JsonProperty("country")
    private  String country;

    @JsonProperty("state")
    private  String state;
}
