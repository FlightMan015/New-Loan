package de.joonko.loan.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LoanCalculatorUtil {

    public static SimpleLoan calculateSimpleLoan(final BigDecimal amount, final int durationInMonth, final BigDecimal effectiveInterestRate) {
        final var monthlyInstallment = effectiveInterestRate.divide(BigDecimal.valueOf(1200), 5, RoundingMode.HALF_UP);
        final var divider = ((BigDecimal.ONE.add(monthlyInstallment)).pow(durationInMonth).subtract(BigDecimal.ONE))
                .divide(monthlyInstallment.multiply(BigDecimal.ONE.add(monthlyInstallment).pow(durationInMonth)), 5, RoundingMode.HALF_UP);
        final var monthlyRepayment = amount.divide(divider, 2, RoundingMode.HALF_UP);
        final var totalRepayment = monthlyRepayment.multiply(BigDecimal.valueOf(durationInMonth));
        return SimpleLoan.builder()
                .durationInMonth(durationInMonth)
                .amount(amount)
                .effectiveInterestRate(effectiveInterestRate)
                .nominalInterestRate(calculateNominalInterestRateFromEffective(effectiveInterestRate, durationInMonth))
                .monthlyRate(monthlyRepayment)
                .totalPayment(totalRepayment)
                .build();
    }

    public static BigDecimal calculateNominalInterestRateFromEffective(final BigDecimal effectiveInterestRate, final int durationInMonths) {
        return BigDecimal.valueOf(durationInMonths).multiply(
                        BigDecimal.valueOf(
                                        Math.pow(
                                                BigDecimal.ONE.add(effectiveInterestRate.divide(BigDecimal.valueOf(100), 5, RoundingMode.HALF_UP)).doubleValue(),
                                                BigDecimal.ONE.divide(BigDecimal.valueOf(durationInMonths), 5, RoundingMode.HALF_UP).doubleValue()))
                                .subtract(BigDecimal.ONE)
                ).multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
