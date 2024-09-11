package de.joonko.loan.webhooks.idnow.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdentificationDocument {

    IdentificationDocumentTuple type;
    IdentificationDocumentTuple country;
    @JsonProperty("validuntil")
    IdentificationDocumentTuple validUntil;
    IdentificationDocumentTuple number;
    @JsonProperty("issuedby")
    IdentificationDocumentTuple issuedBy;
    @JsonProperty("dateissued")
    IdentificationDocumentTuple dateIssued;


}
