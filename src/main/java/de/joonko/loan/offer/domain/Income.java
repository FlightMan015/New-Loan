package de.joonko.loan.offer.domain;

import de.joonko.loan.util.Util;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Income {

    private BigDecimal netIncome;
    private BigDecimal childBenefits;

    private BigDecimal pensionBenefits;
    private BigDecimal otherRevenue;
    private BigDecimal rentalIncome;
    private BigDecimal alimonyPayments;
    private BigDecimal acknowledgedNetIncome;
    private BigDecimal incomeDeclared;

    public int getNetIncomeInEuroCent() {
        return Util.toEuroCent(netIncome);
    }

    public int getAcknowledgedNetIncomeEuroCent() {
        return Util.toEuroCent(acknowledgedNetIncome);
    }


    public int getOtherRevenueInEuroCent() {
        return Util.toEuroCent(otherRevenue);
    }

    public int getRentalIncomeInEuroCent() {
        return Util.toEuroCent(rentalIncome);
    }

    public int getAlimonyPaymentsInEuroCent() {
        return Util.toEuroCent(alimonyPayments);
    }

    public int getChildBenefitsInEuroCent() {
        return Util.toEuroCent(childBenefits);
    }

    public int getPensionBenefitsInEuroCent() {
        return Util.toEuroCent(pensionBenefits);
    }

    public BigDecimal getSumOfAllIncomes() {
        return netIncome.add(childBenefits)
                .add(pensionBenefits)
                .add(otherRevenue)
                .add(rentalIncome)
                .add(alimonyPayments);
    }

}
