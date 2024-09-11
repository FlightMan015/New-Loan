package de.joonko.loan.userdata.api.model;

import lombok.Data;

@Data
public class UserExpenses {

    private Double loanInstalments;
    private Double monthlyLoanInstallmentsDeclared;

    private Boolean isCurrentDelayInInstallments;
    private Boolean isCurrentDelayInInstallmentsDeclared;

    private Double monthlyLifeCost;
    private Double creditCardLimitDeclared;
    private Integer numberOfCreditCard;
}
