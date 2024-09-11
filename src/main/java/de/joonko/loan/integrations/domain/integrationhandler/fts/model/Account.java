package de.joonko.loan.integrations.domain.integrationhandler.fts.model;

import lombok.Data;

@Data
public class Account {
    private String holder;
    private String description;
    private String iban;
    private String bic;
    private String bankName;
    private String countryId;
    private Boolean jointAccount;
}
