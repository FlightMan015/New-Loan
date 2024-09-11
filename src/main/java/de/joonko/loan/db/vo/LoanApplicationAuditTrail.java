package de.joonko.loan.db.vo;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@KeySpace("loanApplicationAuditTrail")
@Data
@Builder
@Document
public class LoanApplicationAuditTrail {

    @Id
    private String loanApplicationAuditTrailId;
    private String applicationId;
    private String status;
    private String remark;
    private String loanProvider;
    private String error;
    @CreatedDate
    private LocalDateTime insertTs;

}
