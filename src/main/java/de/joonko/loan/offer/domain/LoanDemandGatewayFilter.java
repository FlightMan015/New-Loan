package de.joonko.loan.offer.domain;

import de.joonko.loan.metric.LoanDemandMetric;
import de.joonko.loan.offer.api.LoanProvidersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoanDemandGatewayFilter {

    private final LoanDemandMetric metric;
    private final LoanProvidersService loanProvidersService;

    public Set<LoanDemandGateway> filterValidGatewaysForLoanDemand(final LoanDemand loanDemand) {
        return loanProvidersService.getGateways()
                .stream()
                .filter(isNotRecommended(loanDemand).or(isLoanProviderEnabledForRecommended()))
                .filter(isValid(loanDemand))
                .collect(toSet());
    }

    private Predicate<LoanDemandGateway> isNotRecommended(LoanDemand loanDemand) {
        return gateway -> !loanDemand.isRecommended();
    }

    private Predicate<LoanDemandGateway> isLoanProviderEnabledForRecommended() {
        return gateway -> loanProvidersService.isRecommendedEnabled(gateway.getLoanProvider());
    }

    private Predicate<LoanDemandGateway> isValid(LoanDemand loanDemand) {
        return gateway -> {
            try {
                boolean validLoanDemand = !gateway.filterGateway(loanDemand);

                metric.incrementPrecheckCounter(validLoanDemand, gateway.getLoanProvider());
                if (!validLoanDemand) {
                    log.warn("userId: {}, applicationId: {} has been filtered out from {}", loanDemand.getUserUUID(), loanDemand.getLoanApplicationId(), gateway.getLoanProvider());
                }
                return validLoanDemand;
            } catch (Exception e) {
                log.error("Error occurred while filtering gateway for userId: {} from {}", loanDemand.getUserUUID(), gateway.getLoanProvider(), e);
                return false;
            }
        };
    }
}
