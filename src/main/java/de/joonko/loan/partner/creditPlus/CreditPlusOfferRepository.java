package de.joonko.loan.partner.creditPlus;

import de.joonko.loan.partner.creditPlus.mapper.model.CreditPlusOffer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CreditPlusOfferStore")
public interface CreditPlusOfferRepository extends MongoRepository<CreditPlusOffer, String> {

    List<CreditPlusOffer> findByApplicationId(String id);
    List<CreditPlusOffer> findByDealerOrderNumber(String id);
}