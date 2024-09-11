package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartnerBroker {
    private String phone;

    private String city;

    @JsonProperty("broker_id")
    private String brokerId;

    @JsonProperty("company_name")
    private String companyName;
    @JsonProperty("street_number")
    private String streetNumber;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("email")
    private String email;

    @JsonProperty("street_name")
    private String streetName;
    @JsonProperty("zipCode")
    private String zipCode;


}
