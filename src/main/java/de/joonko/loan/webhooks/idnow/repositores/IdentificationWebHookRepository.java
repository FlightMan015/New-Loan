package de.joonko.loan.webhooks.idnow.repositores;

import de.joonko.loan.webhooks.idnow.model.Identification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("IdentificationWebHookRepository")
public interface IdentificationWebHookRepository extends MongoRepository<Identification, String> {

    @Query(delete = true, value = "{'identificationProcess.transactionNumber' : ?0}")
    Long deleteAllByTransactionNumber(final String transactionId);
}
