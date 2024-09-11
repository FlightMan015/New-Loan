package de.joonko.loan.identification.model.solaris;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SolarisGetIdentificationStatusResponse {

    private String id;

    private String reference;

    private String url;

    private String status;

    @JsonProperty("completed_at")
    private String completedAt;

    private String method;

    private Object address;

    @JsonProperty("documents")
    private List<SolarisGetIdentificationStatusDocumentResponse> documents = null;
}
