package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Documents {
    @JsonProperty("id")
    private String documentId;

    private String name;
    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("document_type")
    private String documentType;

    private Long size;
    @JsonProperty("customer_accessible")
    private Boolean customerAccessible;

    @JsonProperty("created_at")
    private String createdAt;

}
