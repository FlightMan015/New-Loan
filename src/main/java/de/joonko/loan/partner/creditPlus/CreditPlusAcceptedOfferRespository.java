package de.joonko.loan.partner.creditPlus;

import de.joonko.loan.partner.creditPlus.mapper.model.CreditPlusAcceptedOffer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CreditPlusAcceptedOfferRespository extends MongoRepository<CreditPlusAcceptedOffer, String> {
    List<CreditPlusAcceptedOffer> findByApplicationId(String applicationId);
    List<CreditPlusAcceptedOffer> findByCreditOffer_LocalCpReferenceNumber(Integer CpReferenceNumber);
}
