package de.joonko.loan.acceptoffer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private String holder;
    private String description;
    private String iban;
    private String bic;
    private String bankName;
    private String countryId;
    private Boolean jointAccount;
}
