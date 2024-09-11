package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Household {
    @JsonProperty("people_living_in_household")
    private int peopleLivingInHousehold;

    @JsonProperty("adults_living_in_household")
    private int adultsLivingInHousehold;


}
