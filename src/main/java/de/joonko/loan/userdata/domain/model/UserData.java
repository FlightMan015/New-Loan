package de.joonko.loan.userdata.domain.model;

import lombok.Data;

@Data
public class UserData {
    private UserPersonal userPersonal;
    private UserContact userContact;
    private UserEmployment userEmployment;
    private UserHousing userHousing;
    private UserCredit userCredit;
    private UserAccount userAccount;

    public boolean additionalFieldsForHighAmountArePresent() {
        return userCredit != null &&
                userCredit.getUserExpenses() != null &&
                userCredit.getUserExpenses().getCreditCardLimitDeclared() != null &&
                userCredit.getUserExpenses().getIsCurrentDelayInInstallmentsDeclared() != null &&
                userCredit.getUserExpenses().getMonthlyLoanInstallmentsDeclared() != null &&
                userCredit.getUserExpenses().getMonthlyLifeCost() != null &&
                userCredit.getUserIncomes() != null &&
                userCredit.getUserIncomes().getIncomeDeclared() != null;
    }
}
