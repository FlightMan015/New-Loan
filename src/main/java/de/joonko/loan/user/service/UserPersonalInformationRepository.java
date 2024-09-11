package de.joonko.loan.user.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPersonalInformationRepository extends MongoRepository<UserPersonalInformationStore, String> {

    UserPersonalInformationStore deleteByUserUUID(final String userId);
}
