package de.joonko.loan.partner.postbank;

import de.joonko.loan.acceptoffer.domain.AcceptOfferGateway;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.exception.PostBankException;
import de.joonko.loan.partner.postbank.model.PostbankAcceptOfferRequest;
import de.joonko.loan.partner.postbank.model.PostbankAcceptOfferResponse;

import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        value = "postbank.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class PostbankAcceptOfferGateway implements AcceptOfferGateway<PostbankAcceptOfferApiMapper, PostbankAcceptOfferRequest, PostbankAcceptOfferResponse> {

    private final PostbankAcceptOfferApiMapper postbankAcceptOfferApiMapper;
    private final LoanOfferStoreService loanOfferStoreService;
    private final PostbankLoanDemandStoreService postbankLoanDemandStoreService;

    @Override
    public PostbankAcceptOfferApiMapper getMapper() {
        return postbankAcceptOfferApiMapper;
    }

    @Override
    public Mono<PostbankAcceptOfferResponse> callApi(PostbankAcceptOfferRequest postbankAcceptOfferRequest, String applicationId, String offerId) {
        log.info("POSTBANK: Received accept offer request.Nothing to do.");
        return Mono.just(new PostbankAcceptOfferResponse());
    }

    @Override
    public Mono<String> getLoanProviderReferenceNumber(String applicationId, String loanOfferId) {
        return loanOfferStoreService.findById(loanOfferId)
                .flatMap(ignore -> postbankLoanDemandStoreService.findByApplicationId(applicationId))
                .map(PostbankLoanDemandStore::getContractNumber)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new PostBankException("Failed getting loan provider reference number: " + applicationId))))
                .doOnError(e -> log.error("Failed getting loan provider reference number for applicationId: {}", applicationId));
    }

    @Override
    public Bank getBank() {
        return Bank.POSTBANK;
    }
}
