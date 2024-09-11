package de.joonko.loan.partner.postbank;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.PreCheckEnum;

import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.function.Predicate;

import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostbankPrecheckFilter implements Predicate<LoanDemand> {

    private final PostbankPropertiesConfig propertiesConfig;

    @Override
    public boolean test(@NotNull LoanDemand loanDemand) {
        if (!hasRequiredNonNullFields(loanDemand)) {
            log.info("POSTBANK: Precheck failed for userId: {}, required non null fields", loanDemand.getUserUUID());
            return false;
        }

        boolean acceptedLoanAmount = isLoanAmountInRangeThanOrEqual(loanDemand);
        boolean acceptedEmploymentContract = isUnlimitedEmploymentContract(loanDemand);

        log.info("userId: {}, acceptedLoanAmount: {},acceptedEmploymentContract: {} "
                , loanDemand.getUserUUID(),
                acceptedLoanAmount, acceptedEmploymentContract);


        return acceptedLoanAmount && acceptedEmploymentContract;
    }

    private boolean isUnlimitedEmploymentContract(final LoanDemand loanDemand) {
        final var isUnlimitedContract = Objects.isNull(loanDemand.getEmploymentDetails().getProfessionEndDate());
        loanDemand.addPreCheck(Bank.POSTBANK, PreCheckEnum.ACCEPTED_UNLIMITED_CONTRACT, isUnlimitedContract);
        return isUnlimitedContract;
    }

    private boolean hasRequiredNonNullFields(final LoanDemand loanDemand) {
        return nonNull(loanDemand.getLoanAsked());
    }

    private boolean isLoanAmountInRangeThanOrEqual(final LoanDemand loanDemand) {
        final var isLoanAmountAccepted = loanDemand.getLoanAsked() >= propertiesConfig.getMinLoanAmount() && loanDemand.getLoanAsked() <= propertiesConfig.getMaxLoanAmount();
        loanDemand.addPreCheck(Bank.POSTBANK, PreCheckEnum.ACCEPTED_LOAN_AMOUNT, isLoanAmountAccepted);
        return isLoanAmountAccepted;
    }
}
