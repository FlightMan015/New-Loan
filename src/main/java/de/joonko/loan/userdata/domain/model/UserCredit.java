package de.joonko.loan.userdata.domain.model;

import lombok.Data;


@Data
public class UserCredit {
    private boolean valid;

    private UserExpenses userExpenses;
    private UserIncomes userIncomes;
}
