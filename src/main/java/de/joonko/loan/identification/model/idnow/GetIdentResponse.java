package de.joonko.loan.identification.model.idnow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class GetIdentResponse {

    String provider;

    @JsonProperty("identificationprocess")
    private IdentificationProcess identificationProcess;

    @JsonProperty("customdata")
    private CustomData customdata;

    @JsonProperty("contactdata")
    private ContactData contactData;

    @JsonProperty("userdata")
    private UserData userData;

    @JsonProperty("identificationdocument")
    private IdentificationDocument identificationDocument;

    private Attachments attachments;
}
