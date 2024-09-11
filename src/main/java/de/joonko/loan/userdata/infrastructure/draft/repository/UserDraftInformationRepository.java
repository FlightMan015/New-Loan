package de.joonko.loan.userdata.infrastructure.draft.repository;

import de.joonko.loan.userdata.infrastructure.draft.model.UserDraftInformationStore;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDraftInformationRepository extends CrudRepository<UserDraftInformationStore, String> {
}
