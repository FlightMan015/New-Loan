package de.joonko.loan.partner.auxmoney.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CorrelationDataRequest {
    private String transactionNo;
    private String auxCreditNo;
    private String auxUserNo;
    private String idNowTransactionId;
}
