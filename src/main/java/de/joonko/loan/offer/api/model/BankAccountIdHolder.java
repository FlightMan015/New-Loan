package de.joonko.loan.offer.api.model;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BankAccountIdHolder {

    private final String accountInternalId;
}
