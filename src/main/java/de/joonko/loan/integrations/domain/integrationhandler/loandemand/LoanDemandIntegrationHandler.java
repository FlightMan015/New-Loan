package de.joonko.loan.integrations.domain.integrationhandler.loandemand;

import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.integrations.domain.integrationhandler.IntegrationHandler;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.metric.LoanDemandMetric;
import de.joonko.loan.metric.OffersStateMetric;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDemandGatewayFilter;
import de.joonko.loan.offer.domain.LoanDemandService;
import de.joonko.loan.user.states.OfferDataStateDetails;
import de.joonko.loan.user.states.Status;
import de.joonko.loan.user.states.UserStateService;
import de.joonko.loan.user.states.UserStatesStore;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Set;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static de.joonko.loan.user.states.Status.ERROR;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Component
@AllArgsConstructor
public class LoanDemandIntegrationHandler implements IntegrationHandler {

    private final LoanDemandIntegrationHandlerFilter handlerFilter;
    private final LoanDemandProviderService loanDemandProviderService;
    private final LoanDemandService loanDemandService;
    private final LoanOfferStoreService loanOfferStoreService;
    private final UserStateService userStateService;

    private final OffersStateMetric offersStateMetric;
    private final LoanDemandMetric loanDemandMetric;
    private final LoanDemandGatewayFilter loanDemandGatewayFilter;

    @Override
    public Mono<Void> triggerMutation(@NotNull OfferRequest offerRequest) {
        return Mono.just(offerRequest)
                .filter(handlerFilter)
                .flatMap(loanDemandProviderService::getLoanDemandFromOfferRequest)
                .doOnError(ex -> {
                    log.error("Failed building loan demand for userId: {}", offerRequest.getUserUUID(), ex);
                    addErrorOffersState(offerRequest);
                })
                .zipWhen(loanDemands -> addOffersStates(loanDemands, offerRequest.getUserUUID()))
                .flatMap(t -> fetchOffersFromProviders(t.getT1(), t.getT2()))
                .flatMap(offerStates -> userStateService.saveUpdatedOffersStates(offerRequest.getUserUUID(), offerStates))
                .then();
    }

    private Mono<Set<OfferDataStateDetails>> fetchOffersFromProviders(Set<LoanDemand> allLoanDemands, Set<OfferDataStateDetails> offersStates) {
        return Flux.fromIterable(allLoanDemands)
                .parallel()
                .runOn(Schedulers.elastic())
                .doOnNext(loanDemand -> log.debug("getting offers for applicationId: {}, userId: {}, amount: {}, isRecommended: {}", loanDemand.getLoanApplicationId(), loanDemand.getUserUUID(), loanDemand.getLoanAsked(), loanDemand.isRecommended()))
                .flatMap(this::fetchOffers)
                .flatMap(loanDemand -> updateOffersState(loanDemand, offersStates))
                .sequential()
                .collect(toSet());
    }

    private Mono<LoanDemand> fetchOffers(LoanDemand loanDemand) {
        final var validGateways = loanDemandGatewayFilter.filterValidGatewaysForLoanDemand(loanDemand);
        return loanDemandProviderService.savePrechecksToRequestAndPublish(loanDemand)
                .flatMapMany(demand -> loanDemandService.getLoanOffersForProviders(demand, validGateways))
                .collectList()
                .doOnNext(loanOffers -> log.info("got {} offers for applicationId: {}, userId: {}, amount: {}, isRecommended: {}", loanOffers.size(), loanDemand.getLoanApplicationId(), loanDemand.getUserUUID(), loanDemand.getLoanAsked(), loanDemand.isRecommended()))
                .filter(loanOffers -> !loanOffers.isEmpty())
                .flatMapMany(loanOffers -> loanOfferStoreService.saveAll(loanOffers, loanDemand.getUserUUID(), loanDemand.getLoanApplicationId(), loanDemand.getParentLoanApplicationId()))
                .then(Mono.just(loanDemand));
    }

    private Mono<OfferDataStateDetails> updateOffersState(LoanDemand loanDemand, Set<OfferDataStateDetails> offersStates) {
        return Flux.fromIterable(offersStates)
                .filter(offersState -> offersState.getApplicationId().equals(loanDemand.getLoanApplicationId()))
                .next()
                .map(offersState -> {
                    offersState.setState(Status.SUCCESS);
                    offersState.setResponseDateTime(OffsetDateTime.now());
                    return offersState;
                })
                .doOnNext(offersState -> offersStateMetric.addOffersStateTimer(loanDemand.getUserUUID(), offersState.getRequestDateTime()))
                .doOnNext(offersState -> loanDemandMetric.incrementCounterForEachLoanProvider(loanDemand.getLoanApplicationId(), loanDemand.isRecommended()));
    }

    private Mono<Set<OfferDataStateDetails>> addOffersStates(Set<LoanDemand> allLoanDemands, String userUuid) {
        return Flux.fromIterable(allLoanDemands)
                .map(loanDemand -> OfferDataStateDetails.builder()
                        .requestDateTime(OffsetDateTime.now())
                        .applicationId(loanDemand.getLoanApplicationId())
                        .parentApplicationId(loanDemand.getParentLoanApplicationId())
                        .purpose(loanDemand.getFundingPurpose())
                        .amount(loanDemand.getLoanAsked()).build())
                .collect(toSet())
                .flatMap(newOfferStates -> userStateService.addOffersStates(userUuid, newOfferStates))
                .map(UserStatesStore::getOfferDateStateDetailsSet);
    }

    private void addErrorOffersState(final OfferRequest offerRequest) {
        Mono.just(offerRequest)
                .map(request -> OfferDataStateDetails.builder()
                        .requestDateTime(OffsetDateTime.now())
                        .responseDateTime(OffsetDateTime.now())
                        .state(ERROR)
                        .purpose(offerRequest.getRequestedPurpose())
                        .amount(offerRequest.getRequestedAmount()).build())
                .flatMap(newOfferStates -> userStateService.addOffersStates(offerRequest.getUserUUID(), Set.of(newOfferStates)))
                .subscribe();
    }
}
