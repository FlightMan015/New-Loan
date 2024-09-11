package de.joonko.loan.offer.domain;

import de.joonko.loan.common.domain.LoanProvider;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@RequiredArgsConstructor
@Builder
public class LoanOffer {

    private final int amount;
    private final int durationInMonth;
    private final BigDecimal effectiveInterestRate;
    private final BigDecimal nominalInterestRate;
    private final BigDecimal monthlyRate;
    private final BigDecimal totalPayment;
    private final LoanProvider loanProvider;
    private final String loanProviderOfferId;

    public LoanOffer(int amount, int durationInMonth, BigDecimal effectiveInterestRate, BigDecimal nominalInterestRate, BigDecimal monthlyRate, BigDecimal totalPayment, LoanProvider loanProvider) {
        this.amount = amount;
        this.durationInMonth = durationInMonth;
        this.effectiveInterestRate = effectiveInterestRate;
        this.nominalInterestRate = nominalInterestRate;
        this.monthlyRate = monthlyRate;
        this.totalPayment = totalPayment;
        this.loanProvider = loanProvider;
        this.loanProviderOfferId = null;
    }

    int calculateScore(Finance finance) {
        int freeToSpendAmount = finance.getRoundUpFreeToSpendAmount();
        BigDecimal halfOfFreeToSpend = getHalfValue(freeToSpendAmount);
        return score(halfOfFreeToSpend);
    }

    int interestPayment() {
        return totalPayment.subtract(BigDecimal.valueOf(amount)).intValue();
    }

    int halfOfFreeToSpend(Finance finance) {
        int freeToSpendAmount = finance.getRoundUpFreeToSpendAmount();
        return getHalfValue(freeToSpendAmount).intValue();
    }

    private int score(BigDecimal halfOfFreeToSpend) {
        return monthlyRate.subtract(halfOfFreeToSpend)
                .abs()
                .intValue();
    }

    private BigDecimal getHalfValue(int freeToSpendAmount) {
        return BigDecimal.valueOf(freeToSpendAmount)
                .divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
    }
}
