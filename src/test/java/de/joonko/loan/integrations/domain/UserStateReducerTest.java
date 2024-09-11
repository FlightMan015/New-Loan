package de.joonko.loan.integrations.domain;

import de.joonko.loan.integrations.configuration.GetOffersConfigurations;
import de.joonko.loan.integrations.model.DacDataState;
import de.joonko.loan.integrations.model.PersonalDataState;
import de.joonko.loan.offer.OfferDemandRequest;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.joonko.loan.integrations.model.DacDataState.FTS_DATA_EXISTS;
import static de.joonko.loan.integrations.model.OffersState.MISSING_OR_STALE;
import static de.joonko.loan.integrations.model.OffersState.OFFERS_EXIST;
import static de.joonko.loan.integrations.model.PersonalDataState.EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, RandomBeansExtension.class})
class UserStateReducerTest {

    @Mock
    private UserStateEvaluator userStateEvaluator;
    @Mock
    private UserStatesStoreService userStatesStoreService;
    @InjectMocks
    private UserStateReducer userStateReducer;
    @Mock
    private GetOffersConfigurations offersConfigurations;

    @Test
    @DisplayName("when offers are valid offers exist status is set")
    void validOffersCaseForExistingUser(@Random OfferDemandRequest offerDemandRequest) {
        offerDemandRequest.setInetAddress(Optional.empty());
        when(userStateEvaluator.getTransactionalDataState(any(UserStatesStore.class))).thenReturn(FTS_DATA_EXISTS);
        when(offersConfigurations.getNewImplementationEnabled()).thenReturn(false);
        when(userStateEvaluator.getPersonalDataStateOld(any(UserStatesStore.class))).thenReturn(EXISTS);
        when(userStateEvaluator.getOffersState(anyInt(), anyString(), any(UserStatesStore.class))).thenReturn(OFFERS_EXIST);
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(offerDemandRequest.getUserUUID());
        userStatesStore.setLastRequestedLoanAmount(offerDemandRequest.getRequestedLoanAmount());
        userStatesStore.setIsLastRequestedBonifyLoans(offerDemandRequest.isOnlyBonify());
        when(userStatesStoreService.findById(anyString())).thenReturn(Mono.just(userStatesStore));
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));

        StepVerifier.create(userStateReducer.deriveUserState(offerDemandRequest))
                .consumeNextWith(r ->
                        assertAll(
                                () -> assertThat(r).isNotNull(),
                                () -> assertThat(r.getRequestedAmount()).isEqualTo(offerDemandRequest.getRequestedLoanAmount()),
                                () -> assertThat(r.getRequestedPurpose()).isEqualTo(offerDemandRequest.getRequestedLoanPurpose()),
                                () -> assertThat(r.isRequestedBonifyLoans()).isEqualTo(offerDemandRequest.isOnlyBonify()),
                                () -> assertThat(r.getUserUUID()).isEqualTo(offerDemandRequest.getUserUUID()),
                                () -> assertThat(r.getUserState().getDacDataState()).isEqualTo(FTS_DATA_EXISTS),
                                () -> assertThat(r.getUserState().getPersonalDataState()).isEqualTo(EXISTS),
                                () -> assertThat(r.getUserState().getOffersState()).isEqualTo(OFFERS_EXIST)
                        )
                ).verifyComplete();
    }

    @Test
    @DisplayName("when offers are valid offers exist status is set and no save on db")
    void validOffersCaseForExistingUserWithSameAmount(@Random OfferDemandRequest offerDemandRequest) {
        offerDemandRequest.setInetAddress(Optional.empty());
        when(userStateEvaluator.getTransactionalDataState(any(UserStatesStore.class))).thenReturn(FTS_DATA_EXISTS);
        when(offersConfigurations.getNewImplementationEnabled()).thenReturn(false);
        when(userStateEvaluator.getPersonalDataStateOld(any(UserStatesStore.class))).thenReturn(EXISTS);
        when(userStateEvaluator.getOffersState(anyInt(), anyString(), any(UserStatesStore.class))).thenReturn(OFFERS_EXIST);
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(offerDemandRequest.getUserUUID());
        userStatesStore.setLastRequestedLoanAmount(offerDemandRequest.getRequestedLoanAmount());
        userStatesStore.setLastRequestedPurpose(offerDemandRequest.getRequestedLoanPurpose());
        userStatesStore.setIsLastRequestedBonifyLoans(offerDemandRequest.isOnlyBonify());
        when(userStatesStoreService.findById(anyString())).thenReturn(Mono.just(userStatesStore));

        StepVerifier.create(userStateReducer.deriveUserState(offerDemandRequest))
                .consumeNextWith(r ->
                        assertAll(
                                () -> assertThat(r).isNotNull(),
                                () -> assertThat(r.getRequestedAmount()).isEqualTo(offerDemandRequest.getRequestedLoanAmount()),
                                () -> assertThat(r.getRequestedPurpose()).isEqualTo(offerDemandRequest.getRequestedLoanPurpose()),
                                () -> assertThat(r.isRequestedBonifyLoans()).isEqualTo(offerDemandRequest.isOnlyBonify()),
                                () -> assertThat(r.getUserUUID()).isEqualTo(offerDemandRequest.getUserUUID()),
                                () -> assertThat(r.getUserState().getDacDataState()).isEqualTo(FTS_DATA_EXISTS),
                                () -> assertThat(r.getUserState().getPersonalDataState()).isEqualTo(EXISTS),
                                () -> assertThat(r.getUserState().getOffersState()).isEqualTo(OFFERS_EXIST)
                        )
                ).verifyComplete();
    }

    @Test
    @DisplayName("when offers are stale correct status is set")
    void inValidOffersCaseForExistingUser(@Random OfferDemandRequest offerDemandRequest) {
        offerDemandRequest.setInetAddress(Optional.empty());
        when(userStateEvaluator.getTransactionalDataState(any(UserStatesStore.class))).thenReturn(DacDataState.MISSING_OR_STALE);
        when(offersConfigurations.getNewImplementationEnabled()).thenReturn(false);
        when(userStateEvaluator.getPersonalDataStateOld(any(UserStatesStore.class))).thenReturn(PersonalDataState.MISSING_OR_STALE);
        when(userStateEvaluator.getOffersState(anyInt(), anyString(), any(UserStatesStore.class))).thenReturn(MISSING_OR_STALE);
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(offerDemandRequest.getUserUUID());
        userStatesStore.setLastRequestedLoanAmount(offerDemandRequest.getRequestedLoanAmount() * 2 + 1);
        userStatesStore.setLastRequestedPurpose(offerDemandRequest.getRequestedLoanPurpose());
        userStatesStore.setIsLastRequestedBonifyLoans(offerDemandRequest.isOnlyBonify());
        when(userStatesStoreService.findById(anyString())).thenReturn(Mono.just(userStatesStore));
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));

        StepVerifier.create(userStateReducer.deriveUserState(offerDemandRequest))
                .consumeNextWith(r ->
                        assertAll(
                                () -> assertThat(r).isNotNull(),
                                () -> assertThat(r.getRequestedAmount()).isEqualTo(offerDemandRequest.getRequestedLoanAmount()),
                                () -> assertThat(r.getRequestedPurpose()).isEqualTo(offerDemandRequest.getRequestedLoanPurpose()),
                                () -> assertThat(r.isRequestedBonifyLoans()).isEqualTo(offerDemandRequest.isOnlyBonify()),
                                () -> assertThat(r.getUserUUID()).isEqualTo(offerDemandRequest.getUserUUID()),
                                () -> assertThat(r.getUserState().getOffersState()).isEqualTo(MISSING_OR_STALE)
                        )
                ).verifyComplete();
    }

    @Test
    @DisplayName("when user is new offers all three state are  set")
    void statusForNewUser(@Random OfferDemandRequest offerDemandRequest) {
        offerDemandRequest.setInetAddress(Optional.empty());
        when(userStateEvaluator.getTransactionalDataState(any(UserStatesStore.class))).thenReturn(DacDataState.MISSING_OR_STALE);
        when(offersConfigurations.getNewImplementationEnabled()).thenReturn(false);
        when(userStateEvaluator.getPersonalDataStateOld(any(UserStatesStore.class))).thenReturn(PersonalDataState.MISSING_OR_STALE);
        when(userStateEvaluator.getOffersState(anyInt(), anyString(), any(UserStatesStore.class))).thenReturn(MISSING_OR_STALE);
        when(userStatesStoreService.findById(anyString())).thenReturn(Mono.empty());
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(offerDemandRequest.getUserUUID());
        userStatesStore.setLastRequestedLoanAmount(offerDemandRequest.getRequestedLoanAmount());
        userStatesStore.setLastRequestedPurpose(offerDemandRequest.getRequestedLoanPurpose());
        userStatesStore.setIsLastRequestedBonifyLoans(offerDemandRequest.isOnlyBonify());
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));

        StepVerifier.create(userStateReducer.deriveUserState(offerDemandRequest))
                .consumeNextWith(r ->
                        assertAll(
                                () -> assertThat(r).isNotNull(),
                                () -> assertThat(r.getRequestedAmount()).isEqualTo(offerDemandRequest.getRequestedLoanAmount()),
                                () -> assertThat(r.getRequestedPurpose()).isEqualTo(offerDemandRequest.getRequestedLoanPurpose()),
                                () -> assertThat(r.isRequestedBonifyLoans()).isEqualTo(offerDemandRequest.isOnlyBonify()),
                                () -> assertThat(r.getUserUUID()).isEqualTo(offerDemandRequest.getUserUUID()),
                                () -> assertThat(r.getUserState().getDacDataState()).isEqualTo(DacDataState.MISSING_OR_STALE),
                                () -> assertThat(r.getUserState().getPersonalDataState()).isEqualTo(PersonalDataState.MISSING_OR_STALE),
                                () -> assertThat(r.getUserState().getOffersState()).isEqualTo(MISSING_OR_STALE)
                        )
                ).verifyComplete();
    }

    @Test
    @DisplayName("when user is new offers all three state are  set new implementatioon case")
    void statusForNewUserForNewFlow(@Random OfferDemandRequest offerDemandRequest) {
        offerDemandRequest.setInetAddress(Optional.empty());
        when(userStateEvaluator.getTransactionalDataState(any(UserStatesStore.class))).thenReturn(DacDataState.MISSING_OR_STALE);
        when(offersConfigurations.getNewImplementationEnabled()).thenReturn(true);
        when(userStateEvaluator.getPersonalDataState( any(UserStatesStore.class))).thenReturn(PersonalDataState.MISSING_OR_STALE);
        when(userStateEvaluator.getOffersState(anyInt(), anyString(), any(UserStatesStore.class))).thenReturn(MISSING_OR_STALE);
        when(userStatesStoreService.findById(anyString())).thenReturn(Mono.empty());
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(offerDemandRequest.getUserUUID());
        userStatesStore.setLastRequestedLoanAmount(offerDemandRequest.getRequestedLoanAmount());
        userStatesStore.setLastRequestedPurpose(offerDemandRequest.getRequestedLoanPurpose());
        userStatesStore.setIsLastRequestedBonifyLoans(offerDemandRequest.isOnlyBonify());
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));

        StepVerifier.create(userStateReducer.deriveUserState(offerDemandRequest))
                .consumeNextWith(r ->
                        assertAll(
                                () -> assertThat(r).isNotNull(),
                                () -> assertThat(r.getRequestedAmount()).isEqualTo(offerDemandRequest.getRequestedLoanAmount()),
                                () -> assertThat(r.getRequestedPurpose()).isEqualTo(offerDemandRequest.getRequestedLoanPurpose()),
                                () -> assertThat(r.isRequestedBonifyLoans()).isEqualTo(offerDemandRequest.isOnlyBonify()),
                                () -> assertThat(r.getUserUUID()).isEqualTo(offerDemandRequest.getUserUUID()),
                                () -> assertThat(r.getUserState().getDacDataState()).isEqualTo(DacDataState.MISSING_OR_STALE),
                                () -> assertThat(r.getUserState().getPersonalDataState()).isEqualTo(PersonalDataState.MISSING_OR_STALE),
                                () -> assertThat(r.getUserState().getOffersState()).isEqualTo(MISSING_OR_STALE)
                        )
                ).verifyComplete();
    }

}
