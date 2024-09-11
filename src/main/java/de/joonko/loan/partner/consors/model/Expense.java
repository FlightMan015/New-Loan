package de.joonko.loan.partner.consors.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Expense {

    private int warmRent;
    private Integer spendingOnOtherChildren;
    private Integer creditsAnother;
    private int otherHouseholdObligations;
    private Boolean privateHealthInsurance;
    private boolean hasResidentialProperty;
    private String realEstate;

}
