package de.joonko.loan.webhooks.idnow.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private UserAttribute<String> street;
    private UserAttribute<String> city;
    private UserAttribute<String> country;
    @JsonProperty("streetnumber")
    private UserAttribute<String> streetNumber;
    @JsonProperty("zipcode")
    private UserAttribute<String> zipCode;
}
