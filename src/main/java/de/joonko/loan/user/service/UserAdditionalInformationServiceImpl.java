package de.joonko.loan.user.service;

import de.joonko.loan.common.CollectionUtil;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalDataStorageService;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper.UserPersonalInformationMapper;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider.UserManagementService;
import de.joonko.loan.offer.api.model.UserPersonalDetails;
import de.joonko.loan.user.api.model.Consent;
import de.joonko.loan.user.service.mapper.ConsentMapper;
import de.joonko.loan.user.service.persistence.domain.ConsentData;
import de.joonko.loan.user.service.persistence.domain.ConsentState;
import de.joonko.loan.user.service.persistence.domain.ConsentType;
import de.joonko.loan.user.states.Status;
import de.joonko.loan.user.states.UserStatesStoreService;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAdditionalInformationServiceImpl implements UserAdditionalInformationService {

    private final UserAdditionalInformationRepository userAdditionalInformationRepository;

    private final UserPersonalInformationMapper userPersonalInformationMapper;

    private final UserPersonalDataStorageService userPersonalDataStorageService;

    private final UserStatesStoreService userStatesStoreService;

    private final UserPersonalInfoService userPersonalInfoService;

    private final UserManagementService userManagementService;

    private final UserDeletionService userDeletionService;

    @Override
    public Mono<Optional<UserAdditionalInformationStore>> findById(String userUUID) {
        return Mono.fromCallable(() -> userAdditionalInformationRepository.findById(userUUID))
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<Void> handleUserInput(String userUUID, UserPersonalDetails userPersonalDetails) {
        log.info("Received user input for update of user information for user {}", userUUID);
        return Mono.just(userUUID)
                .map(userId -> userPersonalInformationMapper.mapFromUserInput(userUUID, userPersonalDetails))
                .flatMap(userPersonalDataStorageService::saveUserPersonalData)
                .flatMap(ignored -> userStatesStoreService.findById(userUUID))
                .map(userStatesStore -> {
                    userStatesStore.getUserPersonalInformationStateDetails().setState(Status.SUCCESS);
                    userStatesStore.getUserPersonalInformationStateDetails().setResponseDateTime(OffsetDateTime.now());
                    userStatesStore.getUserPersonalInformationStateDetails().setAdditionalFieldsForHighAmountAdded(userPersonalDetails.additionalFieldsForHighAmountArePresent());

                    return userStatesStore;
                })
                .flatMap(userStatesStoreService::save)
                .flatMap(ignored -> userPersonalInfoService.findById(userUUID))
                .flatMap(userManagementService::updateUserPersonalData);
    }

    @Override
    public Mono<Void> deleteUserData(final String userUUID) {
        return userDeletionService.deleteUser(userUUID);
    }

    @Override
    public List<Consent> saveConsents(final String userUUID, final List<Consent> consentList, final String clientIP) {
        final var newConsents = CollectionUtil.<Consent, ConsentData>mapList(consent -> ConsentMapper.map(consent, clientIP)).apply(consentList);

        final var userAdditionalInformation = userAdditionalInformationRepository.findById(userUUID)
                .orElseGet(() -> {
                    UserAdditionalInformationStore userAdditionalInformationStore = new UserAdditionalInformationStore();
                    userAdditionalInformationStore.setUserUUID(userUUID);
                    return userAdditionalInformationStore;
                });

        final List<ConsentData> consentsForSave = constructConsent(userAdditionalInformation.getConsentData(), newConsents);

        userAdditionalInformation.setConsentData(consentsForSave);
        final var updatedUserAdditionalInformation = userAdditionalInformationRepository.save(userAdditionalInformation);

        return CollectionUtil.<ConsentData, Consent>mapList(ConsentMapper::map).apply(updatedUserAdditionalInformation.getConsentData());
    }

    @Override
    public List<Consent> getUserConsents(final String userUUID) {
        final var existingConsents = userAdditionalInformationRepository.findById(userUUID)
                .map(UserAdditionalInformationStore::getConsentData)
                .orElseGet(List::of);

        final var allConsents = Arrays.stream(ConsentType.values()).map(type ->
                existingConsents.stream().filter(existingConsent -> existingConsent.getConsentType().equals(type))
                        .findFirst()
                        .orElse(ConsentData.builder()
                                .consentType(type)
                                .consentState(ConsentState.NONE)
                                .build())
        ).collect(toList());

        return CollectionUtil.<ConsentData, Consent>mapList(ConsentMapper::map).apply(allConsents);
    }


    private List<ConsentData> constructConsent(List<ConsentData> existingConsents, List<ConsentData> consents) {
        return Stream.concat(
                consents.stream().map(consent ->
                        ConsentData.builder()
                                .consentState(consent.getConsentState())
                                .consentType(consent.getConsentType())
                                .lastUpdatedTimestamp(
                                        existingConsents.stream()
                                                .filter(existingConsent -> existingConsent.getConsentType().equals(consent.getConsentType())
                                                        && existingConsent.getConsentState().equals(consent.getConsentState()))
                                                .findFirst()
                                                .map(ConsentData::getLastUpdatedTimestamp)
                                                .orElseGet(Instant::now)
                                )
                                .clientIP(
                                        existingConsents.stream()
                                                .filter(existingConsent -> existingConsent.getConsentType().equals(consent.getConsentType())
                                                        && existingConsent.getConsentState().equals(consent.getConsentState()))
                                                .findFirst()
                                                .map(ConsentData::getClientIP)
                                                .orElseGet(consent::getClientIP)
                                )
                                .build()
                ), existingConsents.stream().filter(existingConsent ->
                        consents.stream()
                                .noneMatch(consent -> consent.getConsentType().equals(existingConsent.getConsentType())))
        ).collect(toList());
    }

    @Override
    public Mono<List<UserAdditionalInformationStore>> removeFtsData(List<String> userUuids) {
        return Mono.fromCallable(() -> userAdditionalInformationRepository.findAllById(userUuids))
                .map(userAdditional -> {
                    userAdditional.forEach(draft -> {
                        draft.setEmploymentDetails(null);
                        draft.setCreditDetails(null);
                        draft.setExpenses(null);
                        draft.setIncome(null);
                    });
                    return userAdditional;
                })
                .flatMap(userDrafts -> Mono.fromCallable(() -> userAdditionalInformationRepository.saveAll(userDrafts)))
                .flatMapIterable(list -> list)
                .collectList()
                .subscribeOn(Schedulers.elastic());
    }
}
