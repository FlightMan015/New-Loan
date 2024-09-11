package de.joonko.loan.partner.consors;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.PreCheckEnum;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.function.Predicate;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.nonNull;

@Slf4j
@AllArgsConstructor
@Service
public class ConsorsPrecheckFilter implements Predicate<LoanDemand> {

    private final ConsorsPropertiesConfig propertiesConfig;

    @Override
    public boolean test(@NotNull LoanDemand loanDemand) {
        if (!hasRequiredNonNullFields(loanDemand)) {
            log.info("userId: {}, precheck failed, required non null fields", loanDemand.getUserUUID());
            return false;
        }

        boolean acceptedUserAge = isAcceptedUserAge(loanDemand);
        boolean acceptedIncome = hasIncomeGreaterThanOrEqual(loanDemand);
        boolean acceptedNotJointlyAccount = isJointlyManagedAccount(loanDemand);
        boolean acceptedSalaryAccount = hasSalaryAccount(loanDemand);
        boolean acceptedIncomeTags = has3IncomeTags(loanDemand);
        boolean acceptedNoGovSupport = netIncomeHasNoGovSupport(loanDemand);
        boolean acceptedEncashmentTags = acceptedEncashmentTags(loanDemand);
        boolean acceptedSeizureTags = acceptedSeizureTags(loanDemand);
        boolean acceptedPAccountTags = acceptedPAccountTags(loanDemand);
        boolean acceptedChargebackTags = acceptedChargebackTags(loanDemand);
        boolean acceptedEmploymentSince = isAcceptedEmploymentSinceDate(loanDemand);
        boolean acceptedLoanAmount = isAcceptedLoanAmount(loanDemand);

        log.info("userId: {}, acceptedUserAge: {}, acceptedIncome: {}, acceptedNotJointlyAccount: {}, acceptedSalaryAccount: {}, acceptedIncomeTags: {}, " +
                        "acceptedNoGovSupport: {}, acceptedEmploymentSince: {}, " +
                        "acceptedEncashmentTags: {}, acceptedSeizureTags: {}, acceptedPAccountTags: {}, acceptedChargebackTags: {}, acceptedLoanAmount: {}"
                , loanDemand.getUserUUID(), acceptedUserAge,
                acceptedIncome, acceptedNotJointlyAccount, acceptedSalaryAccount, acceptedIncomeTags, acceptedNoGovSupport, acceptedEmploymentSince,
                acceptedEncashmentTags, acceptedSeizureTags, acceptedPAccountTags, acceptedChargebackTags, acceptedLoanAmount);


        return acceptedUserAge && acceptedIncome && acceptedNotJointlyAccount && acceptedSalaryAccount && acceptedIncomeTags
                && acceptedNoGovSupport && acceptedEmploymentSince &&
                acceptedEncashmentTags && acceptedSeizureTags && acceptedPAccountTags && acceptedChargebackTags && acceptedLoanAmount;
    }

    private boolean hasRequiredNonNullFields(LoanDemand loanDemand) {
        return nonNull(loanDemand.getPersonalDetails()) &&
                nonNull(loanDemand.getPersonalDetails().getBirthDate()) &&
                nonNull(loanDemand.getPersonalDetails().getFinance()) &&
                nonNull(loanDemand.getPersonalDetails().getFinance().getIncome()) &&
                nonNull(loanDemand.getPersonalDetails().getFinance().getIncome().getNetIncome()) &&
                nonNull(loanDemand.getDigitalAccountStatements()) &&
                nonNull(loanDemand.getDigitalAccountStatements().getIsJointlyManaged()) &&
                nonNull(loanDemand.getCustomDACData()) &&
                nonNull(loanDemand.getCustomDACData().getHasSalary()) &&
                nonNull(loanDemand.getCustomDACData().getHas3IncomeTags()) &&
                nonNull(loanDemand.getCustomDACData().getHasSalaryEachMonthLast3M()) &&
                nonNull(loanDemand.getCustomDACData().getNetIncomeHasGovSupport()) &&
                nonNull(loanDemand.getEmploymentDetails()) &&
                nonNull(loanDemand.getEmploymentDetails().getEmploymentSince()) &&
                nonNull(loanDemand.getCustomDACData().getCountEncashmentTag()) &&
                nonNull(loanDemand.getCustomDACData().getCountSeizureTag()) &&
                nonNull(loanDemand.getCustomDACData().getCountPAccountTag()) &&
                nonNull(loanDemand.getCustomDACData().getCountChargebackTag());
    }

    private boolean isAcceptedUserAge(final LoanDemand loanDemand) {
        int ageInYears = Period.between(loanDemand.getPersonalDetails().getBirthDate(), LocalDate.now()).getYears();
        final var isAcceptedUserAge = ageInYears >= propertiesConfig.getAcceptedApplicantMinAge() && ageInYears <= propertiesConfig.getAcceptedApplicantMaxAge();
        loanDemand.addPreCheck(Bank.CONSORS, PreCheckEnum.ACCEPTED_USER_AGE, isAcceptedUserAge);
        return isAcceptedUserAge;
    }

    private boolean isJointlyManagedAccount(final LoanDemand loanDemand) {
        final var isJointlyManaged = loanDemand.getDigitalAccountStatements().getIsJointlyManaged();
        loanDemand.addPreCheck(Bank.CONSORS, PreCheckEnum.ACCEPTED_NOT_JOINTLY_ACCOUNT, !isJointlyManaged);
        return !isJointlyManaged;
    }

    private boolean hasSalaryAccount(final LoanDemand loanDemand) {
        final var hasSalary = loanDemand.getCustomDACData().getHasSalary();
        loanDemand.addPreCheck(Bank.CONSORS, PreCheckEnum.ACCEPTED_HAS_SALARY, hasSalary);
        return hasSalary;
    }

    private boolean has3IncomeTags(final LoanDemand loanDemand) {
        final var hasIncomeTags = loanDemand.getCustomDACData().getHas3IncomeTags() || loanDemand.getCustomDACData().getHasSalaryEachMonthLast3M();
        loanDemand.addPreCheck(Bank.CONSORS, PreCheckEnum.ACCEPTED_3_INCOME_TAGS, hasIncomeTags);
        return hasIncomeTags;
    }

    private boolean netIncomeHasNoGovSupport(final LoanDemand loanDemand) {
        final var noGovSupport = loanDemand.getCustomDACData().getNetIncomeHasGovSupport();
        loanDemand.addPreCheck(Bank.CONSORS, PreCheckEnum.ACCEPTED_NO_GOV_SUPPORT, !noGovSupport);

        return !noGovSupport;
    }

    private boolean hasIncomeGreaterThanOrEqual(final LoanDemand loanDemand) {
        final var isAcceptedIncome = loanDemand.getPersonalDetails().getFinance().getIncome().getNetIncome().intValue() >= propertiesConfig.getAcceptedApplicantMinIncome();
        loanDemand.addPreCheck(Bank.CONSORS, PreCheckEnum.ACCEPTED_INCOME, isAcceptedIncome);

        return isAcceptedIncome;
    }

    private boolean acceptedEncashmentTags(final LoanDemand loanDemand) {
        final var hasPreventionTags = loanDemand.getCustomDACData().getCountEncashmentTag() > 0;
        loanDemand.addPreCheck(Bank.CONSORS, PreCheckEnum.ACCEPTED_TRANSACTIONS_ENCASHMENT, !hasPreventionTags);

        return !hasPreventionTags;
    }

    private boolean acceptedSeizureTags(final LoanDemand loanDemand) {
        final var hasPreventionTags = loanDemand.getCustomDACData().getCountSeizureTag() > 0;
        loanDemand.addPreCheck(Bank.CONSORS, PreCheckEnum.ACCEPTED_TRANSACTIONS_SEIZURE, !hasPreventionTags);

        return !hasPreventionTags;
    }

    private boolean acceptedPAccountTags(final LoanDemand loanDemand) {
        final var hasPreventionTags = loanDemand.getCustomDACData().getCountPAccountTag() > 0;
        loanDemand.addPreCheck(Bank.CONSORS, PreCheckEnum.ACCEPTED_TRANSACTIONS_P_ACCOUNT, !hasPreventionTags);

        return !hasPreventionTags;
    }


    private boolean acceptedChargebackTags(final LoanDemand loanDemand) {
        final var hasPreventionTags = loanDemand.getCustomDACData().getCountChargebackTag() > 0;
        loanDemand.addPreCheck(Bank.CONSORS, PreCheckEnum.ACCEPTED_TRANSACTIONS_CHARGEBACK, !hasPreventionTags);

        return !hasPreventionTags;
    }

    private boolean isAcceptedLoanAmount(final LoanDemand loanDemand) {
        final var isLoanAmountInRange = loanDemand.getLoanAsked() >= propertiesConfig.getAcceptedApplicantMinLoanAmount() &&
                loanDemand.getLoanAsked() <= propertiesConfig.getAcceptedApplicantMaxLoanAmount();
        loanDemand.addPreCheck(Bank.CONSORS, PreCheckEnum.ACCEPTED_LOAN_AMOUNT, isLoanAmountInRange);

        return isLoanAmountInRange;
    }

    private boolean isAcceptedEmploymentSinceDate(final LoanDemand loanDemand) {
        final var isAcceptedEmployment = ChronoUnit.MONTHS.between(loanDemand.getEmploymentDetails().getEmploymentSince(), LocalDate.now()) > propertiesConfig.getAcceptedApplicantMinProbationInMonths();
        loanDemand.addPreCheck(Bank.CONSORS, PreCheckEnum.ACCEPTED_EMPLOYED_SINCE, isAcceptedEmployment);

        return isAcceptedEmployment;
    }
}
