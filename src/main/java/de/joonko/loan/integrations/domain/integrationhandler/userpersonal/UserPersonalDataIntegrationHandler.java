package de.joonko.loan.integrations.domain.integrationhandler.userpersonal;

import de.joonko.loan.integrations.configuration.GetOffersConfigurations;
import de.joonko.loan.integrations.domain.integrationhandler.IntegrationHandler;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider.UserPersonalDataAggregator;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.metric.OffersStateMetric;
import de.joonko.loan.integrations.model.DistributionChannel;
import de.joonko.loan.user.states.*;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class UserPersonalDataIntegrationHandler implements IntegrationHandler {

    private final UserPersonalDataFilter userPersonalDataFilter;
    private final UserPersonalDataValidator userPersonalDataValidator;
    private final UserPersonalDataStorageService userPersonalDataStorageService;
    private final OffersStateMetric offersStateMetric;
    private final UserStateService userStateService;
    private final UserPersonalDataAggregator userPersonalDataAggregator;
    private final GetOffersConfigurations offersConfigurations;

    @Override
    public Mono<Void> triggerMutation(OfferRequest offerRequest) {
        return Mono.just(offerRequest)
                .filter(userPersonalDataFilter)
                .doOnNext(offerReq -> log.debug("User personal data mutation triggered for userId: {}", offerReq.getUserUUID()))
                .flatMap(offerReq -> saveUserDataStateStores(offerReq.getUserUUID()))
                .zipWhen(this::fetchUserPersonalData)
                .flatMap(tuple -> saveUpdatedUserState(tuple.getT1(), tuple.getT2()))
                .flatMap(this::addMetrics);
    }

    private Mono<UserPersonalData> fetchUserPersonalData(UserStatesStore userState) {
        return Mono.just(userState.getUserUUID())
                .flatMap(userPersonalDataAggregator::getUserPersonalData)
                .doOnError(ex -> log.error("Failed fetching user personal data for userId: {}", userState.getUserUUID(), ex))
                .flatMap(this::saveUserPersonalData)
                .onErrorReturn(new UserPersonalData());
    }

    private Mono<UserPersonalData> saveUserPersonalData(final UserPersonalData userPersonalData) {
        if (offersConfigurations.getNewImplementationEnabled()) {
            return userPersonalDataStorageService.saveUserPersonalDataInDraft(userPersonalData);
        }
        return userPersonalDataStorageService.saveUserPersonalData(userPersonalData);
    }

    private Mono<UserStatesStore> saveUserDataStateStores(String userUuid) {
        StateDetails userPersonalDataState = StateDetails.builder().requestDateTime(OffsetDateTime.now()).build();
        return userStateService.saveUserPersonalDataState(userUuid, userPersonalDataState);
    }

    private Mono<UserStatesStore> saveUpdatedUserState(UserStatesStore userStatesStore, UserPersonalData userPersonalData) {
        return Mono.just(userStatesStore)
                .map(userState -> setBonifyUserId(userState, userPersonalData.getBonifyUserId()))
                .map(userState -> setDistributionChannel(userState, userPersonalData.getDistributionChannel()))
                .map(userState -> setVerifiedViaBank(userState, userPersonalData.getVerifiedViaBankAccount()))
                .map(userState -> setPersonalDataState(userState, userPersonalData))
                .map(userState -> setTenantId(userState, userPersonalData.getTenantId()))
                .flatMap(userStateService::saveUserStates)
                .doOnNext(userState -> log.info("Updated user personal data state for userId: {}, with status: {}", userState.getUserUUID(), userState.getUserPersonalInformationStateDetails().getState()));
    }

    private UserStatesStore setTenantId(UserStatesStore userState, String tenantId) {
        userState.setTenantId(tenantId);
        return userState;
    }

    private UserStatesStore setDistributionChannel(UserStatesStore userState, DistributionChannel distributionChannel) {
        userState.setDistributionChannel(distributionChannel);
        return userState;
    }

    private UserStatesStore setPersonalDataState(UserStatesStore userState, UserPersonalData userPersonalData) {
        boolean isPersonalDataValid = userPersonalDataValidator.test(userPersonalData);
        userState.getUserPersonalInformationStateDetails().setResponseDateTime(OffsetDateTime.now());
        userState.getUserPersonalInformationStateDetails().setState(isPersonalDataValid ? Status.SUCCESS : Status.MISSING_USER_INPUT);
        return userState;
    }

    private UserStatesStore setBonifyUserId(UserStatesStore userState, Long bonifyUserId) {
        userState.setBonifyUserId(bonifyUserId);
        return userState;
    }

    private UserStatesStore setVerifiedViaBank(UserStatesStore userState, Boolean verifiedViaBank) {
        if (verifiedViaBank == null) {
            return userState;
        }
        TransactionalDataStateDetails stateDetails = userState.getTransactionalDataStateDetails();
        if (stateDetails == null) {
            userState.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder()
                    .userVerifiedByBankAccount(verifiedViaBank)
                    .build());
        } else {
            stateDetails.setUserVerifiedByBankAccount(verifiedViaBank);
        }
        return userState;
    }

    private Mono<Void> addMetrics(UserStatesStore userState) {
        return Mono.fromRunnable(() -> offersStateMetric.addPersonalInfoTimer(userState.getUserUUID(), userState.getUserPersonalInformationStateDetails().getRequestDateTime()))
                .then();
    }
}
