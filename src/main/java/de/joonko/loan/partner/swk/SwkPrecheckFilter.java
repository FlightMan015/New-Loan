package de.joonko.loan.partner.swk;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.config.SwkConfig;
import de.joonko.loan.offer.domain.EmploymentType;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.PreCheckEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.function.Predicate;

import static java.util.Objects.nonNull;

@Slf4j
@AllArgsConstructor
@Service
public class SwkPrecheckFilter implements Predicate<LoanDemand> {

    private final SwkConfig propertiesConfig;

    @Override
    public boolean test(@NotNull LoanDemand loanDemand) {
        if (!hasRequiredNonNullFields(loanDemand)) {
            log.info("userId: {}, precheck failed, required non null fields", loanDemand.getUserUUID());
            return false;
        }

        boolean acceptedUserAge = isAcceptedUserAge(loanDemand);
        boolean acceptedIncome = hasIncomeGreaterThanOrEqual(loanDemand);
        boolean acceptedEncashmentTags = acceptedEncashmentTags(loanDemand);
        boolean acceptedSeizureTags = acceptedSeizureTags(loanDemand);
        boolean acceptedPAccountTags = acceptedPAccountTags(loanDemand);
        boolean acceptedChargebackTags = acceptedChargebackTags(loanDemand);
        boolean acceptedEmploymentSince = isAcceptedEmploymentSinceDate(loanDemand);
        boolean acceptedGambling = isAcceptedGamblingAmount(loanDemand);
        boolean acceptedEmploymentType = isValidEmploymentType(loanDemand);
        boolean acceptedCashWithdrawal = isAcceptedCashWithdrawalRatio(loanDemand);
        boolean acceptedLoanAmount = isLoanAmountGreaterThanOrEqual(loanDemand);

        log.info("userId: {}, acceptedUserAge: {}, acceptedIncome: {}," +
                        "acceptedEncashmentTags: {}, acceptedSeizureTags: {}, acceptedPAccountTags: {}, acceptedChargebackTags: {}," +
                        "acceptedEmploymentSince: {}, acceptedGambling: {}, acceptedEmploymentType: {}, acceptedCashWithdrawal: {}, acceptedLoanAmount: {}",
                loanDemand.getUserUUID(), acceptedUserAge, acceptedIncome,
                acceptedEncashmentTags, acceptedSeizureTags, acceptedPAccountTags, acceptedChargebackTags,
                acceptedEmploymentSince, acceptedGambling, acceptedEmploymentType, acceptedCashWithdrawal, acceptedLoanAmount);

        return acceptedUserAge && acceptedIncome && acceptedEmploymentSince &&
                acceptedEncashmentTags && acceptedSeizureTags && acceptedPAccountTags && acceptedChargebackTags
                && acceptedGambling && acceptedEmploymentType && acceptedCashWithdrawal && acceptedLoanAmount;
    }

    private boolean hasRequiredNonNullFields(LoanDemand loanDemand) {
        return nonNull(loanDemand.getPersonalDetails()) &&
                nonNull(loanDemand.getPersonalDetails().getBirthDate()) &&
                nonNull(loanDemand.getPersonalDetails().getFinance()) &&
                nonNull(loanDemand.getPersonalDetails().getFinance().getIncome()) &&
                nonNull(loanDemand.getPersonalDetails().getFinance().getIncome().getNetIncome()) &&
                nonNull(loanDemand.getCustomDACData()) &&
                nonNull(loanDemand.getCustomDACData().getGamblingAmountInLast90Days()) &&
                nonNull(loanDemand.getCustomDACData().getCashWithdrawalsInLast90Days()) &&
                nonNull(loanDemand.getCustomDACData().getTotalIncomeInLast90Days()) &&
                nonNull(loanDemand.getEmploymentDetails()) &&
                nonNull(loanDemand.getEmploymentDetails().getEmploymentSince()) &&
                nonNull(loanDemand.getLoanAsked()) &&
                nonNull(loanDemand.getCustomDACData().getCountEncashmentTag()) &&
                nonNull(loanDemand.getCustomDACData().getCountSeizureTag()) &&
                nonNull(loanDemand.getCustomDACData().getCountPAccountTag()) &&
                nonNull(loanDemand.getCustomDACData().getCountChargebackTag());
    }

    private boolean isAcceptedUserAge(final LoanDemand loanDemand) {
        int ageInYears = Period.between(loanDemand.getPersonalDetails().getBirthDate(), LocalDate.now()).getYears();
        final var isAcceptedUserAge = ageInYears >= propertiesConfig.getAcceptedApplicantMinAge() && ageInYears <= propertiesConfig.getAcceptedApplicantMaxAge();
        loanDemand.addPreCheck(Bank.SWK_BANK, PreCheckEnum.ACCEPTED_USER_AGE, isAcceptedUserAge);
        return isAcceptedUserAge;
    }

    private boolean acceptedEncashmentTags(final LoanDemand loanDemand) {
        final var hasPreventionTags = loanDemand.getCustomDACData().getCountEncashmentTag() > 1;
        loanDemand.addPreCheck(Bank.SWK_BANK, PreCheckEnum.ACCEPTED_TRANSACTIONS_ENCASHMENT, !hasPreventionTags);

        return !hasPreventionTags;
    }

    private boolean acceptedSeizureTags(final LoanDemand loanDemand) {
        final var hasPreventionTags = loanDemand.getCustomDACData().getCountSeizureTag() > 1;
        loanDemand.addPreCheck(Bank.SWK_BANK, PreCheckEnum.ACCEPTED_TRANSACTIONS_SEIZURE, !hasPreventionTags);

        return !hasPreventionTags;
    }

    private boolean acceptedPAccountTags(final LoanDemand loanDemand) {
        final var hasPreventionTags = loanDemand.getCustomDACData().getCountPAccountTag() > 1;
        loanDemand.addPreCheck(Bank.SWK_BANK, PreCheckEnum.ACCEPTED_TRANSACTIONS_P_ACCOUNT, !hasPreventionTags);

        return !hasPreventionTags;
    }

    private boolean acceptedChargebackTags(final LoanDemand loanDemand) {
        final var hasPreventionTags = loanDemand.getCustomDACData().getCountChargebackTag() > 2;
        loanDemand.addPreCheck(Bank.SWK_BANK, PreCheckEnum.ACCEPTED_TRANSACTIONS_CHARGEBACK, !hasPreventionTags);

        return !hasPreventionTags;
    }

    private boolean hasIncomeGreaterThanOrEqual(final LoanDemand loanDemand) {
        final var incomeAccepted = loanDemand.getPersonalDetails().getFinance().getIncome().getNetIncome().intValue() >= propertiesConfig.getAcceptedApplicantMinIncome();
        loanDemand.addPreCheck(Bank.SWK_BANK, PreCheckEnum.ACCEPTED_INCOME, incomeAccepted);
        return incomeAccepted;
    }

    private boolean isAcceptedEmploymentSinceDate(final LoanDemand loanDemand) {
        final var isEmploymentAccepted = ChronoUnit.MONTHS.between(loanDemand.getEmploymentDetails().getEmploymentSince(), LocalDate.now()) > propertiesConfig.getAcceptedApplicantMinProbationInMonths();
        loanDemand.addPreCheck(Bank.SWK_BANK, PreCheckEnum.ACCEPTED_EMPLOYED_SINCE, isEmploymentAccepted);
        return isEmploymentAccepted;
    }

    private boolean isAcceptedGamblingAmount(final LoanDemand loanDemand) {
        final var isGamblingAccepted = loanDemand.getCustomDACData().getGamblingAmountInLast90Days() <= propertiesConfig.getAcceptedApplicantMaxGamblingAmountInLast90Days();
        loanDemand.addPreCheck(Bank.SWK_BANK, PreCheckEnum.ACCEPTED_GAMBLING, isGamblingAccepted);
        return isGamblingAccepted;
    }

    private boolean isValidEmploymentType(final LoanDemand loanDemand) {
        final var isValidEmployed = EmploymentType.REGULAR_EMPLOYED == loanDemand.getEmploymentDetails().getEmploymentType();
        loanDemand.addPreCheck(Bank.SWK_BANK, PreCheckEnum.ACCEPTED_EMPLOYMENT_TYPE, isValidEmployed);
        return isValidEmployed;
    }

    private boolean isAcceptedCashWithdrawalRatio(final LoanDemand loanDemand) {
        double ratio = (loanDemand.getCustomDACData().getCashWithdrawalsInLast90Days() / loanDemand.getCustomDACData().getTotalIncomeInLast90Days());

        final var isCashWithdrawalAcceptable = propertiesConfig.getAcceptedCashWithdrawalsOutOfTotalIncomeInLast90DaysRatio() == 100 ||
                ratio <= propertiesConfig.getAcceptedCashWithdrawalsOutOfTotalIncomeInLast90DaysRatio();
        loanDemand.addPreCheck(Bank.SWK_BANK, PreCheckEnum.ACCEPTED_CASH_WITHDRAWAL, isCashWithdrawalAcceptable);
        return isCashWithdrawalAcceptable;
    }

    private boolean isLoanAmountGreaterThanOrEqual(final LoanDemand loanDemand) {
        final var isLoanAmountAccepted = loanDemand.getLoanAsked() >= propertiesConfig.getAcceptedApplicantMinLoanAmount();
        loanDemand.addPreCheck(Bank.SWK_BANK, PreCheckEnum.ACCEPTED_LOAN_AMOUNT, isLoanAmountAccepted);
        return isLoanAmountAccepted;
    }
}
