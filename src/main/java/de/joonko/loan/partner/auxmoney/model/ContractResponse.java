package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ContractResponse {
    @JsonProperty("credit_id")
    private String creditId;

    @JsonProperty("user_id")
    private String userId;

    private byte[] contract;

    private Boolean success;

    @JsonProperty("payout_credit_amount")
    private Double payoutCreditAmount;
}
