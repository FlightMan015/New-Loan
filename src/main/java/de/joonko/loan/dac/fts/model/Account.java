package de.joonko.loan.dac.fts.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @JsonProperty("holder")
    private String holder;

    @JsonProperty("description")
    private String description;

    @JsonProperty("iban")
    private String iban;

    @JsonProperty("bic")
    private String bic;

    @JsonProperty("bank_name")
    private String bankName;

    @JsonProperty("country_id")
    private String countryId;

    @JsonProperty("joint_account")
    private boolean isJointAccount;

    @JsonProperty("type")
    private String type;
}
