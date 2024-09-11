package de.joonko.loan.db.repositories;

import de.joonko.loan.db.vo.LoanOfferStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

public class CustomLoanOffersStoreRepositoryUnitTest {

    private CustomLoanOfferStoreRepository customLoanOfferStoreRepository;
    private MongoTemplate mongoTemplate;


    @BeforeEach
    public void init() {
        mongoTemplate = mock(MongoTemplate.class);
        customLoanOfferStoreRepository = new CustomLoanOfferStoreRepository(mongoTemplate);
    }

    @Test
    public void testSoftDeletion() {
        // given
        String userUUID = "123";

        // when
        customLoanOfferStoreRepository.softDeleteOffersForUser(userUUID);

        // then
        verify(mongoTemplate)
                .updateMulti(eq(query(where("userUUID").is(userUUID))),
                        eq(update("deleted", true)),
                        eq(LoanOfferStore.class)
                );
    }
}
