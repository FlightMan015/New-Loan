package de.joonko.loan.partner.solaris;

import de.joonko.loan.partner.solaris.model.SolarisSignedDocTrail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("SolarisSignedDocRepository")
public interface SolarisSignedDocRepository extends MongoRepository<SolarisSignedDocTrail, String> {
    List<SolarisSignedDocTrail> findByEmailSentIsFalse();
    Optional<SolarisSignedDocTrail> findByApplicationId(String applicationId);
}
