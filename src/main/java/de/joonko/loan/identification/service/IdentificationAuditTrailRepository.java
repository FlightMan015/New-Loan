package de.joonko.loan.identification.service;

import de.joonko.loan.identification.model.IdentificationAuditTrail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("IdentificationAuditTrailRepository")
public interface IdentificationAuditTrailRepository extends MongoRepository<IdentificationAuditTrail, String> {
    List<IdentificationAuditTrail> findByApplicationId(String applicationId);
    List<IdentificationAuditTrail> findByRemarkContains(String applicationId);
    void deleteByApplicationId(final String applicationId);
}
