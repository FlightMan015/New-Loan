package de.joonko.loan.partner.aion.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferResponseValue {

    @JsonProperty("category")
    private String category;

    @JsonProperty("orderId")
    @Builder.Default
    private Integer orderId = 1;

    @JsonProperty("offerDetails")
    private OfferDetails offerDetails;
}
