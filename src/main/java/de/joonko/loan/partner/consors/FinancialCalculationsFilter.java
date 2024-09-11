package de.joonko.loan.partner.consors;

import de.joonko.loan.partner.consors.model.FinancialCalculation;
import lombok.AllArgsConstructor;

import java.util.function.Predicate;

@AllArgsConstructor
public class FinancialCalculationsFilter implements Predicate<FinancialCalculation> {

    private final int minCreditAmount;

    @Override
    public boolean test(FinancialCalculation financialCalculation) {
        return financialCalculation.getCreditAmount() >= minCreditAmount;
    }

    public int compare(FinancialCalculation a, FinancialCalculation b) {
        if (a.getCreditAmount().equals(b.getCreditAmount())) {
            return a.getDuration().compareTo(b.getDuration());
        }
        return a.getCreditAmount().compareTo(b.getCreditAmount());
    }
}
