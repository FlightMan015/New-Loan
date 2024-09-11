package de.joonko.loan.partner.consors;

import de.joonko.loan.acceptoffer.domain.AcceptOfferGateway;
import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.exception.ErrorResponseException;
import de.joonko.loan.partner.consors.mapper.ConsorsAcceptOfferApiMapper;
import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferRequest;
import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferResponse;
import de.joonko.loan.partner.consors.model.LinkRelation;

import de.joonko.loan.partner.consors.model.SubscriptionStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        value = "consors.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class ConsorsAcceptOfferGateway implements AcceptOfferGateway<ConsorsAcceptOfferApiMapper, ConsorsAcceptOfferRequest, ConsorsAcceptOfferResponse> {

    private final ConsorsAcceptOfferApiMapper consorsAcceptOfferApiMapper;

    private final ConsorsStoreService consorsStoreService;
    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private final ConsorsClient consorsClient;
    private final ConsorsNotificationSenderService consorsNotificationSenderService;

    private final LoanOfferStoreService loanOfferStoreService;

    @Override
    public ConsorsAcceptOfferApiMapper getMapper() {
        return consorsAcceptOfferApiMapper;
    }

    @Override
    public Mono<ConsorsAcceptOfferResponse> callApi(ConsorsAcceptOfferRequest consorsAcceptOfferRequest, String applicationId, String offerId) {
        loanApplicationAuditTrailService.acceptOfferRequestSent(applicationId, getBank());
        LinkRelation link = consorsStoreService.getFinalizeSubscriptionLinkForApplicationId(applicationId);

        return acceptOffer(consorsAcceptOfferRequest, link, applicationId)
                .doOnSuccess(acceptOfferResponse -> log.info("Accept offer status from Consors {} for contract id {} and applicationId {}", acceptOfferResponse.getSubscriptionStatus(), acceptOfferResponse.getContractIdentifier(), applicationId))
                .flatMap(acceptOfferResponse -> handleNotApprovedOffer(acceptOfferResponse, offerId))
                .filter(acceptOfferResponse -> acceptOfferResponse.getSubscriptionStatus() == SubscriptionStatus.APPROVED)
                .switchIfEmpty(Mono.error(new ErrorResponseException(String.format("Received not approved response from Consors for offerId: %s, applicationId: %s", offerId, applicationId))))
                .doOnSuccess(acceptOfferResponse -> consorsStoreService.saveAcceptedOffer(applicationId, acceptOfferResponse))
                .doOnSuccess(acceptOfferResponse -> loanApplicationAuditTrailService.acceptOfferResponseReceivedConsors(applicationId, acceptOfferResponse))
                .doOnSuccess(acceptOfferResponse -> consorsNotificationSenderService.sendEmailWithAccountSnapshot(acceptOfferResponse.getContractIdentifier(), applicationId))
                .doOnError(throwable -> loanApplicationAuditTrailService.acceptOfferErrorResponseReceived(applicationId, throwable.getMessage(), getBank()));
    }

    private Mono<ConsorsAcceptOfferResponse> handleNotApprovedOffer(ConsorsAcceptOfferResponse acceptOfferResponse, String offerId) {
        return Mono.just(acceptOfferResponse.getSubscriptionStatus())
                .filter(status -> status == SubscriptionStatus.REFUSED || status == SubscriptionStatus.STUDY)
                .map(status -> LoanApplicationStatus.REJECTED)
                .flatMap(status -> loanOfferStoreService.updateOffer(offerId, acceptOfferResponse.getContractIdentifier(), status))
                .thenReturn(acceptOfferResponse);
    }

    @Override
    public Mono<String> getLoanProviderReferenceNumber(String applicationId, String loanOfferId) {
        return consorsStoreService.getConsorsContractIdentifier(applicationId);
    }

    private Mono<ConsorsAcceptOfferResponse> acceptOffer(ConsorsAcceptOfferRequest consorsAcceptOfferRequest, LinkRelation linkRelation, String applicationId) {
        return consorsClient.getToken(applicationId)
                .flatMap(token -> consorsClient.finalizeSubscription(token, linkRelation, consorsAcceptOfferRequest, applicationId));
    }

    @Override
    public Bank getBank() {
        return Bank.CONSORS;
    }


}
