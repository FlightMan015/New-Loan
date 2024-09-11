package de.joonko.loan.metric;

import de.joonko.loan.metric.model.TagMetricLabel;
import de.joonko.loan.metric.model.TimerMetricLabel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class OffersStateMetricTest {

    private OffersStateMetric offersStateMetric;

    private Metric metric;

    private static final String USER_ID = "2f20a660-f0f2-4ca5-9fe6-b24b52cd1070";
    private static final String APPLICATION_ID = "5f561054e3fa3d3d1a7ef3f4";

    @BeforeEach
    void setUp() {
        metric = mock(Metric.class);
        offersStateMetric = new OffersStateMetric(metric);
    }

    @Test
    void addPersonalInfoTimer() {
        // given
        OffsetDateTime startDateTime = OffsetDateTime.now().minusMinutes(1);
        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);

        // when
        offersStateMetric.addPersonalInfoTimer(USER_ID, startDateTime);

        // then
        verify(metric).addTimer(eq(TimerMetricLabel.PERSONAL_INFO), eq(startDateTime), captor.capture());
        Map<String, String> tags = captor.getValue();
        assertEquals(USER_ID, tags.get(TagMetricLabel.USER_ID.getName()));
    }

    @Test
    void addAdditionalInfoTimer() {
        OffsetDateTime startDateTime = OffsetDateTime.now().minusMinutes(1);
        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);

        // when
        offersStateMetric.addAdditionalInfoTimer(USER_ID, startDateTime);

        // then
        verify(metric).addTimer(eq(TimerMetricLabel.ADDITIONAL_INFO), eq(startDateTime), captor.capture());
        Map<String, String> tags = captor.getValue();
        assertEquals(USER_ID, tags.get(TagMetricLabel.USER_ID.getName()));
    }

    @Test
    void addTransactionalDataFromDSTimer() {
        OffsetDateTime startDateTime = OffsetDateTime.now().minusMinutes(1);
        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);

        // when
        offersStateMetric.addTransactionalDataFromDSTimer(USER_ID, startDateTime);

        // then
        verify(metric).addTimer(eq(TimerMetricLabel.TRANSACTIONAL_DATA_DS), eq(startDateTime), captor.capture());
        Map<String, String> tags = captor.getValue();
        assertEquals(USER_ID, tags.get(TagMetricLabel.USER_ID.getName()));
    }

    @Test
    void addTransactionalDataFromDACTimer() {
        OffsetDateTime startDateTime = OffsetDateTime.now().minusMinutes(1);
        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);

        // when
        offersStateMetric.addTransactionalDataFromDACTimer(USER_ID, startDateTime);

        // then
        verify(metric).addTimer(eq(TimerMetricLabel.TRANSACTIONAL_DATA_DAC), eq(startDateTime), captor.capture());
        Map<String, String> tags = captor.getValue();
        assertEquals(USER_ID, tags.get(TagMetricLabel.USER_ID.getName()));
    }

    @Test
    void addOffersStateTimer() {
        OffsetDateTime startDateTime = OffsetDateTime.now().minusMinutes(1);
        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);

        // when
        offersStateMetric.addOffersStateTimer(USER_ID, startDateTime);

        // then
        verify(metric).addTimer(eq(TimerMetricLabel.OFFER_STATE), eq(startDateTime), captor.capture());
        Map<String, String> tags = captor.getValue();
        assertAll(
                () -> assertEquals(USER_ID, tags.get(TagMetricLabel.USER_ID.getName()))
        );
    }
}
