package de.joonko.loan.user.service;

import reactor.core.publisher.Mono;

public interface UserPersonalInfoService {

    Mono<UserPersonalInformationStore> save(UserPersonalInformationStore userPersonalInformationStore);

    Mono<UserPersonalInformationStore> findById(final String userId);

    Mono<UserPersonalInformationStore> deleteById(final String userId);
}
