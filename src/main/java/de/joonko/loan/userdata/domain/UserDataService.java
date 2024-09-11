package de.joonko.loan.userdata.domain;

import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStoreService;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalDataStorageService;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalDataValidator;
import de.joonko.loan.user.states.Status;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;
import de.joonko.loan.userdata.api.mapper.UserDataMapper;
import de.joonko.loan.userdata.domain.draft.UserDataDraftProvider;
import de.joonko.loan.userdata.domain.model.UserData;
import de.joonko.loan.userdata.domain.validator.UserDataValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserDataService {

    private final UserPersonalDataStorageService userPersonalDataStorageService;
    private final UserDataValidator userDataValidator;
    private final UserPersonalDataValidator userPersonalDataValidator;
    private final UserDataMapper userDataMapper;
    private final UserDataDraftProvider userDataDraftProvider;
    private final UserTransactionalDataStoreService userTransactionalDataStoreService;
    private final UserStatesStoreService userStatesStoreService;

    public Mono<UserData> get(@NotNull final String userUuid) {
        return Mono.zip(getUserData(userUuid), getTransactionalData(userUuid))
                .map(tuple -> userDataMapper.merge(tuple.getT1(), tuple.getT2()));
    }

    public Mono<UserData> update(@NotNull final String userUuid, @NotNull final UserData userData) {
        return Mono.zip(updateUserDataWhenValid(userUuid, userData), updateUserDraftData(userUuid, userData))
                .map(Tuple2::getT2);
    }

    private Mono<UserData> updateUserDataWhenValid(String userUuid, UserData userData) {
        return Mono.just(userUuid)
                .doOnNext(any -> log.debug("Updating user data for userUuid: {}", userUuid))
                .map(userId -> userDataMapper.toUserPersonalData(userData, userId))
                .filter(userPersonalDataValidator)
                .flatMap(userPersonalDataStorageService::saveUserPersonalData)
                .doOnNext(any -> log.debug("Updated valid user data for userUuid: {}", userUuid))
                .flatMap(ignored -> updateUserDataState(userUuid, userData))
                .doOnNext(any -> log.info("Updated user data state for userUuid: {}", userUuid))
                .thenReturn(userData);
    }

    private Mono<UserData> updateUserDraftData(String userUuid, UserData userData) {
        return Mono.just(userUuid)
                .doOnNext(any -> log.debug("Updating draft user data for userUuid: {}", userUuid))
                .flatMap(any -> userDataDraftProvider.save(userUuid, userData))
                .doOnNext(any -> log.info("Updated draft user data for userUuid: {}", userUuid));
    }

    private Mono<UserStatesStore> updateUserDataState(String userUuid, UserData userData) {
        return Mono.just(userUuid)
                .flatMap(ignored -> userStatesStoreService.findById(userUuid))
                .filter(userStatesStore -> userStatesStore.getUserPersonalInformationStateDetails() != null)
                .map(userStatesStore -> {
                    userStatesStore.getUserPersonalInformationStateDetails().setState(Status.SUCCESS);
                    userStatesStore.getUserPersonalInformationStateDetails().setResponseDateTime(OffsetDateTime.now());
                    userStatesStore.getUserPersonalInformationStateDetails().setAdditionalFieldsForHighAmountAdded(userData.additionalFieldsForHighAmountArePresent());

                    return userStatesStore;
                }).flatMap(userStatesStoreService::save);
    }

    private Mono<UserData> getUserData(final String userUuid) {
        return Mono.just(userUuid)
                .flatMap(userPersonalDataStorageService::getUserPersonalData)
                .filter(userPersonalDataValidator)
                .doOnNext(any -> log.info("Found valid user data for userUuid: {}", userUuid))
                .map(userDataMapper::fromValidUserPersonalData)
                .switchIfEmpty(
                        Mono.defer(() -> {
                            log.info("User data is invalid for userUuid: {}, getting user draft data instead", userUuid);
                            return userDataDraftProvider.get(userUuid);
                        }).map(userDataValidator::validateAndGet)
                );
    }

    private Mono<UserTransactionalDataStore> getTransactionalData(final String userUuid) {
        return Mono.just(userUuid)
                .flatMap(userTransactionalDataStoreService::getById)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Bank data not found for userUuid: {}", userUuid);
                    return Mono.just(new UserTransactionalDataStore());
                }));
    }
}
