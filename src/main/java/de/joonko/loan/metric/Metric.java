package de.joonko.loan.metric;

import de.joonko.loan.metric.model.CounterMetricLabel;
import de.joonko.loan.metric.model.TimerMetricLabel;

import java.time.OffsetDateTime;
import java.util.Map;

public interface Metric {
    void addTimer(TimerMetricLabel timerMetricLabel, OffsetDateTime startDateTime, Map<String, String> tags);

    void incrementCounter(CounterMetricLabel counterMetricLabel, Map<String, String> tags);
}
