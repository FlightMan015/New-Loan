package de.joonko.loan.integrations.domain.integrationhandler.fts.model;

import de.joonko.loan.acceptoffer.api.Account;
import de.joonko.loan.acceptoffer.api.Balance;

import java.util.List;

import lombok.Data;

@Data
public class FinleapToFtsTransactionalData {

    private String userUUID;
    private String accountInternalId;
    private Boolean internalUse;
    private Long createdAt;
    private Account account;
    private Balance balance;
    private List<Transaction> transactions;

}
