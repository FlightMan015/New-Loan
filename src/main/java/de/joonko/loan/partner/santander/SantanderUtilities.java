package de.joonko.loan.partner.santander;

import de.joonko.loan.offer.domain.LoanDuration;

import java.util.List;

public class SantanderUtilities {
    public static List<LoanDuration> getDurations(Integer loanAsked) {
        if (loanAsked >= 1000 && loanAsked < 5000)
            return List.of(LoanDuration.TWELVE, LoanDuration.TWENTY_FOUR, LoanDuration.THIRTY_SIX);
        else if (loanAsked >= 5000 && loanAsked < 10000)
            return List.of(LoanDuration.THIRTY_SIX, LoanDuration.FORTY_EIGHT, LoanDuration.SIXTY);
        else if (loanAsked >= 10000 && loanAsked < 20000)
            return List.of(LoanDuration.SIXTY, LoanDuration.SEVENTY_TWO, LoanDuration.EIGHTY_FOUR);
        else if (loanAsked >= 20000 && loanAsked <= 60000)
            return List.of(LoanDuration.SEVENTY_TWO, LoanDuration.EIGHTY_FOUR, LoanDuration.NINETY_SIX);
        else return List.of();
    }
}
