package de.joonko.loan.partner.aion.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsentValue implements TransmissionData {

    @JsonProperty("is_accepted")
    private Boolean isAccepted;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("version")
    @Builder.Default
    private String version = "1";
}
