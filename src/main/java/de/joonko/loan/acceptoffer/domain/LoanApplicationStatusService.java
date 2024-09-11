package de.joonko.loan.acceptoffer.domain;

import de.joonko.loan.common.domain.Bank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

import static de.joonko.loan.acceptoffer.domain.LoanApplicationStatus.UNDEFINED;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoanApplicationStatusService {

    private final List<LoanApplicationStatusGateway> gateways;

    public Mono<LoanApplicationStatus> getStatus(OfferRequest offerRequest) {
        log.info("Getting status for loan offers, offerId - {}", offerRequest.getLoanOfferId());
        final var gateway = findGatewayForOffer(offerRequest);

        return Mono.just(offerRequest)
                .flatMap((Function<OfferRequest, Mono<LoanApplicationStatus>>) gateway::getStatus)
                .doOnError(err -> log.error("error happened while fetching status for offerId: {}", offerRequest.getLoanOfferId(), err))
                .onErrorResume(err -> Mono.just(UNDEFINED));
    }

    private LoanApplicationStatusGateway findGatewayForOffer(final OfferRequest offerRequest) {
        return gateways.stream()
                .filter(gateway -> gateway.getBank().equals(Bank.fromLabel(offerRequest.getLoanProvider())))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Loan application status can not be fetched, as offer bank was not mapped to a gateway: " + offerRequest.getLoanProvider()));
    }
}
