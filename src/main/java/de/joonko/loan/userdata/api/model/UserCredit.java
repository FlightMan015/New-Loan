package de.joonko.loan.userdata.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class UserCredit {
    private boolean valid;

    private UserExpenses userExpenses;
    private UserIncomes userIncomes;
}
