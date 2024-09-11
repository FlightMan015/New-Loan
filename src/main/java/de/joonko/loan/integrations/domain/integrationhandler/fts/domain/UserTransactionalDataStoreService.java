package de.joonko.loan.integrations.domain.integrationhandler.fts.domain;

import reactor.core.publisher.Mono;

import java.util.List;

public interface UserTransactionalDataStoreService {

    Mono<UserTransactionalDataStore> save(final UserTransactionalDataStore userTransactionalDataStore);

    Mono<UserTransactionalDataStore> deleteById(final String userId);

    Mono<List<UserTransactionalDataStore>> deleteByIds(final List<String> userUuids);

    Mono<UserTransactionalDataStore> getKycRelatedPersonalDetails(final String userId);

    Mono<UserTransactionalDataStore> getById(final String userId);
}
