package de.joonko.loan.partner.solaris;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SolarisGetOfferResponseStoreRepository extends MongoRepository<SolarisGetOfferResponseStore, String> {

    List<SolarisGetOfferResponseStore> findByApplicationId(String loanApplicationId);

}
