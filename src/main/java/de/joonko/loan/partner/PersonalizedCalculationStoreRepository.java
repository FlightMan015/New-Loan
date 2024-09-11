package de.joonko.loan.partner;

import de.joonko.loan.partner.consors.PersonalizedCalculationsStore;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalizedCalculationStoreRepository extends MongoRepository<PersonalizedCalculationsStore, String> {

    Optional<PersonalizedCalculationsStore> findByApplicationId(String applicationId);

    List<PersonalizedCalculationsStore> deleteByApplicationId(final String applicationId);
}
