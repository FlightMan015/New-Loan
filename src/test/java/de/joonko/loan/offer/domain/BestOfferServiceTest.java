package de.joonko.loan.offer.domain;

import de.joonko.loan.common.domain.LoanProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BestOfferServiceTest {

    @Test
    void calculateBestOffers_forEmptyOffers() {
        final var result = BestOfferService.calculateBestOffersPerCategory(List.of());

        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void calculateBestOffers(final List<LoanOffer> loanOffers, final List<BestLoanOffer> bestLoanOffers) {
        final var result = BestOfferService.calculateBestOffersPerCategory(loanOffers);

        result.forEach(r ->
                assertEquals(bestLoanOffers.stream().filter(bo ->
                        bo.getOfferCategory().equals(r.getOfferCategory())).findFirst().get(), r)
        );
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(List.of(
                                new LoanOffer(5000, 42, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(131.89), BigDecimal.valueOf(5539.38), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 24, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(221.22), BigDecimal.valueOf(5309.28), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 36, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(151.72), BigDecimal.valueOf(5461.92), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 66, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(88.73), BigDecimal.valueOf(5856.18), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 18, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(290.77), BigDecimal.valueOf(5233.86), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 12, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(429.94), BigDecimal.valueOf(5159.28), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 54, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(105.49), BigDecimal.valueOf(5696.46), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 60, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(96.27), BigDecimal.valueOf(5776.2), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 90, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(68.72), BigDecimal.valueOf(6184.8), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 96, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(65.29), BigDecimal.valueOf(6267.84), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 6, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(847.56), BigDecimal.valueOf(5085.36), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 30, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(179.51), BigDecimal.valueOf(5385.3), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 72, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(82.46), BigDecimal.valueOf(5937.12), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 84, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(72.63), BigDecimal.valueOf(6100.92), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 48, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(117.03), BigDecimal.valueOf(5617.44), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 78, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(77.17), BigDecimal.valueOf(6019.26), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 6, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(847.56), BigDecimal.valueOf(5085.36), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 72, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(82.46), BigDecimal.valueOf(5937.12), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 96, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(65.29), BigDecimal.valueOf(6267.84), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 18, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(290.77), BigDecimal.valueOf(5233.86), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 84, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(72.63), BigDecimal.valueOf(6100.92), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 36, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(151.72), BigDecimal.valueOf(5461.92), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 60, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(96.27), BigDecimal.valueOf(5776.2), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 48, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(117.03), BigDecimal.valueOf(5617.44), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 24, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(221.22), BigDecimal.valueOf(5309.28), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 12, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(429.94), BigDecimal.valueOf(5159.28), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 96, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(64.15), BigDecimal.valueOf(6158.4), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 18, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(289.7), BigDecimal.valueOf(5214.6), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 6, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(846.4), BigDecimal.valueOf(5078.4), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 84, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(71.51), BigDecimal.valueOf(6006.84), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 12, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(428.85), BigDecimal.valueOf(5146.2), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 48, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(115.95), BigDecimal.valueOf(5565.6), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 72, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(81.35), BigDecimal.valueOf(5857.2), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 36, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(150.65), BigDecimal.valueOf(5423.4), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 24, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(220.15), BigDecimal.valueOf(5283.6), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 60, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(95.17), BigDecimal.valueOf(5710.2), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 96, BigDecimal.valueOf(3.29), BigDecimal.ONE, BigDecimal.valueOf(59.2), BigDecimal.valueOf(5682.95), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 48, BigDecimal.valueOf(4.19), BigDecimal.ONE, BigDecimal.valueOf(113.2), BigDecimal.valueOf(5430.75), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 72, BigDecimal.valueOf(2.99), BigDecimal.ONE, BigDecimal.valueOf(75.9), BigDecimal.valueOf(5461.35), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 60, BigDecimal.valueOf(2.99), BigDecimal.ONE, BigDecimal.valueOf(89.8), BigDecimal.valueOf(5383.61), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 84, BigDecimal.valueOf(2.99), BigDecimal.ONE, BigDecimal.valueOf(66), BigDecimal.valueOf(5539.66), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 120, BigDecimal.valueOf(3.29), BigDecimal.ONE, BigDecimal.valueOf(48.9), BigDecimal.valueOf(5859.44), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 108, BigDecimal.valueOf(3.29), BigDecimal.ONE, BigDecimal.valueOf(53.5), BigDecimal.valueOf(5770.38), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 36, BigDecimal.valueOf(4.55), BigDecimal.ONE, BigDecimal.valueOf(148.7), BigDecimal.valueOf(5350.91), LoanProvider.builder().name("1").build()),
                                new LoanOffer(5000, 24, BigDecimal.valueOf(4.65), BigDecimal.ONE, BigDecimal.valueOf(218.4), BigDecimal.valueOf(5240.57), LoanProvider.builder().name("1").build())
                        )
                        , List.of(
                                new BestLoanOffer(null, OfferCategory.APR, 5000, 60, BigDecimal.valueOf(2.97), BigDecimal.valueOf(2.93), BigDecimal.valueOf(0.0297), BigDecimal.valueOf(89.79), new BigDecimal("5387.40")),
                                new BestLoanOffer(null, OfferCategory.MONTHLY_INSTALLMENT_AMOUNT, 5000, 120, BigDecimal.valueOf(3.25), new BigDecimal("3.20"), BigDecimal.valueOf(0.0325), BigDecimal.valueOf(48.86), new BigDecimal("5863.20")),
                                new BestLoanOffer(null, OfferCategory.TOTAL_REPAYMENT_AMOUNT, 5000, 6, BigDecimal.valueOf(5.47), BigDecimal.valueOf(5.35), BigDecimal.valueOf(0.0547), BigDecimal.valueOf(846.68), new BigDecimal("5080.08"))
                        )
                ));
    }

}