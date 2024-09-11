package de.joonko.loan.partner.santander;

import de.joonko.loan.db.service.LoanOfferStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@ConditionalOnProperty(
        value = "santander.enabled",
        havingValue = "true",
        matchIfMissing = true)

public class SantanderAcceptOfferGateway extends SantanderBaseAcceptOfferGateway {

    public SantanderAcceptOfferGateway(
            SantanderAcceptOfferApiMapper acceptOfferApiMapper,
            SantanderStoreService santanderStoreService,
            LoanOfferStoreService loanOfferStoreService) {
        super(acceptOfferApiMapper, santanderStoreService, loanOfferStoreService);
    }

    @Override
    public Mono<SantanderAcceptOfferResponse> callApi(SantanderAcceptOfferRequest santanderAcceptOfferRequest, String applicationId, String offerId) {
        log.info("Accepting offer for Santander for loanApplication id {}, and offer id {}", applicationId, offerId);
        return Mono.just(new SantanderAcceptOfferResponse());
    }
}
