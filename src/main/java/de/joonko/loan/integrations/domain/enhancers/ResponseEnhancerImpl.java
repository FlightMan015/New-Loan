package de.joonko.loan.integrations.domain.enhancers;

import de.joonko.loan.integrations.model.DacDataState;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.integrations.model.PersonalDataState;
import de.joonko.loan.offer.api.model.OfferResponseState;
import de.joonko.loan.offer.api.model.OffersResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;

import static de.joonko.loan.integrations.model.DacDataState.FTS_DATA_EXISTS;
import static de.joonko.loan.integrations.model.OffersState.*;
import static de.joonko.loan.integrations.model.PersonalDataState.USER_INPUT_REQUIRED;
import static java.util.Optional.of;

@Slf4j
@Component
public class ResponseEnhancerImpl implements ResponseEnhancer {

    @Autowired
    private OffersReadyResponseEnhancer offersReadyResponseEnhancer;

    @Autowired
    private PersonalDetailsResponseEnhancer personalDetailsResponseEnhancer;

    @Autowired
    private AccountDetailsResponseEnhancer accountDetailsResponseEnhancer;

    @Autowired
    private WaitingResponseEnhancer waitingResponseEnhancer;

    @Autowired
    private FetchingOffersResponseEnhancer fetchingOffersResponseEnhancer;

    @Override
    public Mono<OffersResponse> buildResponseData(final OfferRequest offerRequest) {
        return Mono.just(offerRequest)
                .filter(this::isNotErrorState)
                .switchIfEmpty(Mono.error(new RuntimeException("ERROR: Unable to recover from error")))
                .doOnError(e -> log.error("Unable to recover from error for userId: {}", offerRequest.getUserUUID()))
                .map(this::getStrategy)
                .flatMap(responseEnhancer -> responseEnhancer.buildResponseData(offerRequest)
                        .doOnNext(response -> log.debug("Response status for userId: {}, {}", offerRequest.getUserUUID(), response.getState())));
    }

    @Override
    public OfferResponseState getState() {
        return OfferResponseState.WAITING;
    }

    private boolean isNotErrorState(OfferRequest offerRequest) {
        return of(offerRequest.getUserState())
                .filter(state -> state.getDacDataState() != DacDataState.ERROR)
                .filter(state -> state.getOffersState() != ERROR)
                .filter(state -> state.getPersonalDataState() != PersonalDataState.ERROR)
                .isPresent();
    }

    private ResponseEnhancer<?> getStrategy(final OfferRequest offerRequest) {
        if (offersExistPath(offerRequest)) {
            return offersReadyResponseEnhancer;
        } else if (offersFetchingInProgressPath(offerRequest)) {
            // TODO write test cases
            return fetchingOffersResponseEnhancer;
        } else if (bankAccountMissingPath(offerRequest)) {
            return accountDetailsResponseEnhancer;
        } else if (userDataMissingPath(offerRequest) && transactionalDataReadyPath(offerRequest)) {
            return personalDetailsResponseEnhancer;
        } else {
            return waitingResponseEnhancer;
        }
    }

    private boolean offersExistPath(final OfferRequest offerRequest) {
        return OFFERS_EXIST == offerRequest.getUserState().getOffersState();
    }

    private boolean offersFetchingInProgressPath(final OfferRequest offerRequest) {
        return IN_PROGRESS == offerRequest.getUserState().getOffersState();
    }

    private boolean userDataMissingPath(final OfferRequest offerRequest) {
        return USER_INPUT_REQUIRED == offerRequest.getUserState().getPersonalDataState();
    }

    private boolean bankAccountMissingPath(final OfferRequest offerRequest) {
        return Set.of(DacDataState.MISSING_SALARY_ACCOUNT, DacDataState.NO_ACCOUNT_ADDED).contains(offerRequest.getUserState().getDacDataState());
    }

    private boolean transactionalDataReadyPath(final OfferRequest offerRequest) {
        return FTS_DATA_EXISTS == offerRequest.getUserState().getDacDataState();
    }
}
