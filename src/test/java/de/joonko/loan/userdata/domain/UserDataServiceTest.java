package de.joonko.loan.userdata.domain;

import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStoreService;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalData;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalDataStorageService;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalDataValidator;
import de.joonko.loan.user.UserDataNotFoundException;
import de.joonko.loan.user.states.StateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;
import de.joonko.loan.userdata.api.mapper.UserDataMapper;
import de.joonko.loan.userdata.domain.draft.UserDataDraftProvider;
import de.joonko.loan.userdata.domain.model.UserData;
import de.joonko.loan.userdata.domain.validator.UserDataValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

class UserDataServiceTest {

    private UserDataService userDataService;

    private UserPersonalDataStorageService userPersonalDataStorageService;
    private UserDataValidator userDataValidator;
    private UserPersonalDataValidator userPersonalDataValidator;
    private UserDataMapper userDataMapper;
    private UserDataDraftProvider userDataDraftProvider;
    private UserTransactionalDataStoreService userTransactionalDataStoreService;
    private UserStatesStoreService userStatesStoreService;

    public static final String USER_UUID = "64c5d31d-6d4f-449d-a4fe-922bb345412b";

    @BeforeEach
    void setUp() {
        userPersonalDataStorageService = mock(UserPersonalDataStorageService.class);
        userDataValidator = mock(UserDataValidator.class);
        userPersonalDataValidator = mock(UserPersonalDataValidator.class);
        userDataMapper = mock(UserDataMapper.class);
        userDataDraftProvider = mock(UserDataDraftProvider.class);
        userTransactionalDataStoreService = mock(UserTransactionalDataStoreService.class);
        userStatesStoreService = mock(UserStatesStoreService.class);

        userDataService = new UserDataService(userPersonalDataStorageService, userDataValidator, userPersonalDataValidator, userDataMapper,
                userDataDraftProvider, userTransactionalDataStoreService, userStatesStoreService);
    }

    @Test
    void getErrorWhenMissingUserDataAndUserDraftData() {
        // given
        when(userPersonalDataStorageService.getUserPersonalData(USER_UUID)).thenReturn(Mono.empty());
        when(userDataDraftProvider.get(USER_UUID)).thenReturn(Mono.error(new UserDataNotFoundException("Missing draft user data with userUuid: " + USER_UUID)));

        // when
        var monoUserData = userDataService.get(USER_UUID);


        // then
        StepVerifier.create(monoUserData).verifyError(UserDataNotFoundException.class);
    }

    @Test
    void getUserDraftDataAndMissingUserAccount() {
        // given
        final var userDraftData = new UserData();
        final var transactionalData = new UserTransactionalDataStore();
        when(userPersonalDataStorageService.getUserPersonalData(USER_UUID)).thenReturn(Mono.empty());
        when(userDataDraftProvider.get(USER_UUID)).thenReturn(Mono.just(userDraftData));
        when(userDataValidator.validateAndGet(userDraftData)).thenReturn(userDraftData);
        when(userTransactionalDataStoreService.getById(USER_UUID)).thenReturn(Mono.empty());
        when(userDataMapper.merge(userDraftData, transactionalData)).thenReturn(userDraftData);

        // when
        var monoUserData = userDataService.get(USER_UUID);


        // then
        assertAll(
                () -> StepVerifier.create(monoUserData).expectNextCount(1).verifyComplete(),
                () -> verify(userPersonalDataStorageService).getUserPersonalData(USER_UUID),
                () -> verify(userDataDraftProvider).get(USER_UUID),
                () -> verify(userTransactionalDataStoreService).getById(USER_UUID)
        );
    }

    @Test
    void getUserDraftDataAndUserAccount() {
        // given
        final var userDraftData = new UserData();
        final var transactionalData = new UserTransactionalDataStore();
        when(userPersonalDataStorageService.getUserPersonalData(USER_UUID)).thenReturn(Mono.empty());
        when(userDataDraftProvider.get(USER_UUID)).thenReturn(Mono.just(userDraftData));
        when(userDataValidator.validateAndGet(userDraftData)).thenReturn(userDraftData);
        when(userTransactionalDataStoreService.getById(USER_UUID)).thenReturn(Mono.just(transactionalData));
        when(userDataMapper.merge(userDraftData, transactionalData)).thenReturn(userDraftData);

        // when
        var monoUserData = userDataService.get(USER_UUID);


        // then
        assertAll(
                () -> StepVerifier.create(monoUserData).expectNextCount(1).verifyComplete(),
                () -> verify(userPersonalDataStorageService).getUserPersonalData(USER_UUID),
                () -> verify(userDataDraftProvider).get(USER_UUID),
                () -> verify(userTransactionalDataStoreService).getById(USER_UUID)
        );
    }

    @Test
    void getValidUserData() {
        // given
        final var userPersonalData = new UserPersonalData();
        final var userData = new UserData();
        final var transactionalData = new UserTransactionalDataStore();
        when(userPersonalDataStorageService.getUserPersonalData(USER_UUID)).thenReturn(Mono.just(userPersonalData));
        when(userPersonalDataValidator.test(userPersonalData)).thenReturn(true);
        when(userDataMapper.fromValidUserPersonalData(userPersonalData)).thenReturn(new UserData());
        when(userTransactionalDataStoreService.getById(USER_UUID)).thenReturn(Mono.just(transactionalData));
        when(userDataMapper.merge(userData, transactionalData)).thenReturn(userData);

        // when
        var monoUserData = userDataService.get(USER_UUID);


        // then
        assertAll(
                () -> StepVerifier.create(monoUserData).expectNextCount(1).verifyComplete(),
                () -> verify(userPersonalDataStorageService).getUserPersonalData(USER_UUID),
                () -> verifyNoInteractions(userDataDraftProvider),
                () -> verify(userTransactionalDataStoreService).getById(USER_UUID)
        );
    }

    @Test
    void getDraftUserDataWhenCurrentIsInvalid() {
        // given
        final var userPersonalData = new UserPersonalData();
        final var transactionalData = new UserTransactionalDataStore();
        final var userDraftData = new UserData();
        when(userPersonalDataStorageService.getUserPersonalData(USER_UUID)).thenReturn(Mono.just(userPersonalData));
        when(userPersonalDataValidator.test(userPersonalData)).thenReturn(false);
        when(userDataDraftProvider.get(USER_UUID)).thenReturn(Mono.just(userDraftData));
        when(userDataValidator.validateAndGet(userDraftData)).thenReturn(userDraftData);
        when(userTransactionalDataStoreService.getById(USER_UUID)).thenReturn(Mono.just(transactionalData));
        when(userDataMapper.merge(userDraftData, transactionalData)).thenReturn(userDraftData);

        // when
        var monoUserData = userDataService.get(USER_UUID);


        // then
        assertAll(
                () -> StepVerifier.create(monoUserData).expectNextCount(1).verifyComplete(),
                () -> verify(userPersonalDataStorageService).getUserPersonalData(USER_UUID),
                () -> verify(userDataDraftProvider).get(USER_UUID),
                () -> verify(userTransactionalDataStoreService).getById(USER_UUID)
        );
    }

    @Test
    void updateInvalidUserData() {
        // given
        final var userData = new UserData();
        final var userPersonalData = new UserPersonalData();
        when(userDataMapper.toUserPersonalData(userData, USER_UUID)).thenReturn(userPersonalData);
        when(userPersonalDataValidator.test(userPersonalData)).thenReturn(false);
        when(userDataDraftProvider.save(USER_UUID, userData)).thenReturn(Mono.just(userData));

        // when
        var monoUserData = userDataService.update(USER_UUID, userData);


        // then
        assertAll(
                () -> StepVerifier.create(monoUserData).expectNextCount(1).verifyComplete(),
                () -> verifyNoInteractions(userStatesStoreService),
                () -> verify(userDataDraftProvider).save(USER_UUID, userData)
        );
    }

    @Test
    void updateValidUserData() {
        // given
        final var userData = new UserData();
        final var userPersonalData = new UserPersonalData();
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setUserPersonalInformationStateDetails(StateDetails.builder().build());
        when(userDataMapper.toUserPersonalData(userData, USER_UUID)).thenReturn(userPersonalData);
        when(userPersonalDataValidator.test(userPersonalData)).thenReturn(true);
        when(userPersonalDataStorageService.saveUserPersonalData(userPersonalData)).thenReturn(Mono.just(userPersonalData));
        when(userStatesStoreService.findById(USER_UUID)).thenReturn(Mono.just(userStatesStore));
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));
        when(userDataDraftProvider.save(USER_UUID, userData)).thenReturn(Mono.just(userData));

        // when
        var monoUserData = userDataService.update(USER_UUID, userData);


        // then
        assertAll(
                () -> StepVerifier.create(monoUserData).expectNextCount(1).verifyComplete(),
                () -> verify(userStatesStoreService).save(userStatesStore),
                () -> verify(userDataDraftProvider).save(USER_UUID, userData)
        );
    }
}
