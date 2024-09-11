package de.joonko.loan.metric;

import de.joonko.loan.metric.model.CounterMetricLabel;
import de.joonko.loan.metric.model.TagMetricLabel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OfferStatusMetricTest {

    private OfferStatusMetric offerStatusMetric;

    private Metric metric;

    @BeforeEach
    void setUp() {
        metric = mock(Metric.class);
        offerStatusMetric = new OfferStatusMetric(metric);
    }

    @Test
    void incrementOfferStatusCounter() {
        // given
        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);

        // when
        offerStatusMetric.incrementOfferStatusCounter("PAID_OUT", "SANTANDER");

        // then
        verify(metric).incrementCounter(eq(CounterMetricLabel.OFFER_STATUS), captor.capture());
        Map<String, String> tags = captor.getValue();
        assertAll(
                () -> assertEquals("SANTANDER", tags.get(TagMetricLabel.OFFER_PROVIDER.getName())),
                () -> assertEquals("PAID_OUT", tags.get(TagMetricLabel.STATUS.getName()))
        );
    }
}
