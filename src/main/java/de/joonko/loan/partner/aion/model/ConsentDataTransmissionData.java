package de.joonko.loan.partner.aion.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsentDataTransmissionData implements TransmissionData {
    
    @JsonProperty("aion_privacy_policy")
    private ConsentValue aionPrivacyPolicyConsent;

    @JsonProperty("data_transfer_consent")
    private ConsentValue dataTransferConsent;

    @JsonProperty("marketing_consent")
    private ConsentValue marketingConsent;
}
