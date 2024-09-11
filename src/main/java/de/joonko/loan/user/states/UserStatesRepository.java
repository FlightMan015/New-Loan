package de.joonko.loan.user.states;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;


@Repository
public interface UserStatesRepository extends MongoRepository<UserStatesStore, String> {
    UserStatesStore deleteByUserUUID(final String userUuid);

    List<UserStatesStore> findAllByUserUUIDIn(final List<String> ids);

    @Query("{" +
            "    'tenantId': ?0," +
            "    'offerDateStateDetailsSet.requestDateTime': {" +
            "        $gte: ?1," +
            "        $lte: ?2" +
            "    }" +
            "}")
    List<UserStatesStore> findAll(String tenantId, OffsetDateTime requestStartDateTime, OffsetDateTime requestEndDateTime);

    @Query("{" +
            "    $or: [" +
            "        {'transactionalDataStateDetails.responseFromDataSolution': {$lte: ?0}}," +
            "        {'transactionalDataStateDetails.responseDateTime': {$lte: ?0}}" +
            "    ]" +
            "}")
    List<UserStatesStore> findAll(OffsetDateTime responseFrom);
}
