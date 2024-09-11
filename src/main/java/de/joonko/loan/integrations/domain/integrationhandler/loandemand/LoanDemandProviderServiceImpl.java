package de.joonko.loan.integrations.domain.integrationhandler.loandemand;

import de.joonko.loan.data.support.DataSolutionCommunicationManager;
import de.joonko.loan.db.repositories.LoanDemandRequestRepository;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.offer.api.GetOffersMapper;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanRecommendationEngine;
import de.joonko.loan.user.states.UserStatesStoreService;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static java.util.stream.Collectors.toSet;

@AllArgsConstructor
@Service
@Slf4j
public class LoanDemandProviderServiceImpl implements LoanDemandProviderService {

    private final LoanDemandRequestBuilder loanDemandRequestBuilder;
    private final LoanDemandStoreService loanDemandStoreService;
    private final LoanDemandRequestRepository loanDemandRequestRepository;
    private final DataSolutionCommunicationManager dataSolutionCommunicationManager;
    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private final LoanRecommendationEngine loanRecommendationEngine;
    private final GetOffersMapper getOffersMapper;
    private final UserStatesStoreService userStatesStoreService;

    @Override
    public Mono<Set<LoanDemand>> getLoanDemandFromOfferRequest(OfferRequest offerRequest) {
        return Mono.just(offerRequest)
                .flatMap(offerReq -> loanDemandRequestBuilder.build(offerRequest))
                .doOnError(ex -> log.error("Failed building loan demand request", ex))
                .flatMapMany(this::getAllRequests)
                .doOnNext(loanDemandRequest -> loanApplicationAuditTrailService.loanDemandReceived(loanDemandRequest.getApplicationId()))
                .flatMap(this::storeLoanDemandRequest)
                .map(this::mapToLoanDemand)
                .collect(toSet())
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<LoanDemand> savePrechecksToRequestAndPublish(final LoanDemand loanDemand) {
        return Mono.fromCallable(() -> loanDemandRequestRepository.findByApplicationId(loanDemand.getLoanApplicationId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap(loanDemandRequest -> {
                    loanDemandRequest.setPreChecks(loanDemand.getPreChecks());
                    return storeLoanDemandRequest(loanDemandRequest);
                })
                .zipWhen(loanDemandRequest -> userStatesStoreService.findById(loanDemand.getUserUUID()))
                .flatMap(tuple -> sendLoanDemandRequest(tuple.getT2().getBonifyUserId(), tuple.getT1()))
                .map(ignored -> loanDemand)
                .subscribeOn(Schedulers.elastic());
    }

    private Flux<LoanDemandRequest> getAllRequests(final LoanDemandRequest loanDemandRequest) {
        return Mono.just(loanDemandRequest)
                .flatMap(requested -> storeLoanDemand(requested, requested.getUserUUID()))
                .zipWhen(this::getRecommended)
                .flatMapMany(t -> Flux.fromStream(Stream.concat(Stream.of(t.getT1()), t.getT2().stream())));
    }

    private Mono<Set<LoanDemandRequest>> getRecommended(final LoanDemandRequest requested) {
        return Flux.fromIterable(loanRecommendationEngine.recommend(requested))
                .map(recommended -> {
                    recommended.setParentApplicationId(requested.getApplicationId());
                    return recommended;
                }).flatMap(recommended -> storeLoanDemand(recommended, recommended.getUserUUID()))
                .collect(toSet())
                .doOnNext(recommendations -> log.info("recommended {} loan demands for userId: {}, applicationId: {}", recommendations.size(), requested.getUserUUID(), requested.getApplicationId()));
    }

    private LoanDemand mapToLoanDemand(LoanDemandRequest loanDemandRequest) {
        LoanDemand loanDemand = getOffersMapper.fromRequest(loanDemandRequest, loanDemandRequest.getApplicationId(), loanDemandRequest.getUserUUID());
        loanDemand.setParentLoanApplicationId(loanDemandRequest.getParentApplicationId());

        return loanDemand;
    }

    private Mono<LoanDemandRequest> storeLoanDemandRequest(LoanDemandRequest loanDemandRequest) {
        return Mono.fromCallable(() -> loanDemandRequestRepository.save(loanDemandRequest));
    }

    private Mono<LoanDemandRequest> sendLoanDemandRequest(Long bonifyUserId, LoanDemandRequest loanDemandRequest) {
        return Mono.fromRunnable(() -> dataSolutionCommunicationManager.sendLoanDemandRequest(bonifyUserId, loanDemandRequest))
                .then(Mono.just(loanDemandRequest));
    }

    private Mono<LoanDemandRequest> storeLoanDemand(LoanDemandRequest loanDemandRequest, String userUuid) {
        return Mono.fromCallable(() -> loanDemandStoreService.saveLoanDemand(loanDemandRequest, false, userUuid))
                .map(loanDemandStore -> {
                    loanDemandRequest.setApplicationId(loanDemandStore.getApplicationId());
                    return loanDemandRequest;
                });
    }
}
