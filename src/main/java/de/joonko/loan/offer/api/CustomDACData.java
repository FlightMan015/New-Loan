package de.joonko.loan.offer.api;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CustomDACData implements Serializable {

    @NotNull(message = "has3IncomeTags in customDACData must not be null")
    private Boolean has3IncomeTags;

    @NotNull(message = "netIncomeHasGovSupport in customDACData must not be null")
    private Boolean netIncomeHasGovSupport;

    @NotNull(message = "carInformation in customDACData must not be null")
    private Boolean carInformation;

    @NotNull(message = "hasSalary in customDACData must not be null")
    private Boolean hasSalary;

    @NotNull(message = "hasConsorsPreventionTags in customDACData must not be null")
    private Boolean hasConsorsPreventionTags;

    @NotNull(message = "hasSwkPreventionTags in customDACData must not be null")
    private Boolean hasSwkPreventionTags;

    @NotNull(message = "hasSalaryEachMonthLast3M in customDACData must not be null")
    private Boolean hasSalaryEachMonthLast3M;

    @NotNull(message = "wasDelayInInstallments40DaysDiff in customDACData must not be null")
    private Boolean wasDelayInInstallments40DaysDiff;

    @NotNull(message = "wasDelayInInstallments62DaysDiff in customDACData must not be null")
    private Boolean wasDelayInInstallments62DaysDiff;

    @NotNull(message = "isCurrentDelayInInstallments in customDACData must not be null")
    private Boolean isCurrentDelayInInstallments;

    @Builder.Default
    private BigDecimal sumIncomes1MAgo = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal sumIncomes2MAgo = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal sumIncomes3MAgo = BigDecimal.ZERO;

    @Builder.Default
    private Integer countEncashmentTag = 0;

    @Builder.Default
    private Integer countSeizureTag = 0;

    @Builder.Default
    private Integer countPAccountTag = 0;

    @Builder.Default
    private Integer countChargebackTag = 0;

    @NotNull(message = "totalIncomeInLast90Days in customDACData must not be null")
    private Double totalIncomeInLast90Days;

    @NotNull(message = "cashWithdrawalsInLast90Days in customDACData must not be null")
    private Double cashWithdrawalsInLast90Days;

    @NotNull(message = "gamblingAmountInLast90Days in customDACData must not be null")
    private Double gamblingAmountInLast90Days;

}
