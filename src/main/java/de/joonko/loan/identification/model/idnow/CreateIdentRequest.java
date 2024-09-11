package de.joonko.loan.identification.model.idnow;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CreateIdentRequest {

    private String birthday;

    private String country;

    @JsonProperty("firstname")
    private String firstName;

    private String gender;

    private String city;

    @JsonProperty("lastname")
    private String lastName;

    @JsonProperty("zipcode")
    private String zipCode;

    private String birthplace;

    private String nationality;

    private String street;

    @JsonProperty("streetnumber")
    private String houseNumber;

    @JsonProperty("mobilephone")
    private String mobilePhone;

    private String custom3;

    private String custom4;

    private String custom1;

    private String custom2;

    private String custom5;

    private String email;

    private String preferredLang;
}
