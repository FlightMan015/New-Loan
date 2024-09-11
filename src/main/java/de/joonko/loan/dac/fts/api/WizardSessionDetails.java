package de.joonko.loan.dac.fts.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class WizardSessionDetails {

    private String  bankName;
    private String  bankCode;
    private String  wizardSessionKey;
    private String  transactionId;
    private String  status;
    private String[] error;
    private String errorCode;
    private String recoverable;
}
