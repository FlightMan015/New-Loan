package de.joonko.loan.integrations.domain.integrationhandler.fts.domain;


import de.joonko.loan.acceptoffer.domain.Account;
import de.joonko.loan.acceptoffer.domain.Balance;
import de.joonko.loan.offer.domain.TransactionDraft;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Document
public class UserTransactionalDraftDataStore {

    @Id
    private String userUUID;

    @Valid
    @NotNull(message = "accountInternalId must not be null")
    private String accountInternalId;

    @Valid
    @NotNull
    @Builder.Default
    private Boolean internalUse = false;

    @Valid
    @NotNull
    private Account account;

    @Valid
    @NotNull
    private Balance balance;

    @Valid
    @NotNull
    @NotEmpty
    private List<TransactionDraft> transactions;

    @Valid
    @NotNull
    private Long createdAt;
}
