package de.joonko.loan.webhooks.postbank.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebtorInformation {

    private Integer debtor;

    private Boolean knownDebtor;

    private String digitaleSignaturUrl;

    private String debtorType;

    private String videoLegiUrl;
}
