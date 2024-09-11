package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaxInformation {

    @JsonProperty("tax_assessment")
    private String taxAssessment;

    @JsonProperty("marital_status")
    private String maritalStatus;

}
