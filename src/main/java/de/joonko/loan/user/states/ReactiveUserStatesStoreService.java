package de.joonko.loan.user.states;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ReactiveUserStatesStoreService implements UserStatesStoreService {

    private final UserStatesRepository repository;

    @Override
    public Mono<UserStatesStore> save(UserStatesStore userStatesStore) {
        return Mono.fromCallable(() -> repository.save(userStatesStore))
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<List<UserStatesStore>> updateAll(List<UserStatesStore> userStatesStores) {
        return Mono.fromCallable(() -> repository.saveAll(userStatesStores))
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<UserStatesStore> findById(String userId) {
        return Mono.fromCallable(() -> repository.findById(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<UserStatesStore> deleteByUserId(final String userId) {
        return Mono.fromCallable(() -> repository.deleteByUserUUID(userId))
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<List<UserStatesStore>> findAllByUserUUID(List<String> userIds) {
        return Mono.fromCallable(() -> repository.findAllByUserUUIDIn(userIds))
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<List<UserStatesStore>> findAll(UUID tenantId, OffsetDateTime requestStartDateTime, OffsetDateTime requestEndDateTime) {
        return Mono.fromCallable(() -> repository.findAll(tenantId.toString(), requestStartDateTime, requestEndDateTime))
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<List<UserStatesStore>> findAllBeforeDate(OffsetDateTime responseFrom) {
        return Mono.fromCallable(() -> repository.findAll(responseFrom))
                .subscribeOn(Schedulers.elastic());
    }
}
