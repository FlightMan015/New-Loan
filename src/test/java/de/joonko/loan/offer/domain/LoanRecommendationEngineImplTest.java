package de.joonko.loan.offer.domain;


import de.joonko.loan.integrations.configuration.GetOffersConfigurations;
import de.joonko.loan.offer.api.CustomDACData;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.api.PersonalDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoanRecommendationEngineImplTest {

    private LoanRecommendationEngine loanRecommendationEngine;

    private GetOffersConfigurations offersConfigurations;

    @BeforeEach
    void setUp() {
        offersConfigurations = mock(GetOffersConfigurations.class);

        loanRecommendationEngine = new LoanRecommendationEngineImpl(offersConfigurations);
    }

    @Test
    void recommend_returns_empty_set_for_min_amount_loan_requests() {
        // given
        final var loanDemandRequest = LoanDemandRequest.builder()
                .loanAsked(1000)
                .applicationId("applicationId")
                ._id("_id")
                .personalDetails(PersonalDetails.builder().numberOfChildren(2).build())
                .customDACData(CustomDACData.builder().carInformation(true).build())
                .build();

        // when
        when(offersConfigurations.getMinimalLoanAmount()).thenReturn(1000);
        final var recommendedLoanDemandRequests = loanRecommendationEngine.recommend(loanDemandRequest);

        // then
        assertAll(
                () -> assertThat(recommendedLoanDemandRequests).isEmpty(),
                () -> verify(offersConfigurations).getMinimalLoanAmount(),
                () -> verifyNoMoreInteractions(offersConfigurations)
        );
    }

    @Test
    void recommend_method_gives_correct_recommendation_when_disposable_amount_is_smaller() {
        // given
        final var loanDemandRequest = LoanDemandRequest.builder()
                .loanAsked(100000)
                .applicationId("applicationId")
                ._id("_id")
                .disposableIncome(BigDecimal.valueOf(1619.6))
                .personalDetails(PersonalDetails.builder().numberOfChildren(2).build())
                .customDACData(CustomDACData.builder().carInformation(true).build())
                .build();

        when(offersConfigurations.getMinimalLoanAmount()).thenReturn(1000);
        when(offersConfigurations.getRecommendedLoanPercentage()).thenReturn(50);
        when(offersConfigurations.getMinimalLoanAmount()).thenReturn(1000);
        when(offersConfigurations.getRecommendedDisposableAmountMultiplier()).thenReturn(20);
        // when
        final var recommendedLoanDemandRequests = loanRecommendationEngine.recommend(loanDemandRequest);
        // then
        assertAll(
                () -> assertEquals(1, recommendedLoanDemandRequests.size()),
                () -> assertEquals(32500, recommendedLoanDemandRequests.stream().findFirst().get().getLoanAsked()),
                () -> assertNull(recommendedLoanDemandRequests.stream().findFirst().get().getApplicationId()),
                () -> assertNull(recommendedLoanDemandRequests.stream().findFirst().get().get_id())
        );

    }

    @Test
    void recommend_method_gives_correct_recommendation_when_per_percent_recommendation_is_smaller() {
        // given
        final var loanDemandRequest = LoanDemandRequest.builder()
                .loanAsked(50000)
                .disposableIncome(BigDecimal.valueOf(1619.6))
                .personalDetails(PersonalDetails.builder().numberOfChildren(2).build())
                .customDACData(CustomDACData.builder().carInformation(true).build())
                .build();

        when(offersConfigurations.getMinimalLoanAmount()).thenReturn(1000);
        when(offersConfigurations.getRecommendedLoanPercentage()).thenReturn(50);
        when(offersConfigurations.getMinimalLoanAmount()).thenReturn(1000);
        when(offersConfigurations.getRecommendedDisposableAmountMultiplier()).thenReturn(20);
        // when
        final var recommendedLoanDemandRequests = loanRecommendationEngine.recommend(loanDemandRequest);
        // then
        assertEquals(1, recommendedLoanDemandRequests.size());
        assertEquals(25000, recommendedLoanDemandRequests.stream().findFirst().get().getLoanAsked());
    }

    @Test
    void recommend_method_gives_correct_recommendation_when_recommended_amount_is_smaller_than_min() {
        // given
        final var loanDemandRequest = LoanDemandRequest.builder()
                .loanAsked(50000)
                .disposableIncome(BigDecimal.TEN)
                .personalDetails(PersonalDetails.builder().numberOfChildren(2).build())
                .customDACData(CustomDACData.builder().carInformation(true).build())
                .build();

        when(offersConfigurations.getMinimalLoanAmount()).thenReturn(1000);
        when(offersConfigurations.getRecommendedLoanPercentage()).thenReturn(50);
        when(offersConfigurations.getMinimalLoanAmount()).thenReturn(1000);
        when(offersConfigurations.getRecommendedDisposableAmountMultiplier()).thenReturn(20);
        // when
        final var recommendedLoanDemandRequests = loanRecommendationEngine.recommend(loanDemandRequest);
        // then
        assertAll(() -> {
            assertThat(recommendedLoanDemandRequests.size()).isEqualTo(1);
            assertThat(recommendedLoanDemandRequests.iterator().next().getLoanAsked()).isEqualTo(1000);
        });
    }

    @Test
    void recommend_method_gives_correct_recommendation_when_disposable_amount_is_null() {
        // given
        final var loanDemandRequest = LoanDemandRequest.builder()
                .loanAsked(50000)
                .disposableIncome(null)
                .personalDetails(PersonalDetails.builder().numberOfChildren(2).build())
                .customDACData(CustomDACData.builder().carInformation(true).build())
                .build();

        when(offersConfigurations.getMinimalLoanAmount()).thenReturn(1000);
        when(offersConfigurations.getRecommendedLoanPercentage()).thenReturn(50);
        when(offersConfigurations.getMinimalLoanAmount()).thenReturn(1000);
        when(offersConfigurations.getRecommendedDisposableAmountMultiplier()).thenReturn(20);
        // when
        final var recommendedLoanDemandRequests = loanRecommendationEngine.recommend(loanDemandRequest);
        // then
        assertAll(() -> {
            assertThat(recommendedLoanDemandRequests.size()).isEqualTo(1);
            assertThat(recommendedLoanDemandRequests.iterator().next().getLoanAsked()).isEqualTo(1000);
        });
    }
}
