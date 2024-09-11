package de.joonko.loan.user.service;

import de.joonko.loan.offer.api.model.UserJourneyStateResponse;
import reactor.core.publisher.Mono;

public interface UserStatesService {

    Mono<UserJourneyStateResponse> getLatestUserJourneyState(final String userUUID);
}
