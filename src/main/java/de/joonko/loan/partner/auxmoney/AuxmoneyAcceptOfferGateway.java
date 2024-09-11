package de.joonko.loan.partner.auxmoney;

import de.joonko.loan.acceptoffer.domain.AcceptOfferGateway;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.partner.auxmoney.model.AuxmoneyAcceptOfferRequest;
import de.joonko.loan.partner.auxmoney.model.AuxmoneyAcceptOfferResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        value = "auxmoney.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class AuxmoneyAcceptOfferGateway implements AcceptOfferGateway<AuxmoneyAcceptOfferApiMapper, AuxmoneyAcceptOfferRequest, AuxmoneyAcceptOfferResponse> {

    private final AuxmoneyAcceptOfferApiMapper auxmoneyAcceptOfferApiMapper;

    @Override
    public AuxmoneyAcceptOfferApiMapper getMapper() {
        return auxmoneyAcceptOfferApiMapper;
    }

    @Override
    public Mono<AuxmoneyAcceptOfferResponse> callApi(AuxmoneyAcceptOfferRequest auxmoneyAcceptOfferRequest, String applicationId, String offerId) {
        log.info("Accepting the offer for Auxmoney with LoanApplication id  and offer Id", applicationId, offerId);
        return Mono.just(new AuxmoneyAcceptOfferResponse());
    }

    @Override
    public Mono<String> getLoanProviderReferenceNumber(String loanApplicationId, String loanOfferId) {
        return Mono.empty(); //TODO: Need to implement
    }

    @Override
    public Bank getBank() {
        return Bank.AUXMONEY;
    }
}
