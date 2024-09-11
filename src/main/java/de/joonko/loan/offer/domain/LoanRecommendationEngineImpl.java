package de.joonko.loan.offer.domain;

import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.integrations.configuration.GetOffersConfigurations;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.math.RoundingMode.CEILING;
import static java.math.RoundingMode.HALF_UP;

@Slf4j
@Component
@ConditionalOnProperty(
        value = "offers.loanRecommendationsEnabled",
        havingValue = "true")
@RequiredArgsConstructor
public class LoanRecommendationEngineImpl implements LoanRecommendationEngine {

    private static final int LOAN_MULTIPLIER_VALUE = 500;

    private final GetOffersConfigurations offersConfigurations;

    @Override
    public Set<LoanDemandRequest> recommend(final LoanDemandRequest loanDemandRequest) {
        log.info("Getting recommended loan demands for userId: {}", loanDemandRequest.getUserUUID());
        if (loanDemandRequest.getLoanAsked().equals(offersConfigurations.getMinimalLoanAmount())) {
            return Set.of();
        }

        var recommendedAmount = getRecommendedAmount(loanDemandRequest);

        if (recommendedAmount < offersConfigurations.getMinimalLoanAmount()) {
            recommendedAmount = offersConfigurations.getMinimalLoanAmount();
        }

        final var recommendedLoanDemandRequest = SerializationUtils.clone(loanDemandRequest).toBuilder()
                ._id(null)
                .applicationId(null)
                .loanAsked(recommendedAmount)
                .build();

        return Set.of(recommendedLoanDemandRequest);

    }

    private Integer getRecommendedAmount(final LoanDemandRequest loanDemandRequest) {
        final BigDecimal disposableAmount = recommendedLoanAmountPerDisposableAmount(loanDemandRequest);
        final BigDecimal recommendedAmountPerPercent = recommendedLoanAmountPerUserRequestedAmountPercentage(loanDemandRequest.getLoanAsked());
        return makeLoanAmountMultiplier(disposableAmount.min(recommendedAmountPerPercent)).toBigInteger().intValue();
    }

    private BigDecimal recommendedLoanAmountPerDisposableAmount(final LoanDemandRequest loanDemandRequest) {
        return Optional.ofNullable(loanDemandRequest.getDisposableIncome())
                .orElse(BigDecimal.ZERO)
                .multiply(BigDecimal.valueOf(offersConfigurations.getRecommendedDisposableAmountMultiplier()));
    }

    private BigDecimal recommendedLoanAmountPerUserRequestedAmountPercentage(final Integer userAskedAmount) {
        return BigDecimal.valueOf(userAskedAmount)
                .multiply(BigDecimal.valueOf(offersConfigurations.getRecommendedLoanPercentage()))
                .divide(BigDecimal.valueOf(100), HALF_UP);
    }

    private BigDecimal makeLoanAmountMultiplier(final BigDecimal bigDecimal) {
        return bigDecimal
                .divide(BigDecimal.valueOf(LOAN_MULTIPLIER_VALUE), 0, CEILING)
                .multiply(BigDecimal.valueOf(LOAN_MULTIPLIER_VALUE));
    }
}
