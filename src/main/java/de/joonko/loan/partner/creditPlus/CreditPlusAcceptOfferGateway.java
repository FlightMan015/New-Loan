package de.joonko.loan.partner.creditPlus;

import de.joonko.loan.acceptoffer.domain.AcceptOfferGateway;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.partner.creditPlus.mapper.model.CreditPlusAcceptedOffer;
import de.joonko.loan.partner.creditPlus.mapper.model.CreditPlusOffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        value = "creditplus.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class CreditPlusAcceptOfferGateway implements AcceptOfferGateway<CreditPlusAcceptOfferMapper, CreditPlusAcceptOfferRequest, CreditPlusAcceptOfferResponse> {

    private final CreditPlusAcceptOfferMapper creditPlusAcceptOfferMapper;

    private final CreditPlusStoreService creditPlusStoreService;

    private final LoanOfferStoreService loanOfferStoreService;

    @Override
    public CreditPlusAcceptOfferMapper getMapper() {
        return creditPlusAcceptOfferMapper;
    }

    @Override
    public Mono<CreditPlusAcceptOfferResponse> callApi(CreditPlusAcceptOfferRequest creditPlusAcceptOfferRequest, String applicationId,String offerId) {
        log.info("Accepting offer for Creditplus for loanApplication id {}, and offer id {}", creditPlusAcceptOfferRequest.getLoanApplicationId(), creditPlusAcceptOfferRequest.getOfferId());
        CreditPlusOffer offer = creditPlusStoreService.findByApplicationId(applicationId)
                .stream()
                .filter(creditPlusOffer -> creditPlusOffer.getCreditOffer().getDuration() == creditPlusAcceptOfferRequest.getDuration())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Error retrieving offer from Credit plus offer store for duration -- " + creditPlusAcceptOfferRequest.getDuration()));
        creditPlusStoreService.saveAcceptedOffer(CreditPlusAcceptedOffer.builder().creditOffer(offer.getCreditOffer()).applicationId(applicationId).build());
        return Mono.just(new CreditPlusAcceptOfferResponse());
    }

    @Override
    public Mono<String> getLoanProviderReferenceNumber(String applicationId, String loanOfferId) {
        LoanOfferStore acceptedOffer = loanOfferStoreService.findByLoanOfferId(loanOfferId);
        return Mono.just(String.valueOf(creditPlusStoreService.getCpTransactionNumber(applicationId, acceptedOffer.getOffer().getDurationInMonth())));
    }

    @Override
    public Bank getBank() {
        return Bank.CREDIT_PLUS;
    }
}
