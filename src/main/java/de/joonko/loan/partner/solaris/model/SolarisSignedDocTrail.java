package de.joonko.loan.partner.solaris.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;
import org.springframework.data.mongodb.core.mapping.Document;

@KeySpace("SolarisSignedDocTrail")
@Data
@Builder
@Document
public class SolarisSignedDocTrail {
    @Id
    private String signedDocId;
    private String applicationId;
    private boolean emailSent;
}
