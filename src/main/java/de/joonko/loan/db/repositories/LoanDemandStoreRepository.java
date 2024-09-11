package de.joonko.loan.db.repositories;

import de.joonko.loan.db.vo.LoanDemandStore;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("loanDemandStoreRepository")
public interface LoanDemandStoreRepository extends CrudRepository<LoanDemandStore, String> {

    List<LoanDemandStore> findByUserUUID(String userUUID);

    LoanDemandStore deleteByApplicationId(final String applicationId);
}
