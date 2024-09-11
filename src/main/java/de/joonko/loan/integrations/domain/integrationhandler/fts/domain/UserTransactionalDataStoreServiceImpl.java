package de.joonko.loan.integrations.domain.integrationhandler.fts.domain;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserTransactionalDataStoreServiceImpl implements UserTransactionalDataStoreService {

    private final UserTransactionalDataRepository repository;

    @Override
    public Mono<UserTransactionalDataStore> save(UserTransactionalDataStore userTransactionalDataStore) {
        return Mono.fromCallable(() -> repository.save(userTransactionalDataStore))
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<UserTransactionalDataStore> deleteById(final String userId) {
        return Mono.fromCallable(() -> repository.deleteByUserUUID(userId))
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<List<UserTransactionalDataStore>> deleteByIds(final List<String> userUuids) {
        return Mono.fromCallable(() -> repository.deleteByUserUUIDIn(userUuids))
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<UserTransactionalDataStore> getKycRelatedPersonalDetails(String userId) {
        return Mono.fromCallable(() -> repository.getKycRelatedPersonalDetails(userId))
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<UserTransactionalDataStore> getById(String userId) {
        return Mono.fromCallable(() -> repository.findById(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .subscribeOn(Schedulers.elastic());
    }
}
