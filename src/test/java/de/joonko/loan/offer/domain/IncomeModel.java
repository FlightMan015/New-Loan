package de.joonko.loan.offer.domain;

import java.math.BigDecimal;

public class IncomeModel {

    public static Income basicIncome() {
        return Income.builder()
                .rentalIncome(BigDecimal.valueOf(10.01))
                .alimonyPayments(BigDecimal.valueOf(10.02))
                .childBenefits(BigDecimal.valueOf(10.03))
                .netIncome(BigDecimal.valueOf(10.03))
                .otherRevenue(BigDecimal.valueOf(10.04))
                .pensionBenefits(BigDecimal.valueOf(10.05))
                .build();
    }

    public static Income income() {
        return Income.builder()
                .rentalIncome(BigDecimal.valueOf(120))
                .alimonyPayments(BigDecimal.valueOf(210))
                .childBenefits(BigDecimal.valueOf(400))
                .netIncome(BigDecimal.valueOf(4216))
                .otherRevenue(BigDecimal.valueOf(54))
                .pensionBenefits(BigDecimal.valueOf(250))
                .build();
    }
}
