package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private Boolean jointAccount;
}
