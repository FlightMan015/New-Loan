package de.joonko.loan.integrations.domain.enhancers;

import lombok.Data;

@Data
public class KycRelatedPersonalDetails {

    private String nameOnAccount;
    private String iban;
    private String bic;
}
