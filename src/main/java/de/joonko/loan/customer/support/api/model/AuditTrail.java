package de.joonko.loan.customer.support.api.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuditTrail{

    private String loanApplicationId;
    private String status;
    private String remark;
    private String loanProvider;
    private String error;
}
