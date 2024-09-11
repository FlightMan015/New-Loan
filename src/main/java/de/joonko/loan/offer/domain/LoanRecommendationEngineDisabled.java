package de.joonko.loan.offer.domain;

import de.joonko.loan.offer.api.LoanDemandRequest;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        value = "offers.loanRecommendationsEnabled",
        havingValue = "false",
        matchIfMissing = true)
@Slf4j
@NoArgsConstructor
public class LoanRecommendationEngineDisabled implements LoanRecommendationEngine {

    @Override
    public Set<LoanDemandRequest> recommend(LoanDemandRequest loanDemandRequest) {
        log.info("Recommendations are disabled globally");
        return Set.of();
    }
}
