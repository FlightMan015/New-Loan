package de.joonko.loan.integrations.domain.integrationhandler.userpersonal;

import de.joonko.loan.integrations.configuration.GetOffersConfigurations;
import de.joonko.loan.integrations.domain.integrationhandler.IntegrationHandler;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider.UserPersonalDataAggregator;
import de.joonko.loan.integrations.model.DistributionChannel;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.integrations.model.OffersState;
import de.joonko.loan.integrations.model.PersonalDataState;
import de.joonko.loan.integrations.model.UserState;
import de.joonko.loan.metric.OffersStateMetric;
import de.joonko.loan.user.states.StateDetails;
import de.joonko.loan.user.states.Status;
import de.joonko.loan.user.states.UserStateService;
import de.joonko.loan.user.states.UserStatesStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.OffsetDateTime;
import java.util.UUID;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(RandomBeansExtension.class)
class UserPersonalDataIntegrationHandlerTest {

    private IntegrationHandler integrationHandler;

    private UserPersonalDataFilter userPersonalDataFilter;
    private UserPersonalDataValidator userPersonalDataValidator;
    private UserPersonalDataStorageService userPersonalDataStorageService;
    private OffersStateMetric offersStateMetric;
    private UserStateService userStateService;
    private UserPersonalDataAggregator userPersonalDataAggregator;
    private GetOffersConfigurations offersConfigurations;

    @BeforeEach
    void setUp() {
        userPersonalDataFilter = new UserPersonalDataFilter();
        userPersonalDataValidator = mock(UserPersonalDataValidator.class);
        userPersonalDataStorageService = mock(UserPersonalDataStorageService.class);
        offersStateMetric = mock(OffersStateMetric.class);
        userStateService = mock(UserStateService.class);
        userPersonalDataAggregator = mock(UserPersonalDataAggregator.class);
        offersConfigurations = mock(GetOffersConfigurations.class);


        integrationHandler = new UserPersonalDataIntegrationHandler(userPersonalDataFilter, userPersonalDataValidator, userPersonalDataStorageService,
                offersStateMetric, userStateService, userPersonalDataAggregator, offersConfigurations);
    }

    @Test
    void doNotTriggerGettingUserPersonalDataWhenNotValidState(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder()
                .personalDataState(PersonalDataState.EXISTS)
                .offersState(OffersState.OFFERS_EXIST)
                .build());

        // when
        var triggered = integrationHandler.triggerMutation(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(triggered).expectNextCount(0).verifyComplete(),
                () -> verifyNoInteractions(userStateService)
        );
    }

    @Test
    void getMissingUserInputWhenFailedGettingUserPersonalData(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder()
                .personalDataState(PersonalDataState.MISSING_OR_STALE)
                .offersState(OffersState.MISSING_OR_STALE)
                .build());
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(offerRequest.getUserUUID());
        userStatesStore.setUserPersonalInformationStateDetails(StateDetails.builder().requestDateTime(OffsetDateTime.now()).build());
        when(userStateService.saveUserPersonalDataState(eq(offerRequest.getUserUUID()), any(StateDetails.class))).thenReturn(Mono.just(userStatesStore));
        when(userStateService.saveUserStates(any(UserStatesStore.class))).thenReturn(Mono.just(userStatesStore));
        when(userPersonalDataAggregator.getUserPersonalData(offerRequest.getUserUUID())).thenReturn(Mono.error(new RuntimeException("Failed getting user personal data")));
        when(userPersonalDataValidator.test(any(UserPersonalData.class))).thenReturn(false);
        when(userStateService.saveUserPersonalDataState(eq(offerRequest.getUserUUID()), any(StateDetails.class))).thenReturn(Mono.just(userStatesStore));

        // when
        var triggered = integrationHandler.triggerMutation(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(triggered).expectNextCount(0).verifyComplete(),
                () -> verify(userStateService).saveUserPersonalDataState(eq(offerRequest.getUserUUID()), any(StateDetails.class)),
                () -> verify(userStateService).saveUserStates(userStatesStore),
                () -> verify(userPersonalDataAggregator).getUserPersonalData(offerRequest.getUserUUID()),
                () -> verifyNoInteractions(userPersonalDataStorageService),
                () -> assertEquals(Status.MISSING_USER_INPUT, userStatesStore.getUserPersonalInformationStateDetails().getState()),
                () -> verify(offersStateMetric).addPersonalInfoTimer(eq(offerRequest.getUserUUID()), any(OffsetDateTime.class))
        );
    }

    @Test
    void getMissingUserInputWhenUserPersonalDataNotValid(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder()
                .personalDataState(PersonalDataState.MISSING_OR_STALE)
                .offersState(OffersState.MISSING_OR_STALE)
                .build());
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(offerRequest.getUserUUID());
        userStatesStore.setUserPersonalInformationStateDetails(StateDetails.builder().requestDateTime(OffsetDateTime.now()).build());
        UserPersonalData userPersonalData = new UserPersonalData();
        when(userStateService.saveUserPersonalDataState(eq(offerRequest.getUserUUID()), any(StateDetails.class))).thenReturn(Mono.just(userStatesStore));
        when(userStateService.saveUserStates(any(UserStatesStore.class))).thenReturn(Mono.just(userStatesStore));
        when(userPersonalDataAggregator.getUserPersonalData(offerRequest.getUserUUID())).thenReturn(Mono.just(userPersonalData));
        when(offersConfigurations.getNewImplementationEnabled()).thenReturn(false);
        when(userPersonalDataStorageService.saveUserPersonalData(userPersonalData)).thenReturn(Mono.just(userPersonalData));
        when(userPersonalDataValidator.test(userPersonalData)).thenReturn(false);

        // when
        var triggered = integrationHandler.triggerMutation(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(triggered).expectNextCount(0).verifyComplete(),
                () -> verify(userStateService).saveUserPersonalDataState(eq(offerRequest.getUserUUID()), any(StateDetails.class)),
                () -> verify(userStateService).saveUserStates(userStatesStore),
                () -> assertEquals(Status.MISSING_USER_INPUT, userStatesStore.getUserPersonalInformationStateDetails().getState()),
                () -> verify(userPersonalDataAggregator).getUserPersonalData(offerRequest.getUserUUID()),
                () -> verify(userPersonalDataValidator).test(any(UserPersonalData.class)),
                () -> verify(offersConfigurations).getNewImplementationEnabled(),
                () -> verify(userPersonalDataStorageService).saveUserPersonalData(userPersonalData),
                () -> verify(offersStateMetric).addPersonalInfoTimer(eq(offerRequest.getUserUUID()), any(OffsetDateTime.class)),
                () -> verifyNoMoreInteractions(userPersonalDataStorageService)
        );
    }

    @Test
    void getMissingUserInputWhenUserPersonalDataNotValidSaveDraftData(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder()
                .personalDataState(PersonalDataState.MISSING_OR_STALE)
                .offersState(OffersState.MISSING_OR_STALE)
                .build());
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(offerRequest.getUserUUID());
        userStatesStore.setUserPersonalInformationStateDetails(StateDetails.builder().requestDateTime(OffsetDateTime.now()).build());
        UserPersonalData userPersonalData = new UserPersonalData();
        when(userStateService.saveUserPersonalDataState(eq(offerRequest.getUserUUID()), any(StateDetails.class))).thenReturn(Mono.just(userStatesStore));
        when(userStateService.saveUserStates(any(UserStatesStore.class))).thenReturn(Mono.just(userStatesStore));
        when(userPersonalDataAggregator.getUserPersonalData(offerRequest.getUserUUID())).thenReturn(Mono.just(userPersonalData));
        when(offersConfigurations.getNewImplementationEnabled()).thenReturn(true);
        when(userPersonalDataStorageService.saveUserPersonalDataInDraft(userPersonalData)).thenReturn(Mono.just(userPersonalData));
        when(userPersonalDataValidator.test(userPersonalData)).thenReturn(false);

        // when
        var triggered = integrationHandler.triggerMutation(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(triggered).expectNextCount(0).verifyComplete(),
                () -> verify(userStateService).saveUserPersonalDataState(eq(offerRequest.getUserUUID()), any(StateDetails.class)),
                () -> verify(userStateService).saveUserStates(userStatesStore),
                () -> assertEquals(Status.MISSING_USER_INPUT, userStatesStore.getUserPersonalInformationStateDetails().getState()),
                () -> verify(userPersonalDataAggregator).getUserPersonalData(offerRequest.getUserUUID()),
                () -> verify(userPersonalDataValidator).test(any(UserPersonalData.class)),
                () -> verify(offersConfigurations).getNewImplementationEnabled(),
                () -> verify(userPersonalDataStorageService).saveUserPersonalDataInDraft(userPersonalData),
                () -> verify(offersStateMetric).addPersonalInfoTimer(eq(offerRequest.getUserUUID()), any(OffsetDateTime.class)),
                () -> verifyNoMoreInteractions(userPersonalDataStorageService)
        );
    }

    @Test
    void getSuccessfulUserPersonalData(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder()
                .personalDataState(PersonalDataState.MISSING_OR_STALE)
                .offersState(OffersState.MISSING_OR_STALE)
                .build());
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(offerRequest.getUserUUID());
        userStatesStore.setUserPersonalInformationStateDetails(StateDetails.builder().requestDateTime(OffsetDateTime.now()).build());
        UserPersonalData userPersonalData = new UserPersonalData();
        userPersonalData.setBonifyUserId(9385938L);
        userPersonalData.setDistributionChannel(DistributionChannel.BONIFY);
        userPersonalData.setVerifiedViaBankAccount(true);
        userPersonalData.setTenantId(UUID.randomUUID().toString());
        when(userStateService.saveUserPersonalDataState(eq(offerRequest.getUserUUID()), any(StateDetails.class))).thenReturn(Mono.just(userStatesStore));
        when(userStateService.saveUserStates(any(UserStatesStore.class))).thenReturn(Mono.just(userStatesStore));
        when(userPersonalDataAggregator.getUserPersonalData(offerRequest.getUserUUID())).thenReturn(Mono.just(userPersonalData));
        when(offersConfigurations.getNewImplementationEnabled()).thenReturn(false);
        when(userPersonalDataStorageService.saveUserPersonalData(userPersonalData)).thenReturn(Mono.just(userPersonalData));
        when(userPersonalDataValidator.test(userPersonalData)).thenReturn(true);

        // when
        var triggered = integrationHandler.triggerMutation(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(triggered).expectNextCount(0).verifyComplete(),
                () -> verify(userStateService).saveUserPersonalDataState(eq(offerRequest.getUserUUID()), any(StateDetails.class)),
                () -> verify(userStateService).saveUserStates(userStatesStore),
                () -> assertEquals(Status.SUCCESS, userStatesStore.getUserPersonalInformationStateDetails().getState()),
                () -> assertTrue(userStatesStore.getTransactionalDataStateDetails().getUserVerifiedByBankAccount()),
                () -> assertEquals(9385938L, userStatesStore.getBonifyUserId()),
                () -> assertEquals(userPersonalData.getTenantId(), userStatesStore.getTenantId()),
                () -> assertEquals(DistributionChannel.BONIFY, userStatesStore.getDistributionChannel()),
                () -> verify(userPersonalDataAggregator).getUserPersonalData(offerRequest.getUserUUID()),
                () -> verify(userPersonalDataValidator).test(any(UserPersonalData.class)),
                () -> verify(offersConfigurations).getNewImplementationEnabled(),
                () -> verify(userPersonalDataStorageService).saveUserPersonalData(userPersonalData),
                () -> verify(offersStateMetric).addPersonalInfoTimer(eq(offerRequest.getUserUUID()), any(OffsetDateTime.class))
        );
    }
}
