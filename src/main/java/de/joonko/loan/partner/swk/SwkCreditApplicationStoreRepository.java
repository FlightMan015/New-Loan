package de.joonko.loan.partner.swk;

import de.joonko.loan.partner.swk.model.SwkCreditApplicationOffer;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SwkCreditApplicationOffer")
public interface SwkCreditApplicationStoreRepository extends MongoRepository<SwkCreditApplicationOffer, String> {
    List<SwkCreditApplicationOffer> findByApplicationIdIs(String id);

    List<SwkCreditApplicationOffer> deleteByApplicationId(final String applicationId);
}

