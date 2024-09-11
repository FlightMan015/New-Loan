package de.joonko.loan.user.states;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserStateServiceTest {

    private UserStateService userStateService;
    private UserStatesStoreService userStatesStoreService;

    private static final String USER_ID = "2f20a660-f0f2-4ca5-9fe6-b24b52cd1070";

    @BeforeEach
    void setUp() {
        userStatesStoreService = mock(ReactiveUserStatesStoreService.class);
        userStateService = new UserStateServiceImpl(userStatesStoreService);
    }

    @Test
    void saveUserStates() {
        // given
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(USER_ID);
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));

        // when
        var monoUserStateStore = userStateService.saveUserStates(userStatesStore);

        // then
        assertAll(
                () -> StepVerifier.create(monoUserStateStore).expectNextCount(1).verifyComplete(),
                () -> verify(userStatesStoreService).save(userStatesStore)
        );
    }

    @Test
    void addNewOffersStatesWhenSomeAlreadyExisted() {
        // given
        UserStatesStore userStatesStore = new UserStatesStore();
        Set<OfferDataStateDetails> existingOffersStates = new HashSet<>();
        existingOffersStates.add(OfferDataStateDetails.builder().applicationId("applicationId123").build());
        userStatesStore.setOfferDateStateDetailsSet(existingOffersStates);
        when(userStatesStoreService.findById(USER_ID)).thenReturn(Mono.just(userStatesStore));
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));

        Set<OfferDataStateDetails> newOffersStates = Set.of(
                OfferDataStateDetails.builder().applicationId("applicationId1").build(),
                OfferDataStateDetails.builder().applicationId("applicationId2").build()
        );

        // when
        var monoUserStateStore = userStateService.addOffersStates(USER_ID, newOffersStates);

        // then
        assertAll(
                () -> StepVerifier.create(monoUserStateStore).expectNextMatches(
                        statesStore -> statesStore.getOfferDateStateDetailsSet().size() == 3
                ).verifyComplete(),
                () -> verify(userStatesStoreService).findById(USER_ID),
                () -> verify(userStatesStoreService).save(userStatesStore)
        );
    }

    @Test
    void addNewOffersStatesWhenNoneExistedBefore() {
        // given
        UserStatesStore userStatesStore = new UserStatesStore();
        when(userStatesStoreService.findById(USER_ID)).thenReturn(Mono.just(userStatesStore));
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));

        Set<OfferDataStateDetails> newOffersStates = Set.of(
                OfferDataStateDetails.builder().applicationId("applicationId1").build(),
                OfferDataStateDetails.builder().applicationId("applicationId2").build()
        );

        // when
        var monoUserStateStore = userStateService.addOffersStates(USER_ID, newOffersStates);

        // then
        assertAll(
                () -> StepVerifier.create(monoUserStateStore).expectNextMatches(
                        statesStore -> statesStore.getOfferDateStateDetailsSet().size() == 2
                ).verifyComplete(),
                () -> verify(userStatesStoreService).findById(USER_ID),
                () -> verify(userStatesStoreService).save(userStatesStore)
        );
    }

    @Test
    void doNotAddOffersStatesWhenEmpty() {
        // given
        Set<OfferDataStateDetails> newOffersStates = Set.of();

        // when
        var monoUserStateStore = userStateService.addOffersStates(USER_ID, newOffersStates);

        // then
        assertAll(
                () -> StepVerifier.create(monoUserStateStore).expectNextCount(0).verifyComplete(),
                () -> verifyNoInteractions(userStatesStoreService)
        );
    }

    @Test
    void doNotAddOffersStatesWhenMissingUserState() {
        // given
        when(userStatesStoreService.findById(USER_ID)).thenReturn(Mono.empty());
        Set<OfferDataStateDetails> newOffersStates = Set.of(OfferDataStateDetails.builder().build());

        // when
        var monoUserStateStore = userStateService.addOffersStates(USER_ID, newOffersStates);

        // then
        assertAll(
                () -> StepVerifier.create(monoUserStateStore).expectNextCount(0).verifyComplete(),
                () -> verify(userStatesStoreService).findById(USER_ID),
                () -> verifyNoMoreInteractions(userStatesStoreService)
        );
    }

    @Test
    void saveUpdatedOffersStates() {
        // given
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(USER_ID);
        final var datetime = OffsetDateTime.now().minusMinutes(30);
        Set<OfferDataStateDetails> existingOffersStates = Set.of(
                OfferDataStateDetails.builder().applicationId("applicationId1").requestDateTime(OffsetDateTime.now().minusSeconds(30)).build(),
                OfferDataStateDetails.builder().applicationId("applicationId2").requestDateTime(OffsetDateTime.now().minusSeconds(50)).build(),
                OfferDataStateDetails.builder().applicationId("applicationId3").requestDateTime(datetime).build());
        userStatesStore.setOfferDateStateDetailsSet(existingOffersStates);

        when(userStatesStoreService.findById(USER_ID)).thenReturn(Mono.just(userStatesStore));
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));

        Set<OfferDataStateDetails> updatedOffersStates = Set.of(
                OfferDataStateDetails.builder().applicationId("applicationId1")
                        .state(Status.SUCCESS)
                        .responseDateTime(OffsetDateTime.now())
                        .build(),
                OfferDataStateDetails.builder().applicationId("applicationId2")
                        .state(Status.SUCCESS)
                        .responseDateTime(OffsetDateTime.now())
                        .build());

        // when
        var monoUserStateStore = userStateService.saveUpdatedOffersStates(USER_ID, updatedOffersStates);

        // then
        assertAll(
                () -> StepVerifier.create(monoUserStateStore).expectNextMatches(
                        statesStore -> statesStore.getOfferDateStateDetailsSet().size() == 3 &&
                                statesStore.getOfferDateStateDetailsSet().stream()
                                        .filter(stateDetails -> Set.of("applicationId1","applicationId2").contains(stateDetails.getApplicationId()))
                                        .allMatch(stateDetails -> stateDetails.getState() == Status.SUCCESS && stateDetails.getRequestDateTime().isBefore(stateDetails.getResponseDateTime())) &&
                                statesStore.getOfferDateStateDetailsSet().stream()
                                        .filter(stateDetails -> "applicationId3".equals(stateDetails.getApplicationId()))
                                        .allMatch(stateDetails -> stateDetails.getState() != Status.SUCCESS && stateDetails.getResponseDateTime() == null)
                ).verifyComplete(),
                () -> verify(userStatesStoreService).findById(USER_ID),
                () -> verify(userStatesStoreService).save(userStatesStore)
        );
    }

    @Test
    void doNotUpdateOffersStatesWhenMissingUserState() {
        // given
        when(userStatesStoreService.findById(USER_ID)).thenReturn(Mono.empty());
        Set<OfferDataStateDetails> newOffersStates = Set.of(OfferDataStateDetails.builder().build());

        // when
        var monoUserStateStore = userStateService.saveUpdatedOffersStates(USER_ID, newOffersStates);

        // then
        assertAll(
                () -> StepVerifier.create(monoUserStateStore).expectNextCount(0).verifyComplete(),
                () -> verify(userStatesStoreService).findById(USER_ID),
                () -> verifyNoMoreInteractions(userStatesStoreService)
        );
    }

    @Test
    void doNotUpdateOffersStatesWhenEmpty() {
        // given
        when(userStatesStoreService.findById(USER_ID)).thenReturn(Mono.empty());

        // when
        var monoUserStateStore = userStateService.saveUpdatedOffersStates(USER_ID, Set.of());

        // then
        assertAll(
                () -> StepVerifier.create(monoUserStateStore).expectNextCount(0).verifyComplete(),
                () -> verifyNoInteractions(userStatesStoreService)
        );
    }

    @Test
    void doNotSaveUserPersonalDataStateWhenMissingUserState() {
        // given
        StateDetails personalState = StateDetails.builder().build();
        when(userStatesStoreService.findById(USER_ID)).thenReturn(Mono.empty());

        // when
        var monoUserStateStore = userStateService.saveUserPersonalDataState(USER_ID, personalState);

        // then
        assertAll(
                () -> StepVerifier.create(monoUserStateStore).expectNextCount(0).verifyComplete(),
                () -> verify(userStatesStoreService).findById(USER_ID),
                () -> verifyNoMoreInteractions(userStatesStoreService)
        );
    }

    @Test
    void saveUserPersonalDataState() {
        // given
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(USER_ID);
        StateDetails personalState = StateDetails.builder().build();
        when(userStatesStoreService.findById(USER_ID)).thenReturn(Mono.just(userStatesStore));
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));

        // when
        var monoUserStateStore = userStateService.saveUserPersonalDataState(USER_ID, personalState);

        // then
        assertAll(
                () -> StepVerifier.create(monoUserStateStore).expectNextCount(1).verifyComplete(),
                () -> verify(userStatesStoreService).findById(USER_ID),
                () -> verify(userStatesStoreService).save(userStatesStore),
                () -> assertNotNull(userStatesStore.getUserPersonalInformationStateDetails())
        );
    }
}
