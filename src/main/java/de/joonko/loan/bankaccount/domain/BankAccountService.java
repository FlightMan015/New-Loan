package de.joonko.loan.bankaccount.domain;

import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStoreService;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStoreService;
import de.joonko.loan.user.UserDataNotFoundException;
import de.joonko.loan.user.states.TransactionalDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class BankAccountService {

    private final UserTransactionalDataStoreService userTransactionalDataStoreService;
    private final UserTransactionalDraftDataStoreService userTransactionalDraftDataStoreService;
    private final UserStatesStoreService userStatesStoreService;

    public Mono<Void> delete(@NotNull final String userUuid) {
        return Mono.just(userUuid)
                .flatMap(this::invalidateTransactionalState)
                .then(userTransactionalDataStoreService.deleteById(userUuid))
                .then(userTransactionalDraftDataStoreService.deleteById(userUuid))
                .doOnNext(any -> log.info("User transactional data deleted for userUuid: {}", userUuid))
                .then();
    }

    private Mono<UserStatesStore> invalidateTransactionalState(String userUuid) {
        return Mono.just(userUuid)
                .flatMap(userStatesStoreService::findById)
                .filter(userStates -> userStates.getTransactionalDataStateDetails() != null)
                .switchIfEmpty(
                        Mono.defer(() -> Mono.error(new UserDataNotFoundException(String.format("Missing user transactional data state with userUuid: %s", userUuid))))
                ).map(userStates -> {
                    userStates.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder()
                            .userVerifiedByBankAccount(false)
                            .build());
                    return userStates;
                }).flatMap(userStatesStoreService::save)
                .doOnNext(any -> log.info("User transactional state invalidated for userUuid: {}", userUuid));
    }
}
