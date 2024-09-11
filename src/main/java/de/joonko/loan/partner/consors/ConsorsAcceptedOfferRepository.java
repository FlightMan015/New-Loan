package de.joonko.loan.partner.consors;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsorsAcceptedOfferRepository extends MongoRepository<ConsorsAcceptedOfferStore, String> {
    Optional<ConsorsAcceptedOfferStore> findByConsorsAcceptOfferResponse_ContractIdentifier(String contractIdentifier);
}
