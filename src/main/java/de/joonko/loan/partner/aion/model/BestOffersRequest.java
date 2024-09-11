package de.joonko.loan.partner.aion.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BestOffersRequest {

    @JsonProperty("name")
    private TransmissionDataType transmissionDataType;

    @JsonProperty("value")
    private BestOfferTransmissionData transmissionData;
}
