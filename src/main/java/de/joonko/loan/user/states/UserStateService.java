package de.joonko.loan.user.states;

import java.util.Set;

import reactor.core.publisher.Mono;

public interface UserStateService {
    Mono<UserStatesStore> saveUpdatedOffersStates(String userId, Set<OfferDataStateDetails> offerStates);

    Mono<UserStatesStore> addOffersStates(String userId, Set<OfferDataStateDetails> offerStates);

    Mono<UserStatesStore> saveUserPersonalDataState(String userId, StateDetails userPersonalDataState);

    Mono<UserStatesStore> saveUserStates(UserStatesStore userStatesStore);
}
