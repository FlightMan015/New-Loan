package de.joonko.loan.integrations.domain.integrationhandler.fts.domain;


import de.joonko.loan.offer.api.AccountDetails;
import de.joonko.loan.offer.api.CustomDACData;
import de.joonko.loan.offer.api.CustomDacPersonalDetails;
import de.joonko.loan.offer.api.Expenses;
import de.joonko.loan.offer.api.Income;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;

//TODO check keyspace topic
@Data
@Document
public class UserTransactionalDataStore {

    @Id
    private String userUUID;

    @Valid
    @NotNull(message = "Income details must not be null")
    private Income income;

    @Valid
    @NotNull(message = "Expense details must not be null")
    private Expenses expenses;

    @Valid
    @NotNull(message = "Account details must not be null")
    private AccountDetails accountDetails;

    @NotNull(message = "FTS transaction id must not be null")
    private String ftsTransactionId;

    @NotNull(message = "DAC id must not be null")
    private String dacId;

    @Valid
    @NotNull(message = "CustomDACData details must not be null")
    private CustomDACData customDACData;

    @Valid
    @NotNull
    private CustomDacPersonalDetails customDacPersonalDetails;

    private LocalDateTime createdAt;


}
