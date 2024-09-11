package de.joonko.loan.userdata.domain.model;

import lombok.Data;

@Data
public class UserIncomes {

    private Double netIncome;
    private Double acknowledgedNetIncome;
    private Double incomeDeclared;
}
