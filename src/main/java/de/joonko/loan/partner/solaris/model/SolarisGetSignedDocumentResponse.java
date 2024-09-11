package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class SolarisGetSignedDocumentResponse {

    @JsonProperty("id")
    private String signingId;
    private String reference;
    private String url;
    private String state;
    private String status;
    private String completed_at;
    private String method;
    @JsonProperty("identification_id")
    private String identificationId;
    private List<Documents> documents;

    public List<Documents> getSignedDocuments() {
        log.info("Documents received {} ", this.documents);
        return documents.stream()
                .filter(doc -> doc.getDocumentType().equalsIgnoreCase("SIGNED_CONTRACT"))
                .filter(doc -> doc.getName().contains("cdoc") || doc.getName().contains("ldoc"))
                .collect(Collectors.toList());
    }

}
