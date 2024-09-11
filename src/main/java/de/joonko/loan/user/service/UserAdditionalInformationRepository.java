package de.joonko.loan.user.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAdditionalInformationRepository extends MongoRepository<UserAdditionalInformationStore, String> {
    UserAdditionalInformationStore deleteByUserUUID(final String userUuid);
}
