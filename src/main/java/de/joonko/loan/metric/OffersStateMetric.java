package de.joonko.loan.metric;

import de.joonko.loan.metric.model.TagMetricLabel;
import de.joonko.loan.metric.model.TimerMetricLabel;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class OffersStateMetric {

    private final Metric metric;

    public void addPersonalInfoTimer(String userId, OffsetDateTime startDateTime) {
        Map<String, String> tags = Map.of(TagMetricLabel.USER_ID.getName(), userId);
        metric.addTimer(TimerMetricLabel.PERSONAL_INFO, startDateTime, tags);
    }

    public void addAdditionalInfoTimer(String userId, OffsetDateTime startDateTime) {
        Map<String, String> tags = Map.of(TagMetricLabel.USER_ID.getName(), userId);
        metric.addTimer(TimerMetricLabel.ADDITIONAL_INFO, startDateTime, tags);
    }

    public void addTransactionalDataFromDSTimer(String userId, OffsetDateTime startDateTime) {
        Map<String, String> tags = Map.of(TagMetricLabel.USER_ID.getName(), userId);
        metric.addTimer(TimerMetricLabel.TRANSACTIONAL_DATA_DS, startDateTime, tags);
    }

    public void addTransactionalDataFromDACTimer(String userId, OffsetDateTime startDateTime) {
        Map<String, String> tags = Map.of(TagMetricLabel.USER_ID.getName(), userId);
        metric.addTimer(TimerMetricLabel.TRANSACTIONAL_DATA_DAC, startDateTime, tags);
    }

    public void addOffersStateTimer(String userId, OffsetDateTime startDateTime) {
        Map<String, String> tags = Map.of(
                TagMetricLabel.USER_ID.getName(), userId);
        metric.addTimer(TimerMetricLabel.OFFER_STATE, startDateTime, tags);
    }
}
