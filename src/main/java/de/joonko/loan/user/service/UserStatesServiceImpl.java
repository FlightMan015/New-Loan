package de.joonko.loan.user.service;

import de.joonko.loan.offer.api.model.UserJourneyStateResponse;
import de.joonko.loan.user.states.UserStatesStoreService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserStatesServiceImpl implements UserStatesService {

    private final UserStatesStoreService userStatesStoreService;

    @Override
    public Mono<UserJourneyStateResponse> getLatestUserJourneyState(final String userUUID) {
        return userStatesStoreService.findById(userUUID)
                .filter(userState -> userState.getLastRequestedLoanAmount() != null)
                .map(userState -> UserJourneyStateResponse.builder()
                        .amount(userState.getLastRequestedLoanAmount())
                        .purpose(userState.getLastRequestedPurpose())
                        .state(UserJourneyStateResponse.UserJourneyState.EXISTING_LOAN_AMOUNT)
                        .build())
                .switchIfEmpty(Mono.just(UserJourneyStateResponse.builder()
                        .state(UserJourneyStateResponse.UserJourneyState.MISSING_LOAN_AMOUNT)
                        .build()));
    }
}
