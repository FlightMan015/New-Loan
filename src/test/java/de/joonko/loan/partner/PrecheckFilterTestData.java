package de.joonko.loan.partner;

import de.joonko.loan.offer.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PrecheckFilterTestData {
    public LoanDemand getBaseLoanDemand(Integer loanAmount) {
        Income income = Income.builder()
                .netIncome(BigDecimal.valueOf(1400)).build();
        Finance finance = new Finance(income, null, BigDecimal.TEN);
        PersonalDetails personalDetails = PersonalDetails.builder()
                .birthDate(LocalDate.now().minusYears(30))
                .finance(finance)
                .build();
        EmploymentDetails employmentDetails = EmploymentDetails.builder()
                .employmentType(EmploymentType.REGULAR_EMPLOYED)
                .employmentSince(LocalDate.now().minusMonths(7))
                .build();
        DigitalAccountStatements digitalAccountStatements = DigitalAccountStatements.builder()
                .isJointlyManaged(false).build();
        CustomDACData customDACData = CustomDACData.builder()
                .hasSalary(true)
                .has3IncomeTags(true)
                .hasSalaryEachMonthLast3M(true)
                .netIncomeHasGovSupport(false)
                .cashWithdrawalsInLast90Days(700.0)
                .totalIncomeInLast90Days(4000.0)
                .sumIncomes1MAgo(BigDecimal.valueOf(4000))
                .sumIncomes2MAgo(BigDecimal.valueOf(4000))
                .sumIncomes3MAgo(BigDecimal.valueOf(4000))
                .gamblingAmountInLast90Days(300.0)
                .countEncashmentTag(0)
                .countSeizureTag(0)
                .countPAccountTag(0)
                .countChargebackTag(0)
                .build();

        return new LoanDemand("loanApplicationId", loanAmount, "other", null, null,
                personalDetails, CreditDetails.builder().build(), employmentDetails, null, digitalAccountStatements, null,
                null, null, null, customDACData, List.of(), "userUuid");
    }

    public LoanDemand getLoanDemandWithBirthDate(int minusYears) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getPersonalDetails().setBirthDate(LocalDate.now().minusYears(minusYears));

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithProfessionEndDate(LocalDate contractEndDate) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getEmploymentDetails().setProfessionEndDate(contractEndDate);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithSwkPreventionTags(Boolean hasTags) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getCustomDACData().setHasSwkPreventionTags(hasTags);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithEnchashmentTags(Integer value) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getCustomDACData().setCountEncashmentTag(value);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithSeizureTags(Integer value) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getCustomDACData().setCountSeizureTag(value);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithPAccountTags(Integer value) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getCustomDACData().setCountPAccountTag(value);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithChargebackTags(Integer value) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getCustomDACData().setCountChargebackTag(value);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithNetIncome(BigDecimal income) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getPersonalDetails().getFinance().getIncome().setNetIncome(income);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithTotal90DaysIncome(Double income) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getCustomDACData().setTotalIncomeInLast90Days(income);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithLast3MSalaries(final BigDecimal income1MAgo, final BigDecimal income2MAgo, final BigDecimal income3MAgo) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getCustomDACData().setSumIncomes1MAgo(income1MAgo);
        loanDemand.getCustomDACData().setSumIncomes2MAgo(income2MAgo);
        loanDemand.getCustomDACData().setSumIncomes3MAgo(income3MAgo);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithBonimaScore(final Integer bonimaScore) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getCreditDetails().setBonimaScore(bonimaScore);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithDeclaredDelayInInstallments(final Boolean declaredDelay) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getCreditDetails().setIsCurrentDelayInInstallmentsDeclared(declaredDelay);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithFinance(Finance finance) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getPersonalDetails().setFinance(finance);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithEmploymentSince(LocalDate localDate) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getEmploymentDetails().setEmploymentSince(localDate);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithGamblingAmount(Double gamblingAmount) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getCustomDACData().setGamblingAmountInLast90Days(gamblingAmount);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithEmploymentType(EmploymentType employmentType) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getEmploymentDetails().setEmploymentType(employmentType);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithAcceptedCashWithdrawalRatio(Double totalIncomeInLast90Days, Double cashWithdrawalsInLast90Days) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getCustomDACData().setTotalIncomeInLast90Days(totalIncomeInLast90Days);
        loanDemand.getCustomDACData().setCashWithdrawalsInLast90Days(cashWithdrawalsInLast90Days);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithJointlyManaged(Boolean isJointlyManaged) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getDigitalAccountStatements().setIsJointlyManaged(isJointlyManaged);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithHasSalary(Boolean hasSalary) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getCustomDACData().setHasSalary(hasSalary);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWith3IncomeTagsOr3MSalary(Boolean has3IncomeTags, Boolean has3MSalary) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getCustomDACData().setHas3IncomeTags(has3IncomeTags);
        loanDemand.getCustomDACData().setHasSalaryEachMonthLast3M(has3MSalary);

        return loanDemand;
    }

    public LoanDemand getLoanDemandWithNetIncomeHasNoGovSupport(Boolean netIncomeHasNoGovSupport) {
        LoanDemand loanDemand = getBaseLoanDemand(5000);
        loanDemand.getCustomDACData().setNetIncomeHasGovSupport(netIncomeHasNoGovSupport);

        return loanDemand;
    }
}
