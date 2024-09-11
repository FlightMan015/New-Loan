package de.joonko.loan.partner.santander;

import de.joonko.loan.partner.santander.model.SantanderOffer;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SantanderOfferStore")
public interface SantanderOfferRepository extends MongoRepository<SantanderOffer, String> {
    List<SantanderOffer> findByApplicationId(String id);

    List<SantanderOffer> deleteByApplicationId(final String applicationId);
}
