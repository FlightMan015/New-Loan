package de.joonko.loan.integrations.domain.integrationhandler.userpersonal;

import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider.UserPersonalDataMapper;
import de.joonko.loan.user.service.UserAdditionalInfoService;
import de.joonko.loan.user.service.UserAdditionalInformationStore;
import de.joonko.loan.user.service.UserPersonalInfoService;
import de.joonko.loan.user.service.UserPersonalInformationStore;
import de.joonko.loan.userdata.api.mapper.UserDataMapper;
import de.joonko.loan.userdata.domain.model.UserData;
import de.joonko.loan.userdata.infrastructure.draft.UserDataDraftStorageService;
import de.joonko.loan.userdata.infrastructure.draft.model.UserDraftInformationStore;
import de.joonko.loan.userdata.infrastructure.draft.repository.UserDraftInformationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserPersonalDataStorageService {

    private final UserPersonalInfoService userPersonalInfoService;
    private final UserAdditionalInfoService userAdditionalInfoService;

    private final UserPersonalDataMapper userPersonalDataMapper;

    private final UserDataDraftStorageService userDataDraftStorageService;

    private final UserDataMapper userDataMapper;

    public Mono<UserPersonalData> saveUserPersonalData(UserPersonalData userPersonalData) {
        return saveUserAdditionalInfo(userPersonalData)
                .then(saveUserPersonalInfo(userPersonalData))
                .doOnNext(any -> log.debug("Successfully saved user personal data to storage for userId: {}", userPersonalData.getUserUuid()))
                .doOnError(any -> log.error("Failed saving user personal data to storage for userId: {}", userPersonalData.getUserUuid()))
                .thenReturn(userPersonalData);
    }

    public Mono<UserPersonalData> saveUserPersonalDataInDraft(UserPersonalData userPersonalData) {
        return saveUserDraftData(userPersonalData)
                .flatMap(userData -> saveUserAdditionalInfo(userPersonalData))
                .doOnNext(any -> log.debug("Successfully saved user personal draft data to storage for userId: {}", userPersonalData.getUserUuid()))
                .doOnError(any -> log.error("Failed saving user personal draft data to storage for userId: {}", userPersonalData.getUserUuid()))
                .thenReturn(userPersonalData);
    }

    private Mono<UserAdditionalInformationStore> saveUserAdditionalInfo(UserPersonalData userPersonalData) {
        return Mono.just(userPersonalData.getUserUuid())
                .flatMap(userAdditionalInfoService::findById)
                .switchIfEmpty(Mono.just(new UserAdditionalInformationStore()))
                .map(existingAdditionalInfo -> userPersonalDataMapper.toUserAdditionalInfo(userPersonalData, existingAdditionalInfo.getConsentData(), existingAdditionalInfo.getCreditDetails()))
                .flatMap(userAdditionalInfoService::save);
    }

    private Mono<UserData> saveUserDraftData(UserPersonalData userPersonalData) {
        return Mono.just(userDataMapper.fromUserPersonalData(userPersonalData))
                .flatMap(userData -> userDataDraftStorageService.save(userPersonalData.getUserUuid(), userData));
    }

    private Mono<UserPersonalInformationStore> saveUserPersonalInfo(UserPersonalData userPersonalData) {
        return Mono.just(userPersonalData)
                .map(userPersonalDataMapper::toUserPersonalInfo)
                .flatMap(userPersonalInfoService::save);
    }

    public Mono<UserPersonalData> getUserPersonalData(@NotNull final String userUuid) {
        return Mono.just(userUuid)
                .flatMap(userAdditionalInfoService::findById)
                .map(userPersonalDataMapper::fromUserAdditionalInfo);
    }
}
