package de.joonko.loan.userdata.api.model;

import lombok.Data;

@Data
public class UserAccount {
    private String nameOnAccount;
    private String iban;
    private String bic;
    private String bankName;
}
