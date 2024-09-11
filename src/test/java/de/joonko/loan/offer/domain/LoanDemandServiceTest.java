package de.joonko.loan.offer.domain;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.metric.LoanDemandMetric;
import de.joonko.loan.metric.model.Process;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.joonko.loan.offer.domain.FinanceFixtures.getFinanceFreeToSpend;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(RandomBeansExtension.class)
@ExtendWith(MockitoExtension.class)
class LoanDemandServiceTest {

    private LoanDemandService loanDemandService;



    @Nested
    class WithRecommendation {

        @Mock
        private LoanDemandGateway gateway;

        @Mock
        private LoanDemandMetric metric;

        @BeforeEach
        void setup() {
            when(gateway.getDurations(any())).thenReturn(List.of(LoanDuration.TWENTY_FOUR));
            loanDemandService = new LoanDemandService(metric);
        }

        @Test
        void getLoanOffer(@Random LoanDemand loanDemand) {
            // given
            loanDemand.setParentLoanApplicationId("parentApplicationId");
            final var loanOffer = createLoanOffer(1000, 24);
            when(gateway.getLoanOffers(any(), any())).thenReturn(Flux.just(loanOffer));

            // when
            var loanOffers = loanDemandService.getLoanOffersForProviders(loanDemand, Set.of(gateway));

            // then
            StepVerifier.create(loanOffers)
                    .expectNextCount(1)
                    .verifyComplete();
        }
    }

    @Nested
    class WithOneGateway {

        @Mock
        private LoanDemandGateway gateway;

        @Mock
        private LoanDemandMetric metric;

        @BeforeEach
        void setup() {
            List<LoanDemandGateway> gateways = new ArrayList<>();
            gateways.add(gateway);
            when(gateway.getDurations(any())).thenReturn(List.of(LoanDuration.TWENTY_FOUR));
            loanDemandService = new LoanDemandService(metric);
        }

        @Test
        @DisplayName("Should return offers for the gateway")
        void getLoanOffers() {
            final var loanDemand = createLoanDemand(1000, LoanDuration.TWENTY_FOUR, 200);
            final var loanOffer = createLoanOffer(1000, 24);
            Mockito.lenient().when(gateway.getLoanOffers(any(), any())).thenReturn(Flux.just(loanOffer));

            Flux<LoanOffer> loanOffers = loanDemandService.getLoanOffersForProviders(loanDemand, Set.of(gateway));
            StepVerifier.create(loanOffers)
                    .expectSubscription()
                    .expectNextCount(1)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should return offers for the gateway")
        void getAllLoanOffers() {
            final var loanDemand = createLoanDemand(1500, LoanDuration.FORTY_EIGHT, 200);
            LoanOffer offer1 = new LoanOffer(1000, 24, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null);
            LoanOffer offer2 = new LoanOffer(1500, 24, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null);
            LoanOffer offer3 = new LoanOffer(1500, 48, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null);

            when(gateway.getLoanOffers(any(), any())).thenReturn(Flux.just(offer1, offer2, offer3));

            Flux<LoanOffer> loanOffers = loanDemandService.getLoanOffersForProviders(loanDemand, Set.of(gateway));
            StepVerifier.create(loanOffers)
                    .expectSubscription()
                    .expectNextCount(3)
                    .verifyComplete();

        }


        @Test
        @DisplayName("Should return empty response if exception is thrown by the gateway")
        void shouldReturnEmptyResponse() {
            final var loanDemand = createLoanDemand(1000, LoanDuration.TWENTY_FOUR, 200);
            when(gateway.getLoanOffers(any(), any())).thenThrow(new RuntimeException());

            Flux<LoanOffer> loanOffers = loanDemandService.getLoanOffersForProviders(loanDemand, Set.of(gateway));
            StepVerifier.create(loanOffers)
                    .expectSubscription()
                    .expectNextCount(0)
                    .verifyComplete();

        }
    }

    @Nested
    class WithMultipleGateways {

        @Mock
        private LoanDemandGateway gateway1;

        @Mock
        private LoanDemandGateway gateway2;

        @Mock
        private LoanDemandMetric metric;

        @BeforeEach
        void setup() {
            loanDemandService = new LoanDemandService( metric);
        }

        @Test
        @DisplayName("Should return offers for all gateways")
        void getLoanOffers() {
            final var loanDemand = createLoanDemand(1000, LoanDuration.TWENTY_FOUR, 200);
            LoanOffer loanOffer1 = createLoanOffer(1000, 24);
            LoanOffer loanOffer2 = createLoanOffer(1000, 24);
            when(gateway1.getLoanOffers(any(), any())).thenReturn(Flux.just(loanOffer1));
            when(gateway1.getLoanProvider()).thenReturn(LoanProvider.builder().name(Bank.SANTANDER.getLabel()).build());
            when(gateway1.getDurations(any())).thenReturn(List.of(LoanDuration.TWENTY_FOUR));
            when(gateway1.getCallApiProcessName()).thenReturn(Process.GET_OFFERS);
            when(gateway2.getLoanOffers(any(), any())).thenReturn(Flux.just(loanOffer2));
            when(gateway2.getLoanProvider()).thenReturn(LoanProvider.builder().name(Bank.POSTBANK.getLabel()).build());
            when(gateway2.getDurations(any())).thenReturn(List.of(LoanDuration.TWENTY_FOUR));
            when(gateway2.getCallApiProcessName()).thenReturn(Process.GET_OFFERS);

            Flux<LoanOffer> loanOffers = loanDemandService.getLoanOffersForProviders(loanDemand, Set.of(gateway1, gateway2));

            assertAll(
                    () -> StepVerifier.create(loanOffers).expectSubscription()
                            .expectNextCount(2)
                            .verifyComplete(),
                    () -> verify(metric, times(2)).addTimer(any(LoanProvider.class), any(OffsetDateTime.class), any(Process.class))
            );
        }

        @Test
        @DisplayName("Offers of first gateway are returned if second gateway is broken")
        @MockitoSettings(strictness = Strictness.LENIENT)
        void returnsOffersOfFirstGateway() {
            // given
            final var loanDemand = createLoanDemand(1000, LoanDuration.FORTY_EIGHT, 200);
            LoanOffer loanOffer = createLoanOffer(1000, 48);
            when(gateway1.getDurations(any())).thenReturn(List.of(LoanDuration.FORTY_EIGHT));
            when(gateway1.getLoanOffers(any(), any())).thenReturn(Flux.just(loanOffer));
            when(gateway2.getLoanOffers(any(), any())).thenReturn(Flux.error(RuntimeException::new));
            when(gateway2.getDurations(any())).thenReturn(List.of(LoanDuration.FORTY_EIGHT));

            // when
            Flux<LoanOffer> loanOffers = loanDemandService.getLoanOffersForProviders(loanDemand, Set.of(gateway1, gateway2));

            // then
            StepVerifier.create(loanOffers)
                    .expectSubscription()
                    .expectNext(loanOffer)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Offers of second gateway are returned if first gateway is broken")
        @MockitoSettings(strictness = Strictness.LENIENT)
        void returnsOffersOfSecondGateway() {
            final var loanDemand = createLoanDemand(1000, LoanDuration.TWENTY_FOUR, 200);
            LoanOffer loanOffer = createLoanOffer(1000, 24);
            when(gateway1.getLoanOffers(any(), any())).thenReturn(Flux.error(new RuntimeException()));
            when(gateway1.getDurations(any())).thenReturn(List.of(LoanDuration.TWENTY_FOUR));
            when(gateway2.getLoanOffers(any(), any())).thenReturn(Flux.just(loanOffer));
            when(gateway2.getDurations(any())).thenReturn(List.of(LoanDuration.TWENTY_FOUR));
            Flux<LoanOffer> loanOffers = loanDemandService.getLoanOffersForProviders(loanDemand, Set.of(gateway1, gateway2));
            StepVerifier.create(loanOffers)
                    .expectSubscription()
                    .expectNext(loanOffer)
                    .verifyComplete();
        }
    }

    private LoanDemand createLoanDemand(int loanAsked, LoanDuration loanDuration, int freeToSpend) {
        return new LoanDemand(RandomStringUtils.randomAlphabetic(20), loanAsked, "car", loanDuration, LoanCategory.FURNITURE_RENOVATION_MOVE, PersonalDetails.builder()
                .finance(getFinanceFreeToSpend(freeToSpend))
                .build(), null, null, null, null, null, null, null, null, null, List.of(), RandomStringUtils.randomAlphabetic(20));
    }

    private LoanOffer createLoanOffer(int loanAmount, int loanDuration) {
        return new LoanOffer(loanAmount, loanDuration, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null);
    }

}
