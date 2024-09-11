package de.joonko.loan.metric;

import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.metric.model.ApiName;
import de.joonko.loan.metric.model.CounterMetricLabel;
import de.joonko.loan.metric.model.Process;
import de.joonko.loan.metric.model.TagMetricLabel;
import de.joonko.loan.metric.model.TimerMetricLabel;
import de.joonko.loan.offer.api.LoanProvidersService;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;


@AllArgsConstructor
@Component
public class LoanDemandMetric {

    private final Metric metric;
    private final LoanProvidersService loanProvidersService;

    public void addTimer(@NotNull LoanProvider loanProvider, @NotNull OffsetDateTime startDateTime, Process process) {
        Map<String, String> tags = Map.of(
                TagMetricLabel.OFFER_PROVIDER.getName(), loanProvider.getName(),
                TagMetricLabel.PROCESS.getName(), process.getName()
        );
        metric.addTimer(TimerMetricLabel.LOAN_DEMAND, startDateTime, tags);
    }

    public void incrementPrecheckCounter(boolean isValid, @NotNull LoanProvider loanProvider) {
        String status = isValid ? "success" : "failure";

        Map<String, String> tags = Map.of(
                TagMetricLabel.STATUS.getName(), status,
                TagMetricLabel.OFFER_PROVIDER.getName(), loanProvider.getName()
        );
        metric.incrementCounter(CounterMetricLabel.PRE_CHECK, tags);
    }

    public void incrementCounterForEachLoanProvider(String applicationId, boolean isRecommended) {
        if (isRecommended) {
            incrementOfferDemandRecommendedCounterForEachLoanProvider(applicationId);
        } else {
            incrementOfferDemandCounterForEachLoanProvider(applicationId);
        }
    }

    private void incrementOfferDemandCounterForEachLoanProvider(String applicationId) {
        List<String> loanProviders = loanProvidersService.getLoanOffersProviders(applicationId);

        loanProvidersService.getActiveLoanProviders()
                .doOnNext(active -> {
                    boolean hasOffers = loanProviders.stream().anyMatch(o -> o.equals(active));
                    incrementOfferCounter(hasOffers, active, CounterMetricLabel.OFFER_DEMAND);
                }).subscribe();
    }

    private void incrementOfferDemandRecommendedCounterForEachLoanProvider(String applicationId) {
        List<String> loanProviders = loanProvidersService.getLoanOffersProviders(applicationId);

        loanProvidersService.getActiveLoanProviders()
                .filter(active -> loanProvidersService.getEnabledRecommendedLoanProviders().contains(active))
                .doOnNext(activeRecommended -> {
                    boolean hasOffers = loanProviders.stream().anyMatch(o -> o.equals(activeRecommended));
                    incrementOfferCounter(hasOffers, activeRecommended, CounterMetricLabel.OFFER_DEMAND_RECOMMENDED);
                }).subscribe();
    }

    private void incrementOfferCounter(boolean hasOffers, String activeLoanProvider, CounterMetricLabel metricLabel) {
        String status = hasOffers ? "success" : "failure";

        Map<String, String> tags = Map.of(
                TagMetricLabel.STATUS.getName(), status,
                TagMetricLabel.OFFER_PROVIDER.getName(), activeLoanProvider
        );
        metric.incrementCounter(metricLabel, tags);
    }
}
