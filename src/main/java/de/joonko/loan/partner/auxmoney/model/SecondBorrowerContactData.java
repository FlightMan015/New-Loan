package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecondBorrowerContactData {
    @JsonProperty("city")
    private String city;
    @JsonProperty("street_number")
    private String streetNumber;
    @JsonProperty("street_name")
    private String streetName;
    @JsonProperty("zip_code")
    private String zipCode;


}
