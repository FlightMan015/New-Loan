package de.joonko.loan.partner.postbank.model.store;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostbankLoanDemandStoreRepository extends MongoRepository<PostbankLoanDemandStore, String> {

    List<PostbankLoanDemandStore> findByApplicationId(final String applicationId);

    List<PostbankLoanDemandStore> findByContractNumber(final String contractNumber);
}
