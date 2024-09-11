package de.joonko.loan.user.service;

import reactor.core.publisher.Mono;

public interface UserDeletionService {

    Mono<Void> deleteUser(final String userUUID);

}
