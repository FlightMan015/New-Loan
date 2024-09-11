package de.joonko.loan.partner.swk;

import de.joonko.loan.partner.swk.model.SwkOffer;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SwkOfferStore")
public interface SwkOfferRepository extends MongoRepository<SwkOffer, String> {
    List<SwkOffer> findByApplicationIdIs(String id);

    List<SwkOffer> deleteByApplicationId(final String applicationId);
}
