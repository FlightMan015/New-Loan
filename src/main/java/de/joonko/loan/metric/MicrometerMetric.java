package de.joonko.loan.metric;

import de.joonko.loan.metric.model.CounterMetricLabel;
import de.joonko.loan.metric.model.TimerMetricLabel;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Component
public class MicrometerMetric implements Metric {

    @Override
    public void addTimer(TimerMetricLabel timerMetricLabel, OffsetDateTime startDateTime, Map<String, String> tags) {
        if (startDateTime == null) {
            return;
        }

        Mono.just(tags)
                .map(this::toTagList)
                .map(t -> Metrics.timer(timerMetricLabel.getName(), t))
                .doOnNext(timer -> timer.record(Duration.between(startDateTime, OffsetDateTime.now())))
                .subscribe();
    }

    @Override
    public void incrementCounter(CounterMetricLabel counterMetricLabel, Map<String, String> tags) {
        Mono.just(tags)
                .map(this::toTagList)
                .map(t -> Metrics.counter(counterMetricLabel.getName(), t))
                .doOnNext(Counter::increment)
                .subscribe();
    }

    private List<Tag> toTagList(Map<String, String> tags) {
        return tags.entrySet().stream()
                .map(entry -> Tag.of(entry.getKey(), entry.getValue()))
                .collect(toList());
    }
}
