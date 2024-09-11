package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class AuxmoneyGetOffersResponse implements Serializable {
    private List<Offers> offers;

    @JsonProperty("credit_id")
    private Integer creditId;

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("is_error")
    private Boolean isError;

    @JsonProperty("is_success")
    private Boolean isSuccess;
}
