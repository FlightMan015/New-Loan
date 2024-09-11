package de.joonko.loan.partner.aion;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.offer.api.model.FundingPurpose;
import de.joonko.loan.offer.domain.EmploymentType;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.PreCheckEnum;
import de.joonko.loan.util.BigDecimalUtil;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static de.joonko.loan.offer.api.model.FundingPurpose.BALANCING_CURRENT_ACCOUNT;
import static de.joonko.loan.offer.api.model.FundingPurpose.HOUSE_SHIFT;
import static de.joonko.loan.offer.api.model.FundingPurpose.LOAN_REPAYMENT;
import static de.joonko.loan.offer.api.model.FundingPurpose.REAL_ESTATE;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Slf4j
@RequiredArgsConstructor
@Service
public class AionPrecheckFilter implements Predicate<LoanDemand> {

    private final AionPropertiesConfig propertiesConfig;

    @Override
    public boolean test(@NotNull LoanDemand loanDemand) {
        if (Boolean.FALSE.equals(propertiesConfig.getEnabled())) {
            log.info("AION: is not enabled");
            return false;
        }
        if (!hasRequiredNonNullFields(loanDemand)) {
            log.info("AION: Precheck failed for userId: {}, required non null fields", loanDemand.getUserUUID());
            return false;
        }

        boolean acceptedUserAge = isAcceptedUserAge(loanDemand);
        boolean acceptedAverageIncome = has3MAverageNetIncomeGreaterThan(loanDemand);
        boolean acceptedLatestIncome = hasLastSalaryGreaterThan(loanDemand);
        boolean acceptedDeclaredDelaysInInstallments = hasNoDeclaredDelaysInInstallments(loanDemand);
        boolean acceptedEmploymentType = isValidEmploymentType(loanDemand);
        boolean acceptedEploymentSince = isAcceptedEmploymentSinceDate(loanDemand);
        boolean acceptedPurpose = isAcceptedLoanPurpose(loanDemand);
        boolean acceptedMaxAmount = isAcceptedMaxAmount(loanDemand);
        boolean acceptedBonimaScore = isAcceptedBonimaScore(loanDemand);

        log.info("userId: {}, acceptedUserAge: {}, acceptedAverageIncome: {}, acceptedLatestIncome: {}, acceptedEmploymentType: {}, acceptedEploymentSince: {}, acceptedDeclaredDelaysInInstallments: {}, acceptedPurpose: {}, acceptedMaxAmount: {}, acceptedBonimaScore: {}"
                , loanDemand.getUserUUID(),
                acceptedUserAge, acceptedAverageIncome, acceptedLatestIncome,
                acceptedEmploymentType, acceptedEploymentSince,
                acceptedDeclaredDelaysInInstallments,
                acceptedPurpose, acceptedMaxAmount, acceptedBonimaScore);


        return acceptedUserAge && acceptedAverageIncome && acceptedLatestIncome && acceptedEmploymentType &&
                acceptedEploymentSince && acceptedDeclaredDelaysInInstallments && acceptedPurpose && acceptedMaxAmount && acceptedBonimaScore;
    }

    private boolean hasRequiredNonNullFields(final LoanDemand loanDemand) {
        return nonNull(loanDemand.getPersonalDetails()) &&
                nonNull(loanDemand.getPersonalDetails().getBirthDate()) &&
                nonNull(loanDemand.getDigitalAccountStatements()) &&
                nonNull(loanDemand.getCustomDACData()) &&
                nonNull(loanDemand.getCustomDACData().getTotalIncomeInLast90Days()) &&
                nonNull(loanDemand.getPersonalDetails().getFinance()) &&
                nonNull(loanDemand.getPersonalDetails().getFinance().getIncome()) &&
                nonNull(loanDemand.getPersonalDetails().getFinance().getIncome().getNetIncome()) &&
                nonNull(loanDemand.getEmploymentDetails()) &&
                nonNull(loanDemand.getEmploymentDetails().getEmploymentType()) &&
                nonNull(loanDemand.getEmploymentDetails().getEmploymentSince()) &&
                nonNull(loanDemand.getCreditDetails());
    }

    private boolean isAcceptedUserAge(final LoanDemand loanDemand) {
        int ageInYears = Period.between(loanDemand.getPersonalDetails().getBirthDate(), LocalDate.now()).getYears();
        final var isAcceptedUserAge = ageInYears >= propertiesConfig.getAcceptedApplicantMinAge() && ageInYears <= propertiesConfig.getAcceptedApplicantMaxAge();
        loanDemand.addPreCheck(Bank.AION, PreCheckEnum.ACCEPTED_USER_AGE, isAcceptedUserAge);
        return isAcceptedUserAge;
    }

    private boolean isValidEmploymentType(final LoanDemand loanDemand) {
        final var isValidEmployed = EmploymentType.REGULAR_EMPLOYED == loanDemand.getEmploymentDetails().getEmploymentType();
        loanDemand.addPreCheck(Bank.AION, PreCheckEnum.ACCEPTED_EMPLOYMENT_TYPE, isValidEmployed);
        return isValidEmployed;
    }

    private boolean has3MAverageNetIncomeGreaterThan(final LoanDemand loanDemand) {
        final var isAcceptedIncome = BigDecimalUtil.average(List.of(
                        ofNullable(loanDemand.getCustomDACData().getSumIncomes1MAgo()).orElse(BigDecimal.ZERO),
                        ofNullable(loanDemand.getCustomDACData().getSumIncomes2MAgo()).orElse(BigDecimal.ZERO),
                        ofNullable(loanDemand.getCustomDACData().getSumIncomes3MAgo()).orElse(BigDecimal.ZERO)))
                .orElse(BigDecimal.ZERO)
                .compareTo(propertiesConfig.getMinAverage3MSalary()) > 0;
        loanDemand.addPreCheck(Bank.AION, PreCheckEnum.ACCEPTED_3M_AVERAGE_INCOME, isAcceptedIncome);

        return isAcceptedIncome;
    }

    private boolean hasLastSalaryGreaterThan(final LoanDemand loanDemand) {
        final var isAcceptedIncome = loanDemand.getPersonalDetails().getFinance().getIncome().getNetIncome()
                .compareTo(propertiesConfig.getMinLastSalary()) > 0;
        loanDemand.addPreCheck(Bank.AION, PreCheckEnum.ACCEPTED_LATEST_SALARY, isAcceptedIncome);

        return isAcceptedIncome;
    }

    private boolean isAcceptedEmploymentSinceDate(final LoanDemand loanDemand) {
        final var isAcceptedEmployment = ChronoUnit.MONTHS.between(loanDemand.getEmploymentDetails().getEmploymentSince(), LocalDate.now()) > propertiesConfig.getMinEmploymentMonths();
        loanDemand.addPreCheck(Bank.AION, PreCheckEnum.ACCEPTED_EMPLOYED_SINCE, isAcceptedEmployment);

        return isAcceptedEmployment;
    }

    private boolean hasNoDeclaredDelaysInInstallments(final LoanDemand loanDemand) {
        final boolean isAcceptedDelayInInstallments = ofNullable(loanDemand.getCreditDetails().getIsCurrentDelayInInstallmentsDeclared()).map(delay -> !delay).orElse(true);
        loanDemand.addPreCheck(Bank.AION, PreCheckEnum.ACCEPTED_DELAY_IN_INSTALLMENTS, isAcceptedDelayInInstallments);

        return isAcceptedDelayInInstallments;
    }

    private boolean isAcceptedMaxAmount(LoanDemand loanDemand) {
        final var isLoanAmountInRange = loanDemand.getLoanAsked() <= propertiesConfig.getAcceptedApplicantMaxLoanAmount();
        loanDemand.addPreCheck(Bank.AION, PreCheckEnum.ACCEPTED_LOAN_AMOUNT, isLoanAmountInRange);

        return isLoanAmountInRange;
    }

    private boolean isAcceptedBonimaScore(LoanDemand loanDemand) {
        final boolean isAcceptedBonimaScore = ofNullable(loanDemand.getCreditDetails().getBonimaScore()).map(score -> score < propertiesConfig.getAcceptedBonimaScore()).orElse(true);
        loanDemand.addPreCheck(Bank.AION, PreCheckEnum.ACCEPTED_BONIMA_SCORE, isAcceptedBonimaScore);

        return isAcceptedBonimaScore;
    }

    private boolean isAcceptedLoanPurpose(final LoanDemand loanDemand) {
        final boolean isAcceptedLoanPurpose = FundingPurpose.fromValue(loanDemand.getFundingPurpose())
                .map(purpose -> {
                    return !rejectedFundingPurposes().contains(purpose);
                }).orElse(false);
        loanDemand.addPreCheck(Bank.AION, PreCheckEnum.ACCEPTED_LOAN_PURPOSE, isAcceptedLoanPurpose);

        return isAcceptedLoanPurpose;
    }

    private Set<FundingPurpose> rejectedFundingPurposes() {
        return Set.of(LOAN_REPAYMENT, BALANCING_CURRENT_ACCOUNT, HOUSE_SHIFT, REAL_ESTATE);
    }
}
