package de.joonko.loan.partner.auxmoney;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuxmoneySingleOfferCallResponseStoreRepository extends MongoRepository<AuxmoneySingleOfferCallResponseStore, String> {

    Optional<AuxmoneySingleOfferCallResponseStore> findByLoanApplicationId(String loanApplicationId);
}
