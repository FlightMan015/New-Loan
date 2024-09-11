package de.joonko.loan.partner.consors.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccount {
    private BankAccountOwner owner; // optional - GetOffersRequest.DigitalAccountStatements.owner

    private String blz; // required if iban is empty - not supported by UI

    private String accountSince; // required - can we infer this information ??

    private String iban; // required if accountNumber is empty - GetOffersRequest.DigitalAccountStatements.iban

    private String accountNumber; // required if iban is empty - not supported by UI

    private String bic; // required if iban not starting with DE - GetOffersRequest.DigitalAccountStatements.bic

}
