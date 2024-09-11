package de.joonko.loan.userdata.infrastructure.draft.model;

import lombok.Data;

@Data
public class UserExpensesStore {

    private Double monthlyLoanInstallmentsDeclared;
    private Boolean isCurrentDelayInInstallmentsDeclared;
    private Double monthlyLifeCost;
    private Double creditCardLimitDeclared;
}
