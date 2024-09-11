package de.joonko.loan.user.service;

import reactor.core.publisher.Mono;

public interface UserAdditionalInfoService {

    Mono<UserAdditionalInformationStore> save(UserAdditionalInformationStore userAdditionalInformationStore);
    Mono<UserAdditionalInformationStore> findById(String userUuid);
    Mono<UserAdditionalInformationStore> deleteById(final String userUuid);
}