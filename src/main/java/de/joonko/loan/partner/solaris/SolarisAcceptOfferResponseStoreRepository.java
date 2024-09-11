package de.joonko.loan.partner.solaris;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SolarisAcceptOfferResponseStoreRepository extends MongoRepository<SolarisAcceptOfferResponseStore, String> {
    List<SolarisAcceptOfferResponseStore> findByIdentificationId(String identificationId);
    List<SolarisAcceptOfferResponseStore> findByApplicationId(String applicationId);
}
