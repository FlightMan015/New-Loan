package de.joonko.loan.offer.domain;

import java.math.BigDecimal;

import static de.joonko.loan.util.BigDecimalUtil.add;

public class DisposableAmountCalculator {

    private static final BigDecimal FIXED_AVERAGE_EXPENSE_FOR_CHILDCARE = BigDecimal.valueOf(200);
    private static final BigDecimal FIXED_AVERAGE_EXPENSE_FOR_CAR = BigDecimal.valueOf(300);

    public static BigDecimal calculateDisposableAmount(final Income income, final Expenses expenses, final int numberOfChildren, final int numberOfCars) {
        return calculateIncomeForDisposableAmount(income).subtract(calculateExpensesForDisposableAmount(expenses, numberOfChildren, numberOfCars));
    }

    private static BigDecimal calculateIncomeForDisposableAmount(final Income income) {
        return add(income.getNetIncome(), income.getPensionBenefits(), income.getChildBenefits());
    }

    private static BigDecimal calculateExpensesForDisposableAmount(final Expenses expenses, final int numberOfChildren, final int numberOfCars) {
        return add(expenses.getAcknowledgedMortgages(),
                expenses.getLoanInstalments(),
                expenses.getPrivateHealthInsurance(),
                expenses.getInsuranceAndSavings(),
                expenses.getAlimony(),
                expenses.getVehicleInsurance(),
                expenses.getAcknowledgedRent(),
                FIXED_AVERAGE_EXPENSE_FOR_CHILDCARE.multiply(BigDecimal.valueOf(numberOfChildren)),
                FIXED_AVERAGE_EXPENSE_FOR_CAR.multiply(BigDecimal.valueOf(numberOfCars))
        );
    }
}
