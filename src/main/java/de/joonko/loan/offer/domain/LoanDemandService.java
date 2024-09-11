package de.joonko.loan.offer.domain;

import de.joonko.loan.metric.LoanDemandMetric;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class LoanDemandService {

    private final LoanDemandMetric metric;

    public Flux<LoanOffer> getLoanOffersForProviders(final LoanDemand loanDemand, final Set<LoanDemandGateway> gateways) {
        log.info("Requesting loan offers for userId: {}, applicationId: {}", loanDemand.getUserUUID(), loanDemand.getLoanApplicationId());
        return Flux.fromIterable(gateways)
                .parallel()
                .runOn(Schedulers.elastic())
                .flatMap(getOffersForEachDuration(loanDemand))
                .sequential()
                .doOnError(err -> log.error("Error while processing offers for applicationId: {}", loanDemand.getLoanApplicationId(), err))
                .onErrorResume(e -> Mono.empty());
    }

    private Function<LoanDemandGateway, Flux<LoanOffer>> getOffersForEachDuration(LoanDemand loanDemand) {
        final var requestDateTime = OffsetDateTime.now();

        return gateway ->
                Flux.fromIterable(gateway.getDurations(loanDemand.getLoanAsked()))
                        .parallel()
                        .runOn(Schedulers.elastic())
                        .flatMap(duration -> gateway.getLoanOffers(loanDemand, (LoanDuration) duration)
                                .doOnError(error -> log.error("Error Occurred when getting the loan offers from gateway - {}, for duration - {}", gateway.getLoanProvider(), duration))
                                .onErrorResume(o -> Mono.empty()))
                        .sequential()
                        .collectList()
                        .doOnNext(any -> metric.addTimer(gateway.getLoanProvider(), requestDateTime, gateway.getCallApiProcessName()))
                        .flatMapIterable(list -> list);
    }

}
