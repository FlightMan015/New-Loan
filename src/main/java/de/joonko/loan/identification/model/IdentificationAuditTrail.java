package de.joonko.loan.identification.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@KeySpace("IdentificationAuditTrail")
@Data
@Builder
@Document
public class IdentificationAuditTrail {
    @Id
    private String kycAuditId;
    private String applicationId;
    private String status;
    private String error;
    private String remark;
    private String loanProvider;

    @CreatedDate
    private LocalDateTime insertTs;
}
