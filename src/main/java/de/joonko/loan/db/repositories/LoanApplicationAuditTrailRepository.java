package de.joonko.loan.db.repositories;

import de.joonko.loan.db.vo.LoanApplicationAuditTrail;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("loanApplicationAuditTrailRepository")
public interface LoanApplicationAuditTrailRepository extends MongoRepository<LoanApplicationAuditTrail, String> {

    List<LoanApplicationAuditTrail> findAllByApplicationIdOrderByInsertTs(String loanApplicationId);

    List<LoanApplicationAuditTrail> deleteByApplicationId(final String applicationId);

}
