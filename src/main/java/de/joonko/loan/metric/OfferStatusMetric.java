package de.joonko.loan.metric;

import de.joonko.loan.metric.model.CounterMetricLabel;
import de.joonko.loan.metric.model.TagMetricLabel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@AllArgsConstructor
@Component
public class OfferStatusMetric {

    private final Metric metric;

    public void incrementOfferStatusCounter(String status, String loanProvider) {
        Map<String, String> tags = Map.of(
                TagMetricLabel.STATUS.getName(), status,
                TagMetricLabel.OFFER_PROVIDER.getName(), loanProvider
        );
        metric.incrementCounter(CounterMetricLabel.OFFER_STATUS, tags);
    }
}
