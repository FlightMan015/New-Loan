package de.joonko.loan.integrations.domain.integrationhandler.fts.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserTransactionalDataRepository extends MongoRepository<UserTransactionalDataStore, String> {

    @Query(value = "{'userUUID': ?0 }", fields = "{'accountDetails.nameOnAccount': 1, 'accountDetails.iban': 1 , 'accountDetails.bic': 1  }" )
    UserTransactionalDataStore getKycRelatedPersonalDetails(String userUUID);

    UserTransactionalDataStore deleteByUserUUID(final String userUuid);

    List<UserTransactionalDataStore> deleteByUserUUIDIn(final List<String> userUuids);
}
