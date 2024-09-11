package de.joonko.loan.identification.model.solaris;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SolarisGetIdentificationStatusDocumentResponse {

    @JsonProperty("id")
    private String documentId;

    private String name;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("document_type")
    private String documentType;

    private Integer size;
}
