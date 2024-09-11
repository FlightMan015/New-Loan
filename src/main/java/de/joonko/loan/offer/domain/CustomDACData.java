package de.joonko.loan.offer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomDACData {

    private Boolean has3IncomeTags;
    private Boolean netIncomeHasGovSupport;
    private Boolean carInformation;
    private Boolean hasSalary;
    private Boolean hasConsorsPreventionTags;
    private Boolean hasSwkPreventionTags;
    private Double totalIncomeInLast90Days;
    private Double cashWithdrawalsInLast90Days;
    private Double gamblingAmountInLast90Days;

    private Integer countEncashmentTag;
    private Integer countSeizureTag;
    private Integer countPAccountTag;
    private Integer countChargebackTag;

    private Boolean hasSalaryEachMonthLast3M;

    private Boolean wasDelayInInstallments40DaysDiff;
    private Boolean wasDelayInInstallments62DaysDiff;
    private Boolean isCurrentDelayInInstallments;

    private BigDecimal sumIncomes1MAgo;
    private BigDecimal sumIncomes2MAgo;
    private BigDecimal sumIncomes3MAgo;
}
