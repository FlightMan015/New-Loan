package de.joonko.loan.userdata.infrastructure.draft.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserCreditStore {

    private UserExpensesStore userExpenses;
    private UserIncomesStore userIncomes;
}
