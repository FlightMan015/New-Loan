package de.joonko.loan.user.states;

import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface UserStatesStoreService {

    Mono<UserStatesStore> save(UserStatesStore userStatesStore);

    Mono<List<UserStatesStore>> updateAll(List<UserStatesStore> userStatesStores);

    Mono<UserStatesStore> findById(String userId);

    Mono<UserStatesStore> deleteByUserId(final String userId);

    Mono<List<UserStatesStore>> findAllByUserUUID(List<String> userIds);

    Mono<List<UserStatesStore>> findAll(UUID tenantId, OffsetDateTime requestStartDateTime, OffsetDateTime requestEndDateTime);

    Mono<List<UserStatesStore>> findAllBeforeDate(OffsetDateTime responseFrom);
}
