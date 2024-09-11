package de.joonko.loan.integrations.domain.integrationhandler.fts;

import de.joonko.loan.config.RetryConfigProperties;
import de.joonko.loan.integrations.domain.integrationhandler.IntegrationHandler;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStoreService;
import de.joonko.loan.integrations.domain.integrationhandler.fts.mapper.TransactionalDraftDataToFtsRequestMapper;
import de.joonko.loan.integrations.model.DacDataState;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.integrations.domain.integrationhandler.fts.model.FinleapToFtsTransactionalData;
import de.joonko.loan.data.support.DataSolutionCommunicationManager;
import de.joonko.loan.user.states.Status;
import de.joonko.loan.user.states.TransactionalDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserTransactionalDataIntegrationHandler implements IntegrationHandler {

    private final UserTransactionalDataIntegrationHandlerFilter handlerFilter;
    private final DataSolutionCommunicationManager dataSolutionCommunicationManager;
    private final UserStatesStoreService userStatesStoreService;
    private final DacFeignClient dacFeignClient;
    private final RetryConfigProperties retryConfigProperties;
    private final UserTransactionalDraftDataStoreService userTransactionalDraftDataStoreService;
    private final TransactionalDraftDataToFtsRequestMapper transactionalDraftDataToFtsRequestMapper;

    @Override
    public Mono<Void> triggerMutation(final OfferRequest offerRequest) {
        return Mono.just(offerRequest)
                .filter(handlerFilter)
                .flatMap(this::sendTransactionsFetchingRequest)
                .then();
    }

    private Mono<Void> sendTransactionsFetchingRequest(final OfferRequest offerRequest) {
        return Mono.just(offerRequest)
                .flatMap(request -> {
                    if (DacDataState.MISSING_OR_STALE.equals(request.getUserState().getDacDataState())) {
                        log.info("Sending request to DS for fetching salary account for user {}", offerRequest.getUserUUID());
                        return queryDataSolutionForSalaryAccount(offerRequest);
                    }
                    log.info("Sending request to DAC for fetching classified account details for user {}", offerRequest.getUserUUID());
                    return queryDACForAccountClassification(offerRequest);
                });
    }

    private Mono<Void> queryDataSolutionForSalaryAccount(final OfferRequest offerRequest) {
        return getTransactionalDataStoreDetailsAndUpdateState(offerRequest.getUserUUID(), Status.QUERY_DATA_SOLUTION)
                .flatMap(userStatesStore -> {
                    userStatesStore.getTransactionalDataStateDetails().setRequestFromDataSolution(OffsetDateTime.now());
                    userStatesStore.getTransactionalDataStateDetails().setResponseFromDataSolution(null);
                    log.info("Updating requestFromDS for user - {}", offerRequest.getUserUUID());
                    return userStatesStoreService.save(userStatesStore);
                })
                .flatMap(this::sendMessageToDS);
    }

    private Mono<Void> sendMessageToDS(final UserStatesStore userStatesStore) {
        return Mono.fromRunnable(() -> dataSolutionCommunicationManager.queryDataSolutionForSalaryAccount(userStatesStore.getUserUUID(), userStatesStore.getBonifyUserId()));
    }

    private Mono<Void> queryDACForAccountClassification(final OfferRequest offerRequest) {
        return userStatesStoreService.findById(offerRequest.getUserUUID())
                .flatMap(userStatesStore -> {
                    final var sentForClassificationCounter = ofNullable(userStatesStore.getTransactionalDataStateDetails().getSentForClassificationCounter()).orElse(0);
                    if (sentForClassificationCounter < retryConfigProperties.getMaxRetry()) {
                        log.info("DAC: Trying to send transactions for classification for user {}", offerRequest.getUserUUID());
                        return triggerDACRequest(offerRequest);
                    } else {
                        log.error("DAC: The counter max retry is reached for trying to send transactions for classification for user {}", offerRequest.getUserUUID());
                        return updateTransactionalDataStoreDetailsForMissingSalaryAccount(offerRequest.getUserUUID());
                    }
                })
                .then();
    }

    private Mono<Void> triggerDACRequest(final @NotNull OfferRequest offerRequest) {
        return userTransactionalDraftDataStoreService.findById(offerRequest.getUserUUID())
                .map(transactionalDraftDataToFtsRequestMapper::map)
                .flatMap(this::requestFinLeap)
                .then();
    }

    private Mono<Void> requestFinLeap(final FinleapToFtsTransactionalData finleapToFtsTransactionalData) {
        log.info("DAC: Salary account for user {} is updated, will send transactions for classification", finleapToFtsTransactionalData.getUserUUID());
        return callFinleap(finleapToFtsTransactionalData)
                .doOnSuccess(any -> log.info("DAC: Successfully sent transactions of user {} for classification", finleapToFtsTransactionalData.getUserUUID()))
                .switchIfEmpty(Mono.defer(() -> updateTransactionalDataStoreDetailsForSentForClassification(finleapToFtsTransactionalData.getUserUUID(), finleapToFtsTransactionalData.getAccountInternalId())))
                .onErrorResume(ex -> {
                    log.error("DAC: Exception happened while requesting dacFeignClient for classifying transactions for user - {}, exception type - , message - {}, cause - {}", finleapToFtsTransactionalData.getUserUUID(), ex.getClass(), ex.getMessage(), ex.getCause());
                    return increaseSentForClassificationCounter(finleapToFtsTransactionalData.getUserUUID());
                });
    }

    private Mono<Void> callFinleap(final FinleapToFtsTransactionalData finleapToFtsTransactionalData) {
        return Mono.fromRunnable(() -> dacFeignClient.finleapToFts(finleapToFtsTransactionalData));
    }


    private Mono<UserStatesStore> getTransactionalDataStoreDetailsAndUpdateState(final String userUUID, final Status status) {
        return userStatesStoreService.findById(userUUID)
                .map(userStatesStore -> {
                    final var transactionalDataStateDetails = ofNullable(userStatesStore.getTransactionalDataStateDetails())
                            .orElse(TransactionalDataStateDetails.builder().build());
                    transactionalDataStateDetails.setState(status);
                    userStatesStore.setTransactionalDataStateDetails(transactionalDataStateDetails);

                    return userStatesStore;
                });
    }

    private Mono<Void> updateTransactionalDataStoreDetailsForMissingSalaryAccount(final String userUUID) {
        return getTransactionalDataStoreDetailsAndUpdateState(userUUID, Status.MISSING_SALARY_ACCOUNT)
                .flatMap(userStatesStoreService::save)
                .then();
    }


    private Mono<Void> updateTransactionalDataStoreDetailsForSentForClassification(final String userUUID, final String salaryAccountId) {
        return getTransactionalDataStoreDetailsAndUpdateState(userUUID, Status.SENT_FOR_CLASSIFICATION)
                .map(userStatesStore -> {
                    userStatesStore.getTransactionalDataStateDetails().setAccountInternalId(salaryAccountId);
                    userStatesStore.getTransactionalDataStateDetails().setSentForClassification(OffsetDateTime.now());
                    userStatesStore.getTransactionalDataStateDetails().setSentForClassificationCounter(null);
                    userStatesStore.getTransactionalDataStateDetails().setResponseDateTime(null);
                    return userStatesStore;
                })
                .flatMap(userStatesStoreService::save)
                .then();
    }

    private Mono<Void> increaseSentForClassificationCounter(final String userUUID) {
        return userStatesStoreService.findById(userUUID)
                .map(userStatesStore -> {
                    userStatesStore.getTransactionalDataStateDetails().increaseSentForClassificationCounter();
                    return userStatesStore;
                })
                .flatMap(userStatesStoreService::save)
                .then();
    }
}
