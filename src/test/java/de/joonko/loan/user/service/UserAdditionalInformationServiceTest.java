package de.joonko.loan.user.service;

import de.joonko.loan.common.CollectionUtil;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalData;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalDataStorageService;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper.UserPersonalInformationMapper;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider.UserManagementService;
import de.joonko.loan.offer.api.CreditDetails;
import de.joonko.loan.offer.api.Expenses;
import de.joonko.loan.offer.api.Income;
import de.joonko.loan.offer.api.model.UserPersonalDetails;
import de.joonko.loan.user.api.model.Consent;
import de.joonko.loan.user.api.model.ConsentApiState;
import de.joonko.loan.user.api.model.ConsentApiType;
import de.joonko.loan.user.service.mapper.ConsentMapper;
import de.joonko.loan.user.service.persistence.domain.ConsentData;
import de.joonko.loan.user.states.StateDetails;
import de.joonko.loan.user.states.Status;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(RandomBeansExtension.class)
public class UserAdditionalInformationServiceTest {

    private final static String IP = "192.168.0.104";

    private UserAdditionalInformationService userAdditionalInformationService;

    private UserAdditionalInformationRepository userAdditionalInformationRepository;

    private UserPersonalInformationMapper userPersonalInformationMapper;

    private UserManagementService userManagementService;

    private UserPersonalDataStorageService userPersonalDataStorageService;

    private UserStatesStoreService userStatesStoreService;

    private UserPersonalInfoService userPersonalInfoService;

    private UserDeletionService userDeletionService;

    @BeforeEach
    void setUp() {
        userAdditionalInformationRepository = mock(UserAdditionalInformationRepository.class);
        userPersonalInformationMapper = mock(UserPersonalInformationMapper.class);
        userManagementService = mock(UserManagementService.class);
        userPersonalDataStorageService = mock(UserPersonalDataStorageService.class);
        userStatesStoreService = mock(UserStatesStoreService.class);
        userPersonalInfoService = mock(UserPersonalInfoService.class);
        userDeletionService = mock(UserDeletionService.class);

        userAdditionalInformationService = new UserAdditionalInformationServiceImpl(userAdditionalInformationRepository, userPersonalInformationMapper,
                userPersonalDataStorageService, userStatesStoreService, userPersonalInfoService, userManagementService, userDeletionService);
    }

    @Test
    void handleUserInput(@Random String userUUID, @Random UserPersonalDetails userPersonalDetails) {
        // given
        userPersonalDetails.setCreditDetails(null);
        final var userPersonalData = UserPersonalData.builder().build();
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setUserPersonalInformationStateDetails(StateDetails.builder().build());
        final var userPersonalInformationStore = new UserPersonalInformationStore();
        ArgumentCaptor<UserStatesStore> argument = ArgumentCaptor.forClass(UserStatesStore.class);

        // when
        when(userPersonalInformationMapper.mapFromUserInput(userUUID, userPersonalDetails)).thenReturn(userPersonalData);
        when(userPersonalDataStorageService.saveUserPersonalData(userPersonalData)).thenReturn(Mono.just(userPersonalData));
        when(userStatesStoreService.findById(userUUID)).thenReturn(Mono.just(userStatesStore));
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));
        when(userPersonalInfoService.findById(userUUID)).thenReturn(Mono.just(userPersonalInformationStore));
        when(userManagementService.updateUserPersonalData(userPersonalInformationStore)).thenReturn(Mono.empty());

        var triggered = userAdditionalInformationService.handleUserInput(userUUID, userPersonalDetails);

        assertAll(
                () -> StepVerifier.create(triggered).expectNextCount(0).verifyComplete(),
                () -> verify(userPersonalInformationMapper).mapFromUserInput(userUUID, userPersonalDetails),
                () -> verify(userPersonalDataStorageService).saveUserPersonalData(userPersonalData),
                () -> verify(userStatesStoreService).findById(userUUID),
                () -> verify(userStatesStoreService).save(argument.capture()),
                () -> verify(userPersonalInfoService).findById(userUUID),
                () -> verify(userManagementService).updateUserPersonalData(userPersonalInformationStore),
                () -> assertEquals(Status.SUCCESS, argument.getValue().getUserPersonalInformationStateDetails().getState()),
                () -> assertNotNull(argument.getValue().getUserPersonalInformationStateDetails().getResponseDateTime()),
                () -> assertFalse(argument.getValue().getUserPersonalInformationStateDetails().getAdditionalFieldsForHighAmountAdded())
        );
    }

    @Test
    void handleUserInputWithAdditionalUserData(@Random String userUUID, @Random UserPersonalDetails userPersonalDetails) {
        // given
        final var userPersonalDetailsUpdated = userPersonalDetails.toBuilder()
                .creditDetails(CreditDetails.builder()
                        .isCurrentDelayInInstallmentsDeclared(false)
                        .creditCardLimitDeclared(100d)
                        .build())
                .income(Income.builder()
                        .incomeDeclared(5000d)
                        .build())
                .expenses(Expenses.builder()
                        .monthlyLifeCost(2000d)
                        .monthlyLoanInstallmentsDeclared(200d)
                        .build())
                .build();

        final var userPersonalData = UserPersonalData.builder().build();
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setUserPersonalInformationStateDetails(StateDetails.builder().build());
        final var userPersonalInformationStore = new UserPersonalInformationStore();
        ArgumentCaptor<UserStatesStore> argument = ArgumentCaptor.forClass(UserStatesStore.class);

        // when
        when(userPersonalInformationMapper.mapFromUserInput(userUUID, userPersonalDetailsUpdated)).thenReturn(userPersonalData);
        when(userPersonalDataStorageService.saveUserPersonalData(userPersonalData)).thenReturn(Mono.just(userPersonalData));
        when(userStatesStoreService.findById(userUUID)).thenReturn(Mono.just(userStatesStore));
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));
        when(userPersonalInfoService.findById(userUUID)).thenReturn(Mono.just(userPersonalInformationStore));
        when(userManagementService.updateUserPersonalData(userPersonalInformationStore)).thenReturn(Mono.empty());

        var triggered = userAdditionalInformationService.handleUserInput(userUUID, userPersonalDetailsUpdated);

        assertAll(
                () -> StepVerifier.create(triggered).expectNextCount(0).verifyComplete(),
                () -> verify(userPersonalInformationMapper).mapFromUserInput(userUUID, userPersonalDetailsUpdated),
                () -> verify(userPersonalDataStorageService).saveUserPersonalData(userPersonalData),
                () -> verify(userStatesStoreService).findById(userUUID),
                () -> verify(userStatesStoreService).save(argument.capture()),
                () -> verify(userPersonalInfoService).findById(userUUID),
                () -> verify(userManagementService).updateUserPersonalData(userPersonalInformationStore),
                () -> assertEquals(Status.SUCCESS, argument.getValue().getUserPersonalInformationStateDetails().getState()),
                () -> assertNotNull(argument.getValue().getUserPersonalInformationStateDetails().getResponseDateTime()),
                () -> assertTrue(argument.getValue().getUserPersonalInformationStateDetails().getAdditionalFieldsForHighAmountAdded())
        );
    }


    @Test
    void save_consent_works_correctly_when_no_user_additional_information_exists() {
        // given
        final var userUUID = randomUUID().toString();
        final var emailConsent = Consent.builder()
                .type(ConsentApiType.EMAIL)
                .consent(ConsentApiState.ACCEPTED)
                .build();
        final var phoneConsent = Consent.builder()
                .type(ConsentApiType.PHONE)
                .consent(ConsentApiState.DECLINED)
                .build();
        final var consents = List.of(emailConsent, phoneConsent);
        final var consentData = CollectionUtil.<Consent, ConsentData>mapList(consent -> ConsentMapper.map(consent, IP)).apply(consents);

        final var userAdditionalInformation = new UserAdditionalInformationStore();
        userAdditionalInformation.setUserUUID(userUUID);
        userAdditionalInformation.setConsentData(consentData);

        // when
        when(userAdditionalInformationRepository.findById(userUUID)).thenReturn(Optional.empty());
        when(userAdditionalInformationRepository.save(any())).thenReturn(userAdditionalInformation);

        final var results = userAdditionalInformationService.saveConsents(userUUID, consents, IP);

        // then
        assertEquals(2, results.size());
        assertEquals(consents, results);
    }

    @Test
    void save_consent_works_correctly_when_no_initial_consent_exists() {
        // given
        final var userUUID = randomUUID().toString();
        final var emailConsent = Consent.builder()
                .type(ConsentApiType.EMAIL)
                .consent(ConsentApiState.ACCEPTED)
                .build();
        final var phoneConsent = Consent.builder()
                .type(ConsentApiType.PHONE)
                .consent(ConsentApiState.DECLINED)
                .build();
        final var consents = List.of(emailConsent, phoneConsent);
        final var consentData = CollectionUtil.<Consent, ConsentData>mapList(consent -> ConsentMapper.map(consent, IP)).apply(consents);

        final var userAdditionalInformation = new UserAdditionalInformationStore();
        userAdditionalInformation.setUserUUID(userUUID);

        final var userInformationAfterUpdate = userAdditionalInformation;
        userInformationAfterUpdate.setConsentData(consentData);

        // when
        when(userAdditionalInformationRepository.findById(userUUID)).thenReturn(Optional.of(userAdditionalInformation));
        when(userAdditionalInformationRepository.save(any())).thenReturn(userInformationAfterUpdate);

        final var results = userAdditionalInformationService.saveConsents(userUUID, consents, IP);

        // then
        assertEquals(2, results.size());
        assertEquals(consents, results);
    }

    @Test
    void save_consent_works_correctly_when_some_initial_consents_exists() {
        // given
        final var userUUID = randomUUID().toString();
        final var emailConsent = Consent.builder()
                .type(ConsentApiType.EMAIL)
                .consent(ConsentApiState.ACCEPTED)
                .build();
        final var phoneConsent = Consent.builder()
                .type(ConsentApiType.PHONE)
                .consent(ConsentApiState.DECLINED)
                .build();
        final var existingEmailConsent = Consent.builder()
                .type(ConsentApiType.EMAIL)
                .consent(ConsentApiState.ACCEPTED)
                .build();
        final var existingPhoneConsent = Consent.builder()
                .type(ConsentApiType.PHONE)
                .consent(ConsentApiState.ACCEPTED)
                .build();
        final var existingSMSConsent = Consent.builder()
                .type(ConsentApiType.SMS)
                .consent(ConsentApiState.REVOKED)
                .build();
        final var consents = List.of(emailConsent, phoneConsent);
        final var existingConsents = List.of(emailConsent, phoneConsent);
        final var consentData = CollectionUtil.<Consent, ConsentData>mapList(consent -> ConsentMapper.map(consent, IP)).apply(existingConsents);

        final var consentsAfterUpdate = List.of(existingEmailConsent, phoneConsent, existingSMSConsent);
        final var consentDataAfterUpdate = CollectionUtil.<Consent, ConsentData>mapList(consent -> ConsentMapper.map(consent, IP)).apply(consentsAfterUpdate);

        final var existingUserAdditionalInformation = new UserAdditionalInformationStore();
        existingUserAdditionalInformation.setUserUUID(userUUID);
        existingUserAdditionalInformation.setConsentData(consentData);

        final var userAdditionalInformation = new UserAdditionalInformationStore();
        userAdditionalInformation.setUserUUID(userUUID);
        userAdditionalInformation.setConsentData(consentDataAfterUpdate);

        // when
        when(userAdditionalInformationRepository.findById(userUUID)).thenReturn(Optional.of(existingUserAdditionalInformation));
        when(userAdditionalInformationRepository.save(any())).thenReturn(userAdditionalInformation);

        final var results = userAdditionalInformationService.saveConsents(userUUID, consents, IP);

        // then
        assertEquals(3, results.size());
        assertEquals(consentsAfterUpdate, results);
    }

    @Test
    void get_consents_works_correctly() {
        // given
        final var userUUID = randomUUID().toString();
        final var emailConsent = Consent.builder()
                .type(ConsentApiType.EMAIL)
                .consent(ConsentApiState.ACCEPTED)
                .build();
        final var phoneConsent = Consent.builder()
                .type(ConsentApiType.PHONE)
                .consent(ConsentApiState.DECLINED)
                .build();
        final var consents = List.of(emailConsent, phoneConsent);
        final var consentData = CollectionUtil.<Consent, ConsentData>mapList(consent -> ConsentMapper.map(consent, IP)).apply(consents);

        final var userAdditionalInformation = new UserAdditionalInformationStore();
        userAdditionalInformation.setUserUUID(userUUID);
        userAdditionalInformation.setConsentData(consentData);
        // when
        when(userAdditionalInformationRepository.findById(userUUID)).thenReturn(Optional.of(userAdditionalInformation));

        final var results = userAdditionalInformationService.getUserConsents(userUUID);

        // then
        assertEquals(4, results.size());
        assertEquals(ConsentApiState.ACCEPTED, results.stream()
                .filter(consent -> consent.getType().equals(ConsentApiType.EMAIL)).map(Consent::getConsent).findFirst().get());
        assertEquals(ConsentApiState.DECLINED, results.stream()
                .filter(consent -> consent.getType().equals(ConsentApiType.PHONE)).map(Consent::getConsent).findFirst().get());
        assertEquals(Set.of(ConsentApiState.NONE), results.stream()
                .filter(consent -> !Set.of(ConsentApiType.EMAIL, ConsentApiType.PHONE).contains(consent.getType()))
                .map(Consent::getConsent)
                .collect(toSet())
        );
    }
}
