package de.joonko.loan.integrations.domain.integrationhandler.fts.domain;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserTransactionalDraftDataStoreServiceImpl implements UserTransactionalDraftDataStoreService {

    private final UserTransactionalDraftDataRepository repository;

    @Override
    public Mono<UserTransactionalDraftDataStore> save(UserTransactionalDraftDataStore userTransactionalDraftDataStore) {
        return Mono.fromCallable(() -> repository.save(userTransactionalDraftDataStore))
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<UserTransactionalDraftDataStore> findById(final String userUUID) {
        return Mono.fromCallable(() -> repository.findById(userUUID))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<UserTransactionalDraftDataStore> deleteById(final String userUuid) {
        return Mono.fromCallable(() -> repository.deleteByUserUUID(userUuid))
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<List<UserTransactionalDraftDataStore>> deleteByIds(List<String> userUuids) {
        return Mono.fromCallable(() -> repository.deleteByUserUUIDIn(userUuids))
                .subscribeOn(Schedulers.elastic());
    }
}
