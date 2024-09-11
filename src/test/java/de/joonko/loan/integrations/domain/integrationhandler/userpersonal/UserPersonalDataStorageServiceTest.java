package de.joonko.loan.integrations.domain.integrationhandler.userpersonal;

import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider.UserPersonalDataMapper;
import de.joonko.loan.offer.api.CreditDetails;
import de.joonko.loan.user.service.UserAdditionalInfoService;
import de.joonko.loan.user.service.UserAdditionalInformationStore;
import de.joonko.loan.user.service.UserPersonalInfoService;
import de.joonko.loan.user.service.UserPersonalInformationStore;
import de.joonko.loan.userdata.api.mapper.UserDataMapper;
import de.joonko.loan.userdata.domain.model.UserData;
import de.joonko.loan.userdata.infrastructure.draft.UserDataDraftStorageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(RandomBeansExtension.class)
class UserPersonalDataStorageServiceTest {

    private UserPersonalDataStorageService userPersonalDataStorageService;

    private UserPersonalInfoService userPersonalInfoService;
    private UserAdditionalInfoService userAdditionalInfoService;

    private UserPersonalDataMapper userPersonalDataMapper;

    private UserDataDraftStorageService userDataDraftStorageService;
    private UserDataMapper userDataMapper;

    private static final String USER_ID = "2f20a660-f0f2-4ca5-9fe6-b24b52cd1070";

    @BeforeEach
    void setUp() {
        userPersonalInfoService = mock(UserPersonalInfoService.class);
        userAdditionalInfoService = mock(UserAdditionalInfoService.class);
        userPersonalDataMapper = mock(UserPersonalDataMapper.class);
        userDataDraftStorageService = mock(UserDataDraftStorageService.class);
        userDataMapper = mock(UserDataMapper.class);

        userPersonalDataStorageService = new UserPersonalDataStorageService(userPersonalInfoService, userAdditionalInfoService, userPersonalDataMapper, userDataDraftStorageService, userDataMapper);
    }

    @Test
    void saveAdditionalDataAndPersonalData() {
        // given
        UserPersonalData userPersonalData = new UserPersonalData();
        userPersonalData.setUserUuid(USER_ID);
        UserPersonalInformationStore userPersonalInformationStore = new UserPersonalInformationStore();
        UserAdditionalInformationStore userAdditionalInformationStore = new UserAdditionalInformationStore();
        userAdditionalInformationStore.setCreditDetails(new CreditDetails());

        when(userPersonalDataMapper.toUserAdditionalInfo(eq(userPersonalData), any(List.class), any(CreditDetails.class))).thenReturn(userAdditionalInformationStore);
        when(userAdditionalInfoService.findById(USER_ID)).thenReturn(Mono.just(userAdditionalInformationStore));
        when(userAdditionalInfoService.save(userAdditionalInformationStore)).thenReturn(Mono.just(userAdditionalInformationStore));
        when(userPersonalDataMapper.toUserPersonalInfo(userPersonalData)).thenReturn(userPersonalInformationStore);
        when(userPersonalInfoService.save(userPersonalInformationStore)).thenReturn(Mono.just(userPersonalInformationStore));

        // when
        var monoUserPersonalData = userPersonalDataStorageService.saveUserPersonalData(userPersonalData);

        // then
        assertAll(
                () -> StepVerifier.create(monoUserPersonalData).expectNextCount(1).verifyComplete(),
                () -> verify(userAdditionalInfoService).save(any(UserAdditionalInformationStore.class)),
                () -> verify(userPersonalInfoService).save(any(UserPersonalInformationStore.class))
        );
    }

    @Test
    void throwErrorWhenFailedSavingAdditionalData() {
        // given
        UserPersonalData userPersonalData = new UserPersonalData();
        userPersonalData.setUserUuid(USER_ID);
        UserAdditionalInformationStore userAdditionalInformationStore = new UserAdditionalInformationStore();

        when(userAdditionalInfoService.findById(USER_ID)).thenReturn(Mono.empty());
        when(userPersonalDataMapper.toUserAdditionalInfo(eq(userPersonalData), any(List.class), any())).thenReturn(userAdditionalInformationStore);
        when(userAdditionalInfoService.save(userAdditionalInformationStore)).thenReturn(Mono.error(new RuntimeException("Failed saving user additional info")));

        // when
        var monoUserPersonalData = userPersonalDataStorageService.saveUserPersonalData(userPersonalData);

        // then
        assertAll(
                () -> StepVerifier.create(monoUserPersonalData).verifyError(),
                () -> verify(userAdditionalInfoService).save(any(UserAdditionalInformationStore.class)),
                () -> verifyNoInteractions(userPersonalInfoService)
        );
    }

    @Test
    void throwErrorWhenFailedSavingPersonalInfo() {
        // given
        UserPersonalData userPersonalData = new UserPersonalData();
        userPersonalData.setUserUuid(USER_ID);
        UserPersonalInformationStore userPersonalInformationStore = new UserPersonalInformationStore();
        UserAdditionalInformationStore userAdditionalInformationStore = new UserAdditionalInformationStore();

        when(userAdditionalInfoService.findById(USER_ID)).thenReturn(Mono.empty());
        when(userPersonalDataMapper.toUserAdditionalInfo(eq(userPersonalData), any(List.class), any())).thenReturn(userAdditionalInformationStore);
        when(userAdditionalInfoService.save(userAdditionalInformationStore)).thenReturn(Mono.just(userAdditionalInformationStore));
        when(userPersonalDataMapper.toUserPersonalInfo(userPersonalData)).thenReturn(userPersonalInformationStore);
        when(userPersonalInfoService.save(userPersonalInformationStore)).thenReturn(Mono.error(new RuntimeException("Failed saving user personal info")));

        // when
        var monoUserPersonalData = userPersonalDataStorageService.saveUserPersonalData(userPersonalData);

        // then
        assertAll(
                () -> StepVerifier.create(monoUserPersonalData).verifyError(),
                () -> verify(userAdditionalInfoService).save(any(UserAdditionalInformationStore.class)),
                () -> verify(userPersonalInfoService).save(any(UserPersonalInformationStore.class))
        );
    }


    @Test
    void saveUserPersonalDataInDraft(@Random UserPersonalData userPersonalData) {
        // given
        userPersonalData.setUserUuid(USER_ID);
        final var userData = new UserData();
        final var userAdditionalInformationStore = new UserAdditionalInformationStore();
        userAdditionalInformationStore.setCreditDetails(CreditDetails.builder().build());

        when(userDataMapper.fromUserPersonalData(userPersonalData)).thenReturn(userData);
        when(userDataDraftStorageService.save(userPersonalData.getUserUuid(), userData)).thenReturn(Mono.just(userData));
        when(userPersonalDataMapper.toUserAdditionalInfo(eq(userPersonalData), any(List.class), any(CreditDetails.class))).thenReturn(userAdditionalInformationStore);
        when(userAdditionalInfoService.findById(USER_ID)).thenReturn(Mono.just(userAdditionalInformationStore));
        when(userAdditionalInfoService.save(userAdditionalInformationStore)).thenReturn(Mono.just(userAdditionalInformationStore));

        // when
        var monoUserPersonalData = userPersonalDataStorageService.saveUserPersonalDataInDraft(userPersonalData);

        // then
        assertAll(
                () -> StepVerifier.create(monoUserPersonalData).expectNextCount(1).verifyComplete(),
                () -> verify(userDataMapper).fromUserPersonalData(userPersonalData),
                () -> verify(userDataDraftStorageService).save(userPersonalData.getUserUuid(), userData),
                () -> verify(userPersonalDataMapper).toUserAdditionalInfo(eq(userPersonalData), any(List.class), any(CreditDetails.class)),
                () -> verify(userAdditionalInfoService).findById(USER_ID),
                () -> verify(userAdditionalInfoService).save(userAdditionalInformationStore),
                () -> verifyNoMoreInteractions(userDataMapper, userDataDraftStorageService)
        );
    }
}
