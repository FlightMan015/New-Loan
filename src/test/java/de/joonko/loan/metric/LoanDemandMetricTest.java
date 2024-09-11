package de.joonko.loan.metric;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.metric.model.CounterMetricLabel;
import de.joonko.loan.metric.model.Process;
import de.joonko.loan.metric.model.TagMetricLabel;
import de.joonko.loan.metric.model.TimerMetricLabel;
import de.joonko.loan.offer.api.LoanProvidersService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class LoanDemandMetricTest {

    private LoanDemandMetric loanDemandMetric;

    private Metric metric;
    private LoanProvidersService loanProvidersService;

    private static final String APPLICATION_ID = "5f561054e3fa3d3d1a7ef3f4";

    @BeforeEach
    void setUp() {
        metric = mock(Metric.class);
        loanProvidersService = mock(LoanProvidersService.class);
        loanDemandMetric = new LoanDemandMetric(metric, loanProvidersService);
    }

    @Test
    void addTimer() {
        // given
        final var startTime = OffsetDateTime.now().minusMinutes(2);
        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);

        // when
        loanDemandMetric.addTimer(new LoanProvider("SANTANDER"), startTime, Process.GET_OFFERS);

        // then
        verify(metric).addTimer(eq(TimerMetricLabel.LOAN_DEMAND), eq(startTime), captor.capture());
        Map<String, String> tags = captor.getValue();

        assertAll(
                () -> assertEquals("SANTANDER", tags.get(TagMetricLabel.OFFER_PROVIDER.getName())),
                () -> assertEquals("get_offers", tags.get(TagMetricLabel.PROCESS.getName()))
        );
    }

    @Test
    void incrementPrecheckCounter() {
        // given
        LoanProvider loanProvider = LoanProvider.builder().name(Bank.SWK_BANK.getLabel()).build();
        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);

        // when
        loanDemandMetric.incrementPrecheckCounter(true, loanProvider);

        // then
        verify(metric).incrementCounter(eq(CounterMetricLabel.PRE_CHECK), captor.capture());
        Map<String, String> tags = captor.getValue();
        assertAll(
                () -> assertEquals("SWK_BANK", tags.get(TagMetricLabel.OFFER_PROVIDER.getName())),
                () -> assertEquals("success", tags.get(TagMetricLabel.STATUS.getName()))
        );
    }

    @Test
    void doNotIncrementOfferDemandCounterWhenNoActiveLoanProviders() {
        // given
        when(loanProvidersService.getLoanOffersProviders(APPLICATION_ID)).thenReturn(List.of());
        when(loanProvidersService.getActiveLoanProviders()).thenReturn(Flux.empty());

        // when
        loanDemandMetric.incrementCounterForEachLoanProvider(APPLICATION_ID, false);

        // then
        verifyNoInteractions(metric);
    }

    @Test
    void doNotIncrementOfferDemandRecommendedCounterWhenNoEnabledLoanProviders() {
        // given
        when(loanProvidersService.getLoanOffersProviders(APPLICATION_ID)).thenReturn(List.of());
        when(loanProvidersService.getActiveLoanProviders()).thenReturn(Flux.just(Bank.CONSORS.getLabel(), Bank.SWK_BANK.getLabel()));
        when(loanProvidersService.getEnabledRecommendedLoanProviders()).thenReturn(List.of());

        // when
        loanDemandMetric.incrementCounterForEachLoanProvider(APPLICATION_ID, true);

        // then
        verifyNoInteractions(metric);
    }

    @Test
    void incrementOfferDemandCounterWhenActiveLoanProvidersAndNoOffersExist() {
        // given
        when(loanProvidersService.getLoanOffersProviders(APPLICATION_ID)).thenReturn(List.of());
        when(loanProvidersService.getActiveLoanProviders()).thenReturn(Flux.just(Bank.CONSORS.getLabel(), Bank.SWK_BANK.getLabel()));
        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);

        // when
        loanDemandMetric.incrementCounterForEachLoanProvider(APPLICATION_ID, false);

        // then
        verify(metric, times(2)).incrementCounter(eq(CounterMetricLabel.OFFER_DEMAND), captor.capture());
        List<Map<String, String>> tags = captor.getAllValues();
        assertAll(
                () -> assertEquals("Consors Finanz", tags.get(0).get(TagMetricLabel.OFFER_PROVIDER.getName())),
                () -> assertEquals("failure", tags.get(0).get(TagMetricLabel.STATUS.getName())),
                () -> assertEquals("SWK_BANK", tags.get(1).get(TagMetricLabel.OFFER_PROVIDER.getName())),
                () -> assertEquals("failure", tags.get(1).get(TagMetricLabel.STATUS.getName()))
        );
    }

    @Test
    void incrementOfferDemandCounterWhenActiveLoanProvidersAndSomeOffersExist() {
        // given
        when(loanProvidersService.getLoanOffersProviders(APPLICATION_ID)).thenReturn(List.of(Bank.SWK_BANK.getLabel()));
        when(loanProvidersService.getActiveLoanProviders()).thenReturn(Flux.just(Bank.CONSORS.getLabel(), Bank.SWK_BANK.getLabel()));
        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);

        // when
        loanDemandMetric.incrementCounterForEachLoanProvider(APPLICATION_ID, false);

        // then
        verify(metric, times(2)).incrementCounter(eq(CounterMetricLabel.OFFER_DEMAND), captor.capture());
        List<Map<String, String>> tags = captor.getAllValues();
        assertAll(
                () -> assertEquals("Consors Finanz", tags.get(0).get(TagMetricLabel.OFFER_PROVIDER.getName())),
                () -> assertEquals("failure", tags.get(0).get(TagMetricLabel.STATUS.getName())),
                () -> assertEquals("SWK_BANK", tags.get(1).get(TagMetricLabel.OFFER_PROVIDER.getName())),
                () -> assertEquals("success", tags.get(1).get(TagMetricLabel.STATUS.getName()))
        );
    }

    @Test
    void incrementOfferDemandRecommendedCounterWhenEnabledLoanProvidersAndSomeOffersExist() {
        // given
        when(loanProvidersService.getLoanOffersProviders(APPLICATION_ID)).thenReturn(List.of(Bank.SWK_BANK.getLabel()));
        when(loanProvidersService.getActiveLoanProviders()).thenReturn(Flux.just(Bank.CONSORS.getLabel(), Bank.SWK_BANK.getLabel(), Bank.SANTANDER.getLabel()));
        when(loanProvidersService.getEnabledRecommendedLoanProviders()).thenReturn(List.of(Bank.CONSORS.getLabel(), Bank.SWK_BANK.getLabel()));
        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);

        // when
        loanDemandMetric.incrementCounterForEachLoanProvider(APPLICATION_ID, true);

        // then
        verify(metric, times(2)).incrementCounter(eq(CounterMetricLabel.OFFER_DEMAND_RECOMMENDED), captor.capture());
        List<Map<String, String>> tags = captor.getAllValues();
        assertAll(
                () -> assertEquals("Consors Finanz", tags.get(0).get(TagMetricLabel.OFFER_PROVIDER.getName())),
                () -> assertEquals("failure", tags.get(0).get(TagMetricLabel.STATUS.getName())),
                () -> assertEquals("SWK_BANK", tags.get(1).get(TagMetricLabel.OFFER_PROVIDER.getName())),
                () -> assertEquals("success", tags.get(1).get(TagMetricLabel.STATUS.getName()))
        );
    }

}
