package de.joonko.loan.user.service;

import de.joonko.loan.offer.api.model.UserJourneyStateResponse;
import de.joonko.loan.user.states.OfferDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserStatesServiceTest {

    private UserStatesService userStatesService;

    private UserStatesStoreService userStatesStoreService;

    @BeforeEach
    void setUp() {
        userStatesStoreService = mock(UserStatesStoreService.class);
        userStatesService = new UserStatesServiceImpl(userStatesStoreService);
    }

    @Test
    void getLatestUserJourneyState_no_userState() {
        // given
        final var userUUID = randomUUID().toString();
        when(userStatesStoreService.findById(userUUID)).thenReturn(Mono.empty());

        // when
        final var result = userStatesService.getLatestUserJourneyState(userUUID);

        // then
        StepVerifier.create(result).consumeNextWith(res -> assertThat(res).isEqualToComparingFieldByField(
                UserJourneyStateResponse.builder()
                        .amount(null)
                        .purpose(null)
                        .state(UserJourneyStateResponse.UserJourneyState.MISSING_LOAN_AMOUNT).build())
        ).verifyComplete();
    }

    @Test
    void getLatestUserJourneyState_no_offers_requested() {
        // given
        final var userUUID = randomUUID().toString();
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setLastRequestedLoanAmount(null);
        when(userStatesStoreService.findById(userUUID)).thenReturn(Mono.just(userStatesStore));

        // when
        final var result = userStatesService.getLatestUserJourneyState(userUUID);

        // then
        StepVerifier.create(result).consumeNextWith(res -> assertThat(res).isEqualToComparingFieldByField(
                UserJourneyStateResponse.builder()
                        .amount(null)
                        .purpose(null)
                        .state(UserJourneyStateResponse.UserJourneyState.MISSING_LOAN_AMOUNT).build())
        ).verifyComplete();
    }

    @Test
    void getLatestUserJourneyState_has_offer() {
        // given
        final var userUUID = randomUUID().toString();
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setLastRequestedLoanAmount(8000);
        userStatesStore.setOfferDateStateDetailsSet(Set.of(OfferDataStateDetails.builder()
                .parentApplicationId(null)
                .amount(8000)
                .expired(false)
                .build()));

        when(userStatesStoreService.findById(userUUID)).thenReturn(Mono.just(userStatesStore));

        // when
        final var result = userStatesService.getLatestUserJourneyState(userUUID);

        // then
        StepVerifier.create(result).consumeNextWith(res -> assertThat(res).isEqualToComparingFieldByField(
                UserJourneyStateResponse.builder()
                        .amount(8000)
                        .purpose(null)
                        .state(UserJourneyStateResponse.UserJourneyState.EXISTING_LOAN_AMOUNT).build())
        ).verifyComplete();
    }

    @Test
    void getLatestUserJourneyState_hasLastRequestedAmount() {
        // given
        final var LOAN_PURPOSE = "car";
        final var userUUID = randomUUID().toString();
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setLastRequestedLoanAmount(8000);
        userStatesStore.setLastRequestedPurpose(LOAN_PURPOSE);

        when(userStatesStoreService.findById(userUUID)).thenReturn(Mono.just(userStatesStore));

        // when
        final var result = userStatesService.getLatestUserJourneyState(userUUID);

        // then
        StepVerifier.create(result).consumeNextWith(res -> assertThat(res).isEqualToComparingFieldByField(
                UserJourneyStateResponse.builder()
                        .amount(8000)
                        .purpose(LOAN_PURPOSE)
                        .state(UserJourneyStateResponse.UserJourneyState.EXISTING_LOAN_AMOUNT).build())
        ).verifyComplete();
    }
}
