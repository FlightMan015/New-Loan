package de.joonko.loan.db.repositories;

import de.joonko.loan.offer.api.LoanDemandRequest;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanDemandRequestRepository extends CrudRepository<LoanDemandRequest, String> {
    List<LoanDemandRequest> deleteByUserUUID(final String userUUID);

    Optional<LoanDemandRequest> findByApplicationId(final String applicationId);
}
