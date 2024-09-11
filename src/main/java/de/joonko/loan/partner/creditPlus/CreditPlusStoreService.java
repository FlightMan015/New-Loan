package de.joonko.loan.partner.creditPlus;

import de.joonko.loan.partner.creditPlus.mapper.model.CreditPlusAcceptedOffer;
import de.joonko.loan.partner.creditPlus.mapper.model.CreditPlusOffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditPlusStoreService {

    private final CreditPlusOfferRepository creditPlusOfferRepository;
    private final CreditPlusAcceptedOfferRespository creditPlusAcceptedOfferRespository;

    @Autowired
    MongoTemplate mongoTemplate;

    public void saveOffer(CreditPlusOffer offer) {
        log.info("Saving offer in CreditPlus store for loanApplication Id  {} ", offer.getApplicationId());
        creditPlusOfferRepository.save(offer);
    }

    public void saveAcceptedOffer(CreditPlusAcceptedOffer offer) {
        log.info("Saving accepted offer in CreditPlus store for loanApplication Id  {} ", offer.getApplicationId());
        creditPlusAcceptedOfferRespository.save(offer);
    }

    public Integer findCpReferenceNumberForAcceptedOffer(String applicationId) {
        return creditPlusAcceptedOfferRespository.findByApplicationId(applicationId)
                .get(0)
                .getCreditOffer()
                .getCpReferenceNumber();
    }

    public Integer getCpTransactionNumber(String applicationId, Integer duration) {
        return creditPlusOfferRepository.findByDealerOrderNumber(applicationId.concat(String.valueOf(duration)))
                .get(0)
                .getCreditOffer()
                .getCpReferenceNumber();
    }

    public String findApplicationIdByCpReferencenumber(Integer cpReferenceNumber) {
        List<CreditPlusAcceptedOffer> offers = creditPlusAcceptedOfferRespository.findByCreditOffer_LocalCpReferenceNumber(cpReferenceNumber);
        if (offers.size() > 0) {
            return offers
                    .get(0)
                    .getApplicationId();
        } else {
            return null;
        }
    }

    public void updateOffer(CreditPlusOffer offer) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("dealerOrderNumber").is(offer.getDealerOrderNumber()));
            mongoTemplate.updateFirst(query, Update.update("creditOffer", offer.getCreditOffer()), "creditPlusOffer");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateContractState(String dealerOrderNumber, Integer contractState) {
        CreditPlusOffer creditPlusOffer = creditPlusOfferRepository.findByDealerOrderNumber(dealerOrderNumber)
                .get(0);
        List<Integer> existingContractStates = creditPlusOffer.getContractState();
        existingContractStates.add(contractState);
        Query query = new Query();
        query.addCriteria(Criteria.where("dealerOrderNumber").is(dealerOrderNumber));
        mongoTemplate.updateFirst(query, Update.update("contractState", existingContractStates), "creditPlusOffer");
    }

    public List<CreditPlusOffer> findByApplicationId(String applicationId) {
        return creditPlusOfferRepository.findByApplicationId(applicationId);
    }

}
