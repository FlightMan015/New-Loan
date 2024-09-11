package de.joonko.loan.partner.santander.model.entry;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractEntryRequest {
    private String applicationNo;
    private String transactionId;
    private String actionId;
    private ContractDomain contractDomain;
}
