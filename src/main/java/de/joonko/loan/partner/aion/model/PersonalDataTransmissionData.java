package de.joonko.loan.partner.aion.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonalDataTransmissionData implements TransmissionData {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("surname")
    private String lastName;

    @JsonProperty("phone_prefix")
    @Builder.Default
    private String phonePrefix = "49";

    @JsonProperty("mobile_phone_number")
    private String mobilePhoneNumber;

    @JsonProperty("email")
    private String email;
}
