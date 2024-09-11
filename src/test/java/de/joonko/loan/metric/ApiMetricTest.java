package de.joonko.loan.metric;

import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;
import de.joonko.loan.metric.model.CounterMetricLabel;
import de.joonko.loan.metric.model.TagMetricLabel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ApiMetricTest {

    private ApiMetric apiMetric;

    private Metric metric;

    @BeforeEach
    void setUp() {
        metric = mock(Metric.class);
        apiMetric = new ApiMetric(metric);
    }

    @Test
    void incrementApiCounter() {
        // given
        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
        
        // when
        apiMetric.incrementStatusCounter(HttpStatus.CREATED, ApiComponent.SWK, ApiName.AUTHORIZATION);
        
        // then
        verify(metric).incrementCounter(eq(CounterMetricLabel.API_STATUS), captor.capture());
        Map<String, String> tags = captor.getValue();
        assertAll(
                () -> assertEquals("swk", tags.get(TagMetricLabel.API_COMPONENT.getName())),
                () -> assertEquals("2xx", tags.get(TagMetricLabel.HTTP_CODE.getName())),
                () -> assertEquals("authorization", tags.get(TagMetricLabel.API_NAME.getName()))
        );
    }

}
