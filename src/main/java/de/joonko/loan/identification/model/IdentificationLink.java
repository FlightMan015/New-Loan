package de.joonko.loan.identification.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@KeySpace("identificationLinks")
@Data
@Builder
@Document
public class IdentificationLink {
    @Id
    private String linkId;
    private String applicationId;
    private String offerId;
    private String loanProvider;
    private IdentificationProvider identProvider;
    private String externalIdentId;
    private String kycUrl;

    @CreatedDate
    private LocalDateTime insertTs;
}
