package de.joonko.loan.partner.santander;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.config.SantanderConfig;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.offer.domain.PreCheckEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Predicate;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Slf4j
@AllArgsConstructor
@Service
public class SantanderPrecheckFilter implements Predicate<LoanDemand> {

    private final SantanderConfig propertiesConfig;

    @Override
    public boolean test(@NotNull LoanDemand loanDemand) {
        if (!hasRequiredNonNullFields(loanDemand)) {
            log.info("userId: {}, precheck failed, required non null fields", loanDemand.getUserUUID());
            return false;
        }

        boolean acceptedEmploymentSince = isAcceptedEmploymentSinceDate(loanDemand);
        boolean acceptedNoGovSupport = netIncomeHasNoGovSupport(loanDemand);
        boolean acceptedIncome = hasIncomeGreaterThanOrEqual(loanDemand);
        boolean acceptedIncomeTags = has3IncomeTags(loanDemand);
        boolean acceptedLoanAmount = isLoanAmountWithinLimit(loanDemand);

        log.info("userId: {}, acceptedEmploymentSince: {}, acceptedNoGovSupport: {}, acceptedIncome: {}, acceptedIncomeTags: {}," +
                        "acceptedLoanAmount: {}", loanDemand.getUserUUID(), acceptedEmploymentSince, acceptedNoGovSupport,
                acceptedIncome, acceptedIncomeTags, acceptedLoanAmount);

        return acceptedEmploymentSince && acceptedNoGovSupport && acceptedIncome && acceptedIncomeTags && acceptedLoanAmount;
    }

    public boolean doesContractEndBeforeRepayment(Date professionEndDate, LoanDuration loanDuration, String applicationId) {
        boolean isBeforeRepayment = ofNullable(professionEndDate)
                .map(d -> LocalDate.ofInstant(professionEndDate.toInstant(), ZoneId.systemDefault()))
                .map(d -> d.isBefore(LocalDate.now().plusMonths(loanDuration.getValue())))
                .orElse(false);

        log.info("applicationId: {}, loanDuration: {}, contractEndsBeforeRepayment: {}", applicationId, loanDuration, isBeforeRepayment);

        return isBeforeRepayment;
    }

    private boolean hasRequiredNonNullFields(LoanDemand loanDemand) {
        return nonNull(loanDemand.getEmploymentDetails()) &&
                nonNull(loanDemand.getEmploymentDetails().getEmploymentSince()) &&
                nonNull(loanDemand.getCustomDACData()) &&
                nonNull(loanDemand.getCustomDACData().getHas3IncomeTags()) &&
                nonNull(loanDemand.getCustomDACData().getHasSalaryEachMonthLast3M()) &&
                nonNull(loanDemand.getCustomDACData().getNetIncomeHasGovSupport()) &&
                nonNull(loanDemand.getPersonalDetails()) &&
                nonNull(loanDemand.getPersonalDetails().getFinance()) &&
                nonNull(loanDemand.getPersonalDetails().getFinance().getIncome()) &&
                nonNull(loanDemand.getPersonalDetails().getFinance().getIncome().getNetIncome()) &&
                nonNull(loanDemand.getLoanAsked());
    }

    private boolean isAcceptedEmploymentSinceDate(final LoanDemand loanDemand) {
        final var isEmploymentAccepted = ChronoUnit.MONTHS.between(loanDemand.getEmploymentDetails().getEmploymentSince(), LocalDate.now()) >= propertiesConfig.getAcceptedApplicantMinProbationInMonths();
        loanDemand.addPreCheck(Bank.SANTANDER, PreCheckEnum.ACCEPTED_EMPLOYED_SINCE, isEmploymentAccepted);

        return isEmploymentAccepted;
    }

    private boolean netIncomeHasNoGovSupport(final LoanDemand loanDemand) {
        final var noGovSupport = loanDemand.getCustomDACData().getNetIncomeHasGovSupport();
        loanDemand.addPreCheck(Bank.SANTANDER, PreCheckEnum.ACCEPTED_NO_GOV_SUPPORT, !noGovSupport);

        return !noGovSupport;
    }

    private boolean hasIncomeGreaterThanOrEqual(final LoanDemand loanDemand) {
        final var isIncomeAccepted = loanDemand.getPersonalDetails().getFinance().getIncome().getNetIncome().intValue() >= propertiesConfig.getAcceptedApplicantMinIncome();
        loanDemand.addPreCheck(Bank.SANTANDER, PreCheckEnum.ACCEPTED_INCOME, isIncomeAccepted);

        return isIncomeAccepted;
    }

    private boolean has3IncomeTags(final LoanDemand loanDemand) {
        final var hasIncomeTags = loanDemand.getCustomDACData().getHas3IncomeTags() || loanDemand.getCustomDACData().getHasSalaryEachMonthLast3M();
        loanDemand.addPreCheck(Bank.SANTANDER, PreCheckEnum.ACCEPTED_3_INCOME_TAGS, hasIncomeTags);

        return hasIncomeTags;
    }

    private boolean isLoanAmountWithinLimit(final LoanDemand loanDemand) {
        final var isLoanAmountInRange = loanDemand.getLoanAsked() >= propertiesConfig.getAcceptedApplicantMinLoanAmount() &&
                loanDemand.getLoanAsked() <= propertiesConfig.getAcceptedApplicantMaxLoanAmount();
        loanDemand.addPreCheck(Bank.SANTANDER, PreCheckEnum.ACCEPTED_LOAN_AMOUNT, isLoanAmountInRange);

        return isLoanAmountInRange;
    }
}
