package de.joonko.loan.integrations.domain;

import de.joonko.loan.integrations.configuration.GetOffersConfigurations;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.integrations.model.PersonalDataState;
import de.joonko.loan.integrations.model.UserState;
import de.joonko.loan.offer.OfferDemandRequest;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;

import org.springframework.stereotype.Component;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
@Slf4j
public class UserStateReducer {

    private final UserStatesStoreService userStatesStoreService;
    private final UserStateEvaluator userStateEvaluator;
    private final GetOffersConfigurations offersConfigurations;

    public Mono<OfferRequest> deriveUserState(OfferDemandRequest offerDemandRequest) {
        return userStatesStoreService.findById(offerDemandRequest.getUserUUID())
                .switchIfEmpty(Mono.defer(() -> Mono.just(this.createNewUser(offerDemandRequest.getUserUUID()))))
                .flatMap(userStates -> saveUserStates(userStates, offerDemandRequest.getRequestedLoanAmount(), offerDemandRequest.getRequestedLoanPurpose(), offerDemandRequest.isOnlyBonify()))
                .flatMap(userStates -> this.calculateOfferRequestState(offerDemandRequest, userStates))
                .doOnError(e -> log.error("Error in deriving user state for userId: {}", offerDemandRequest.getUserUUID(), e))
                .doOnNext(offerRequest -> log.info("Derived user state {} for userId: {}", offerRequest.getUserState(), offerDemandRequest.getUserUUID()));
    }

    private Mono<UserStatesStore> saveUserStates(UserStatesStore userStatesStore, int requestedLoanAmount, String requestedLoanPurpose, boolean isRequestedBonifyLoans) {
        return Mono.just(userStatesStore)
                .filter(userStates -> !Objects.equals(userStates.getLastRequestedLoanAmount(), requestedLoanAmount) ||
                        !Objects.equals(userStates.getLastRequestedPurpose(), requestedLoanPurpose) ||
                        !Objects.equals(userStates.getIsLastRequestedBonifyLoans(), isRequestedBonifyLoans))
                .map(userStates -> {
                    userStates.setLastRequestedLoanAmount(requestedLoanAmount);
                    userStates.setLastRequestedPurpose(requestedLoanPurpose);
                    userStates.setIsLastRequestedBonifyLoans(isRequestedBonifyLoans);
                    return userStates;
                }).flatMap(userStatesStoreService::save)
                .switchIfEmpty(Mono.just(userStatesStore));
    }

    private UserStatesStore createNewUser(String userUUID) {
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUUID);
        log.info("Creating new user states for userId: {}", userUUID);
        return userStatesStore;
    }

    private Mono<OfferRequest> calculateOfferRequestState(OfferDemandRequest offerDemandRequest, UserStatesStore userStates) {
        OfferRequest offerRequest = new OfferRequest(offerDemandRequest, userStates.getBonifyUserId());

        return Mono.just(offerRequest)
                .map(offerReq -> {
                    UserState userState = UserState.builder()
                            .offersState(userStateEvaluator.getOffersState(offerDemandRequest.getRequestedLoanAmount(), offerDemandRequest.getRequestedLoanPurpose(), userStates))
                            .dacDataState(userStateEvaluator.getTransactionalDataState(userStates))
                            .personalDataState(getPersonalDataState(offerDemandRequest.getRequestedLoanAmount(), userStates)).build();
                    offerReq.setUserState(userState);
                    return offerReq;
                });
    }

    private PersonalDataState getPersonalDataState(final int amount, final UserStatesStore userStates) {
        if (Boolean.TRUE.equals(offersConfigurations.getNewImplementationEnabled())) {
            return userStateEvaluator.getPersonalDataState(userStates);
        }
        return userStateEvaluator.getPersonalDataStateOld(userStates);
    }
}
