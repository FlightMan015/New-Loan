package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class AuxmoneyCreateContractRequest {
    @JsonProperty("credit_id")
    private String creditId;

    @JsonProperty("user_id")
    private String userId;
}
