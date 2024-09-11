package de.joonko.loan.acceptoffer.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private String holder;
    private String description;
    private String iban;
    private String bic;

    @JsonProperty("bank_name")
    private String bankName;

    @JsonProperty("country_id")
    private String countryId;

    @JsonProperty("joint_account")
    private Boolean isJointlyManaged;
}
