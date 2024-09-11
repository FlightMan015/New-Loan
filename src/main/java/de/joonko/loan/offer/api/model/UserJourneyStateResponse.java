package de.joonko.loan.offer.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserJourneyStateResponse {

    private Integer amount;

    private UserJourneyState state;

    private String purpose;

    public enum UserJourneyState {
        MISSING_LOAN_AMOUNT,
        EXISTING_LOAN_AMOUNT
    }

}
