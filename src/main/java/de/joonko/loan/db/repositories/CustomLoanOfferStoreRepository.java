package de.joonko.loan.db.repositories;

import de.joonko.loan.db.vo.LoanOfferStore;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@Repository
@RequiredArgsConstructor
public class CustomLoanOfferStoreRepository {

    private final MongoTemplate mongoTemplate;

    public void softDeleteOffersForUser(String userUUID) {
        mongoTemplate.updateMulti(query(where("userUUID").is(userUUID)), update("deleted", true), LoanOfferStore.class);
    }

}
