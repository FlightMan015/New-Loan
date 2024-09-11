package de.joonko.loan.integrations.domain.integrationhandler.fts.domain;

import reactor.core.publisher.Mono;

import java.util.List;

public interface UserTransactionalDraftDataStoreService {

    Mono<UserTransactionalDraftDataStore> save(final UserTransactionalDraftDataStore userTransactionalDraftDataStore);

    Mono<UserTransactionalDraftDataStore> findById(final String userUUID);

    Mono<UserTransactionalDraftDataStore> deleteById(final String userUuid);

    Mono<List<UserTransactionalDraftDataStore>> deleteByIds(final List<String> userUuids);
}
