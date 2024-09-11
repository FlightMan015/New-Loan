package de.joonko.loan.offer.domain;

import de.joonko.loan.metric.LoanDemandMetric;
import de.joonko.loan.offer.api.LoanProvidersService;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(RandomBeansExtension.class)
class GatewayFilterTest {

    private LoanDemandGatewayFilter loanDemandGatewayFilter;

    @Mock
    private LoanDemandMetric loanDemandMetric;

    @Mock
    private LoanProvidersService loanProvidersService;

    @Mock
    private LoanDemandGateway gateway1;

    @Mock
    private LoanDemandGateway gateway2;

    @BeforeEach
    void setUp() {
        loanDemandGatewayFilter = new LoanDemandGatewayFilter(loanDemandMetric, loanProvidersService);
    }

    @Test
    void getEmptySetWhenNoAvailableGateways(@Random LoanDemand loanDemand) {
        // given
        when(loanProvidersService.getGateways()).thenReturn(List.of());

        // when
        var actualSet = loanDemandGatewayFilter.filterValidGatewaysForLoanDemand(loanDemand);

        // then
        assertAll(
                () -> assertTrue(actualSet.isEmpty()),
                () -> verify(loanProvidersService).getGateways()
        );
    }

    @SneakyThrows
    @Test
    void getEmptySetWhenIsNotValid(@Random LoanDemand loanDemand) {
        // given
        loanDemand.setParentLoanApplicationId(null);
        when(loanProvidersService.getGateways()).thenReturn(List.of(gateway1, gateway2));
        when(gateway1.filterGateway(loanDemand)).thenReturn(true);
        when(gateway2.filterGateway(loanDemand)).thenReturn(true);

        // when
        var actualSet = loanDemandGatewayFilter.filterValidGatewaysForLoanDemand(loanDemand);

        // then
        assertAll(
                () -> assertTrue(actualSet.isEmpty()),
                () -> verify(loanProvidersService).getGateways(),
                () -> verify(loanDemandMetric, times(2)).incrementPrecheckCounter(eq(false), any())
        );
    }

    @SneakyThrows
    @Test
    void getSetWhenIsValidAndIsRecommended(@Random LoanDemand loanDemand) {
        // given
        loanDemand.setParentLoanApplicationId("932865932");
        when(loanProvidersService.getGateways()).thenReturn(List.of(gateway1, gateway2));
        when(loanProvidersService.isRecommendedEnabled(any())).thenReturn(true);
        when(gateway1.filterGateway(loanDemand)).thenReturn(false);
        when(gateway2.filterGateway(loanDemand)).thenReturn(true);

        // when
        var actualSet = loanDemandGatewayFilter.filterValidGatewaysForLoanDemand(loanDemand);

        // then
        assertAll(
                () -> assertEquals(1, actualSet.size()),
                () -> verify(loanProvidersService).getGateways(),
                () -> verify(loanDemandMetric).incrementPrecheckCounter(eq(true), any()),
                () -> verify(loanDemandMetric).incrementPrecheckCounter(eq(false), any())
        );
    }

    @SneakyThrows
    @Test
    void getSetWhenIsValidAndIsNotRecommended(@Random LoanDemand loanDemand) {
        // given
        loanDemand.setParentLoanApplicationId(null);
        when(loanProvidersService.getGateways()).thenReturn(List.of(gateway1, gateway2));
        when(gateway1.filterGateway(loanDemand)).thenReturn(false);
        when(gateway2.filterGateway(loanDemand)).thenReturn(true);

        // when
        var actualSet = loanDemandGatewayFilter.filterValidGatewaysForLoanDemand(loanDemand);

        // then
        assertAll(
                () -> assertEquals(1, actualSet.size()),
                () -> verify(loanProvidersService).getGateways(),
                () -> verify(loanDemandMetric).incrementPrecheckCounter(eq(true), any()),
                () -> verify(loanDemandMetric).incrementPrecheckCounter(eq(false), any())
        );
    }
}
