package de.joonko.loan.integrations.domain.integrationhandler.fts.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserTransactionalDraftDataRepository extends MongoRepository<UserTransactionalDraftDataStore, String> {
    UserTransactionalDraftDataStore deleteByUserUUID(final String userUuid);

    List<UserTransactionalDraftDataStore> deleteByUserUUIDIn(final List<String> userUuids);
}
