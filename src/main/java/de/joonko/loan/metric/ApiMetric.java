package de.joonko.loan.metric;

import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;
import de.joonko.loan.metric.model.CounterMetricLabel;
import de.joonko.loan.metric.model.TagMetricLabel;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@AllArgsConstructor
@Component
public class ApiMetric {

    private final Metric metric;

    public void incrementStatusCounter(HttpStatus httpStatus, ApiComponent apiComponent, ApiName apiName) {
        Map<String, String> tags = Map.of(
                TagMetricLabel.API_COMPONENT.getName(), apiComponent.getName(),
                TagMetricLabel.HTTP_CODE.getName(), getHttpStatusGroup(httpStatus),
                TagMetricLabel.API_NAME.getName(), apiName.getName()
        );
        metric.incrementCounter(CounterMetricLabel.API_STATUS, tags);
    }

    private String getHttpStatusGroup(HttpStatus httpStatus) {
        String group;

        if (httpStatus.is1xxInformational()) {
            group = "1xx";
        } else if (httpStatus.is2xxSuccessful()) {
            group = "2xx";
        } else if (httpStatus.is3xxRedirection()) {
            group = "3xx";
        } else if (httpStatus.is4xxClientError()) {
            group = "4xx";
        } else {
            group = "5xx";
        }
         return group;
    }
}
