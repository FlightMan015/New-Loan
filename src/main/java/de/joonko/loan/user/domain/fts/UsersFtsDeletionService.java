package de.joonko.loan.user.domain.fts;

import de.joonko.loan.config.UsersFtsDeletionConfig;
import de.joonko.loan.db.service.LoanDemandRequestService;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStoreService;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStoreService;
import de.joonko.loan.user.service.UserAdditionalInformationService;
import de.joonko.loan.user.states.TransactionalDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;
import de.joonko.loan.userdata.infrastructure.draft.UserDataDraftStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

import static de.joonko.loan.common.CollectionUtil.mapList;

@Slf4j
@RequiredArgsConstructor
@Service
public class UsersFtsDeletionService {

    private final UsersFtsDeletionConfig config;
    private final UserStatesStoreService userStatesStoreService;
    private final UserTransactionalDraftDataStoreService userTransactionalDraftDataStoreService;
    private final UserTransactionalDataStoreService userTransactionalDataStoreService;
    private final UserDataDraftStorageService userDataDraftStorageService;
    private final UserAdditionalInformationService userAdditionalInformationService;
    private final LoanDemandRequestService loanDemandRequestService;

    public Mono<List<UserStatesStore>> delete() {
        return Mono.just(config.getDaysAgo())
                .map(daysAgo -> OffsetDateTime.now().minusDays(daysAgo))
                .doOnNext(dateThreshold -> log.debug("delete fts data older than {}", dateThreshold.toLocalDate()))
                .flatMap(userStatesStoreService::findAllBeforeDate)
                .doOnNext(userStates -> log.debug("found {} users with old fts data", userStates.size()))
                .filter(userStates -> !userStates.isEmpty())
                .flatMap(this::updateDb);
    }

    private Mono<List<UserStatesStore>> updateDb(List<UserStatesStore> userStatesStores) {
        return Mono.just(userStatesStores)
                .map(mapList(UserStatesStore::getUserUUID))
                .flatMap(this::updateByUserUuid)
                .then(invalidateTransactionalState(userStatesStores));
    }

    private Mono<Void> updateByUserUuid(final List<String> userUuids) {
        return Mono.just(userUuids)
                .flatMap(userTransactionalDraftDataStoreService::deleteByIds)
                .doOnNext(deletedDocuments -> log.debug("deleted {} documents from userTransactionalDraftDataStore", deletedDocuments.size()))
                .then(userTransactionalDataStoreService.deleteByIds(userUuids))
                .doOnNext(deletedDocuments -> log.debug("deleted {} documents from userTransactionalDataStoreService", deletedDocuments.size()))
                .then(userDataDraftStorageService.removeFtsData(userUuids))
                .doOnNext(updatedDocuments -> log.debug("updated {} documents from userDraftInformationStore", updatedDocuments.size()))
                .then(userAdditionalInformationService.removeFtsData(userUuids))
                .doOnNext(updatedDocuments -> log.debug("updated {} documents from userAdditionalInformationStore", updatedDocuments.size()))
                .then(loanDemandRequestService.removeFtsData(userUuids))
                .doOnNext(updatedDocuments -> log.debug("updated {} documents from loanDemandRequest", updatedDocuments.size()))
                .then();
    }

    private Mono<List<UserStatesStore>> invalidateTransactionalState(List<UserStatesStore> userStatesStores) {
        return Mono.just(userStatesStores)
                .map(userStates -> {
                    userStates.forEach(userStatesStore -> userStatesStore.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder()
                            .userVerifiedByBankAccount(false)
                            .build()));
                    return userStates;
                })
                .flatMap(userStatesStoreService::updateAll)
                .doOnNext(updatedDocuments -> log.debug("updated {} documents from userStatesStore", updatedDocuments.size()));
    }
}
