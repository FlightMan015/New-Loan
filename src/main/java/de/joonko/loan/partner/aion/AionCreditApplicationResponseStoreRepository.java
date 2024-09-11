package de.joonko.loan.partner.aion;

import de.joonko.loan.partner.aion.model.CreditApplicationResponseStore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AionCreditApplicationResponseStoreRepository extends MongoRepository<CreditApplicationResponseStore, String> {

    List<CreditApplicationResponseStore> findByApplicationId(final String applicationId);

    List<CreditApplicationResponseStore> findByProcessId(final String processId);
}
