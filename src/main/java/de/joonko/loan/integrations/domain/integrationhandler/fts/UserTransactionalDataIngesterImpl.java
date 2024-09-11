package de.joonko.loan.integrations.domain.integrationhandler.fts;

import de.joonko.loan.avro.dto.dac.DacAccountSnapshot;
import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountResponse;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStoreService;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStoreService;
import de.joonko.loan.integrations.domain.integrationhandler.fts.mapper.SalaryAccountResponseToTransactionalDraftDataMapper;
import de.joonko.loan.metric.OffersStateMetric;
import de.joonko.loan.user.states.Status;
import de.joonko.loan.user.states.TransactionalDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStoreMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserTransactionalDataIngesterImpl implements UserTransactionalDataIngester {

    private final UserStatesStoreService userStatesStoreService;
    private final OffersStateMetric offersStateMetric;
    private final UserTransactionalDataStoreMapper userTransactionalDataStoreMapper;
    private final UserTransactionalDataStoreService userTransactionalDataStoreService;
    private final SalaryAccountResponseToTransactionalDraftDataMapper salaryAccountResponseToTransactionalDraftDataMapper;
    private final UserTransactionalDraftDataStoreService userTransactionalDraftDataStoreService;

    @Override
    public Mono<Void> handleQuerySalaryAccountResponseFromDS(final @NotNull QuerySalaryAccountResponse querySalaryAccountResponse) {
        log.info("DS: receiving salary account response for user {}", querySalaryAccountResponse.getUserUUID());
        return Mono.just(querySalaryAccountResponse)
                .flatMap(response -> ofNullable(response.getAccountInternalId())
                        .map(accountId ->
                                ofNullable(querySalaryAccountResponse.getTransactions()).filter(CollectionUtils::isNotEmpty)
                                        .map(transactions -> saveSalaryAccountResponse(querySalaryAccountResponse))
                                        .orElseGet(() -> {
                                            log.warn("DS: received salary account response for user {}, containing no transaction information", querySalaryAccountResponse.getUserUUID());
                                            return updateTransactionalDataStoreDetailsForOutdatedSalaryAccount(querySalaryAccountResponse.getUserUUID(), accountId);
                                        })
                        )
                        .orElseGet(() -> {
                            log.warn("DS: received salary account response for user {}, containing no accountId", querySalaryAccountResponse.getUserUUID());
                            return updateTransactionalDataStoreDetailsForMissingSalaryAccountFromDS(querySalaryAccountResponse.getUserUUID());
                        })
                );
    }

    @Override
    public Mono<Void> handleDacResponse(final DacAccountSnapshot dacAccountSnapshot) {
        log.info("DAC: receiving dac account snapshot for user {}", dacAccountSnapshot.getUserUUID());
        return Mono.just(dacAccountSnapshot)
                .flatMap(accountSnapshot -> {
                    if (TRUE.equals(dacAccountSnapshot.getCustomDACData().getHasSalary())) {
                        // TODO: Maybe checking if we have already received the same data previously and it's still valid and not overwriting with the same (or changed, if user was forced to add account and decided to add another account) data?
                        final var userTransactionalDataStore = userTransactionalDataStoreMapper.toTransactionalDataStore(dacAccountSnapshot);
                        log.info("DAC: Successfully saved dacAccountSnapshot for user {}", dacAccountSnapshot.getUserUUID());
                        return userTransactionalDataStoreService.save(userTransactionalDataStore)
                                .flatMap(ignored -> updateTransactionalDataStateToSuccess(dacAccountSnapshot.getUserUUID()));
                    } else {
                        return updateTransactionalDataStoreDetailsForMissingSalaryAccountFromDAC(dacAccountSnapshot.getUserUUID());
                    }
                });
    }

    private Mono<Void> saveSalaryAccountResponse(final QuerySalaryAccountResponse querySalaryAccountResponse) {
        log.info("DS: Salary response is valid, saving to db");
        return Mono.just(querySalaryAccountResponse)
                .map(salaryAccountResponseToTransactionalDraftDataMapper::map)
                .flatMap(userTransactionalDraftDataStoreService::save)
                .flatMap(response -> updateTransactionalDataStoreDetailsForWaitingToSendToClassification(querySalaryAccountResponse.getUserUUID(), querySalaryAccountResponse.getAccountInternalId()));
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

    private Mono<Void> updateTransactionalDataStoreDetailsForWaitingToSendToClassification(final String userUUID, final String salaryAccountId) {
        return getTransactionalDataStoreDetailsAndUpdateState(userUUID, Status.WAITING_TO_SEND_FOR_CLASSIFICATION)
                .map(userStatesStore -> {
                    userStatesStore.getTransactionalDataStateDetails().setAccountInternalId(salaryAccountId);
                    userStatesStore.getTransactionalDataStateDetails()
                            .setResponseFromDataSolution(
                                    ofNullable(userStatesStore.getTransactionalDataStateDetails().getResponseFromDataSolution())
                                            .orElseGet(() -> {
                                                offersStateMetric.addTransactionalDataFromDSTimer(userStatesStore.getUserUUID(), userStatesStore.getTransactionalDataStateDetails().getRequestFromDataSolution());
                                                return OffsetDateTime.now();
                                            }));
                    userStatesStore.getTransactionalDataStateDetails().setUserVerifiedByBankAccount(true);
                    return userStatesStore;
                })
                .flatMap(userStatesStoreService::save)
                .then();
    }

    private Mono<Void> updateTransactionalDataStoreDetailsForMissingSalaryAccountFromDS(final String userUUID) {
        return getTransactionalDataStoreDetailsAndUpdateState(userUUID, Status.MISSING_SALARY_ACCOUNT)
                .map(userStatesStore -> {
                    userStatesStore.getTransactionalDataStateDetails()
                            .setResponseFromDataSolution(
                                    ofNullable(userStatesStore.getTransactionalDataStateDetails().getResponseFromDataSolution())
                                            .orElseGet(() -> {
                                                offersStateMetric.addTransactionalDataFromDSTimer(userUUID, userStatesStore.getTransactionalDataStateDetails().getRequestFromDataSolution());
                                                return nonNull(userStatesStore.getTransactionalDataStateDetails().getRequestFromDataSolution()) ? OffsetDateTime.now() : null;
                                            }));
                    return userStatesStore;
                })
                .flatMap(userStatesStoreService::save)
                .then();
    }


    private Mono<Void> updateTransactionalDataStoreDetailsForMissingSalaryAccountFromDAC(final String userUUID) {
        return getTransactionalDataStoreDetailsAndUpdateState(userUUID, Status.MISSING_SALARY_ACCOUNT)
                .map(userStatesStore -> {
                    userStatesStore.getTransactionalDataStateDetails()
                            .setResponseDateTime(
                                    ofNullable(userStatesStore.getTransactionalDataStateDetails().getResponseDateTime())
                                            .orElseGet(() -> {
                                                offersStateMetric.addTransactionalDataFromDACTimer(userUUID, userStatesStore.getTransactionalDataStateDetails().getSentForClassification());
                                                return OffsetDateTime.now();
                                            }));
                    return userStatesStore;
                })
                .flatMap(userStatesStoreService::save)
                .then();
    }

    private Mono<Void> updateTransactionalDataStoreDetailsForOutdatedSalaryAccount(final String userUUID, final String salaryAccountId) {
        return getTransactionalDataStoreDetailsAndUpdateState(userUUID, Status.OUTDATED_SALARY_ACCOUNT)
                .map(userStatesStore -> {
                    userStatesStore.getTransactionalDataStateDetails().setAccountInternalId(salaryAccountId);
                    userStatesStore.getTransactionalDataStateDetails().setResponseFromDataSolution(OffsetDateTime.now());
                    offersStateMetric.addTransactionalDataFromDSTimer(userStatesStore.getUserUUID(), userStatesStore.getTransactionalDataStateDetails().getRequestFromDataSolution());
                    return userStatesStore;
                })
                .flatMap(userStatesStoreService::save)
                .then();
    }

    private Mono<Void> updateTransactionalDataStateToSuccess(final String userUUID) {
        return getTransactionalDataStoreDetailsAndUpdateState(userUUID, Status.SUCCESS)
                .map(userStatesStore -> {
                    userStatesStore.getTransactionalDataStateDetails().clearSentForClassificationCounter();
                    userStatesStore.getTransactionalDataStateDetails().setResponseDateTime(OffsetDateTime.now());
                    userStatesStore.getTransactionalDataStateDetails().setSalaryAccountAdded(true);
                    userStatesStore.getTransactionalDataStateDetails().setUserVerifiedByBankAccount(true);
                    offersStateMetric.addTransactionalDataFromDACTimer(userStatesStore.getUserUUID(), userStatesStore.getTransactionalDataStateDetails().getResponseFromDataSolution());
                    return userStatesStore;
                })
                .flatMap(userStatesStoreService::save)
                .then();
    }

}
