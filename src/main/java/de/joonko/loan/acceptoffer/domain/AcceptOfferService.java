package de.joonko.loan.acceptoffer.domain;

import de.joonko.loan.acceptoffer.api.AcceptOfferRequest;
import de.joonko.loan.acceptoffer.api.AcceptOfferResponse;
import de.joonko.loan.acceptoffer.api.OfferRequestMapper;
import de.joonko.loan.acceptoffer.api.SaveConsentRequest;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanDemandRequestService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.db.vo.OfferAcceptedEnum;
import de.joonko.loan.offer.api.GetOffersMapper;
import de.joonko.loan.partner.solaris.SolarisStoreService;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static java.util.Optional.of;

@Component
@Slf4j
@RequiredArgsConstructor
public class AcceptOfferService {

    private final List<AcceptOfferGateway> gateways;

    private final LoanOfferStoreService loanOfferStoreService;
    private final SolarisStoreService solarisStoreService;
    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private final LoanDemandRequestService loanDemandRequestService;
    private final GetOffersMapper getOffersMapper;
    private final OfferRequestMapper offerRequestMapper;

    public Mono<AcceptOfferResponse> acceptOffer(final AcceptOfferRequest acceptOfferRequest, final String userUUID) {

        return loanOfferStoreService.findById(acceptOfferRequest.getLoanOfferId())
                .map(loanOffer -> {
                    if (!Objects.equals(loanOffer.getUserUUID(), userUUID)) {
                        throw new IllegalStateException(String.format("unable to find loan offer store %s for user %s", acceptOfferRequest.getLoanOfferId(), userUUID));
                    }
                    return loanOffer;
                })
                .doOnNext(loanOffer -> {
                    log.info("user {} want to accept Loan offer for amount {} and duration {} ", userUUID, loanOffer.getOffer().getAmount(), loanOffer.getOffer().getDurationInMonth());
                    loanApplicationAuditTrailService.acceptOfferRequestReceived(loanOffer.getApplicationId(), loanOffer.getOffer().getLoanProvider().getName());
                })
                .flatMap(this::getApplicationAndBuildOfferRequest)
                .flatMap(offerRequest -> acceptOffer(offerRequest, of(userUUID)))
                .map(offerRequestMapper::toResponse)
                .doOnError(WebClientResponseException.class, ex -> log.error(ex.getResponseBodyAsString()));
    }

    public Mono<LoanOfferStore> acceptOfferByUser(final String offerId, final String userUUID) {
        return loanOfferStoreService.findById(offerId)
                .map(loanOffer -> {
                    if (!Objects.equals(loanOffer.getUserUUID(), userUUID)) {
                        throw new IllegalStateException(String.format("unable to find loan offer store %s for user %s", offerId, userUUID));
                    }
                    return loanOffer;
                })
                .flatMap(loanOfferStore -> {
                    if (Boolean.TRUE.equals(loanOfferStore.getIsAccepted()) && loanOfferStore.getLoanProviderReferenceNumber() != null) {
                        log.info("Tried to accept offer - {} by user, but the offer was already accepted", offerId);
                        return Mono.just(loanOfferStore);
                    }
                    return updateLoanOfferToAccepted(loanOfferStore, of(userUUID));
                })
                .doOnError(WebClientResponseException.class, ex -> log.error(ex.getResponseBodyAsString()));
    }

    public Mono<LoanOfferStore> acceptOfferByInternalUser(final String offerId) {
        return loanOfferStoreService.findById(offerId)
                .flatMap(loanOfferStore -> {
                    if (Boolean.TRUE.equals(loanOfferStore.getIsAccepted()) && loanOfferStore.getLoanProviderReferenceNumber() != null) {
                        log.info("Tried to accept offer - {} by internal user, but the offer was already accepted", offerId);
                        return Mono.just(loanOfferStore);
                    }
                    return updateLoanOfferToAccepted(loanOfferStore, Optional.empty());
                })
                .doOnError(WebClientResponseException.class, ex -> log.error(ex.getResponseBodyAsString()));
    }

    private Mono<LoanOfferStore> updateLoanOfferToAccepted(final LoanOfferStore loanOffer, final Optional<String> userUUID) {
        return Mono.just(loanOffer)
                .doOnNext(loanOfferStore -> {
                    if (userUUID.isPresent()) {
                        log.info("user {} wants to accept Loan offer for amount {} and duration {} ", userUUID.get(), loanOffer.getOffer().getAmount(), loanOffer.getOffer().getDurationInMonth());
                        loanApplicationAuditTrailService.acceptOfferRequestReceived(loanOffer.getApplicationId(), loanOffer.getOffer().getLoanProvider().getName());
                    } else {
                        log.info("Accepting offer - {} by Internal user", loanOfferStore.getLoanOfferId());
                        loanApplicationAuditTrailService.acceptOfferByInternalUserRequestReceived(loanOfferStore.getApplicationId(), loanOfferStore.getOffer().getLoanProvider().getName());
                    }
                })
                .flatMap(loanOfferStore ->
                        getApplicationAndBuildOfferRequest(loanOfferStore)
                                .flatMap(offerRequest -> acceptOfferV2(offerRequest, userUUID))
                );
    }

    private Mono<OfferRequest> getApplicationAndBuildOfferRequest(final LoanOfferStore loanOffer) {
        return loanDemandRequestService.findLoanDemandRequest(loanOffer.getApplicationId())
                .map(loanDemandRequest -> {
                    OfferRequest offerRequest = offerRequestMapper.fromRequest(loanOffer);
                    offerRequest.setLoanDemand(getOffersMapper.fromRequest(loanDemandRequest, loanOffer.getApplicationId(), loanOffer.getUserUUID()));

                    return offerRequest;
                });
    }

    private Mono<OfferStatus> acceptOffer(final OfferRequest offerRequest, final Optional<String> userUUID) {
        log.info("Accepting loan offers. Request: {}", offerRequest);
        final var acceptOfferGateway = findGateway(offerRequest);

        return acceptOfferGateway.acceptOffer(offerRequest)
                .zipWhen(os -> acceptOfferGateway.getLoanProviderReferenceNumber(offerRequest.getApplicationId(), offerRequest.getLoanOfferId()))
                .flatMap(tuple -> loanOfferStoreService.updateAcceptedStatus(offerRequest.getLoanOfferId(), tuple.getT2(), userUUID.map(user -> OfferAcceptedEnum.USER).orElse(OfferAcceptedEnum.INTERNAL))
                        .map(offer -> tuple.getT1()))
                .doOnSuccess(offerStatus -> loanApplicationAuditTrailService.acceptOfferRequestServedSuccess(offerRequest.getApplicationId(), offerRequest.getLoanOfferId(), offerRequest.getLoanProvider()))
                .doOnSuccess(offerStatus -> log.info("offer {} is accepted successfully by user {}", offerRequest.getLoanOfferId(), userUUID.orElse("INTERNAL")))
                .doOnError(err -> log.error("error happened while user {} accepting offer {}", userUUID.orElse("Internal"), offerRequest.getLoanOfferId(), err));
    }

    private Mono<LoanOfferStore> acceptOfferV2(final OfferRequest offerRequest, final Optional<String> userUUID) {
        log.info("Accepting loan offers. Request: {}", offerRequest);
        final var acceptOfferGateway = findGateway(offerRequest);

        return acceptOfferGateway.acceptOffer(offerRequest)
                .zipWhen(os -> acceptOfferGateway.getLoanProviderReferenceNumber(offerRequest.getApplicationId(), offerRequest.getLoanOfferId()))
                .flatMap(tuple -> loanOfferStoreService.updateAcceptedStatus(offerRequest.getLoanOfferId(), tuple.getT2(), userUUID.map(user -> OfferAcceptedEnum.USER).orElse(OfferAcceptedEnum.INTERNAL)))
                .doOnSuccess(offerStatus -> loanApplicationAuditTrailService.acceptOfferRequestServedSuccess(offerRequest.getApplicationId(), offerRequest.getLoanOfferId(), offerRequest.getLoanProvider()))
                .doOnSuccess(offerStatus -> log.info("offer {} is accepted successfully by user {}", offerRequest.getLoanOfferId(), userUUID.orElse("INTERNAL")))
                .doOnError(err -> log.error("error happened while user {} accepting offer {}", userUUID.orElse("Internal"), offerRequest.getLoanOfferId(), err));
    }

    private AcceptOfferGateway<?, ?, ?> findGateway(OfferRequest offerRequest) {
        return gateways.stream()
                .filter(gateway -> gateway.getBank().equals(Bank.fromLabel(offerRequest.getLoanProvider())))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Bank cannot be mapped to gateway: " + offerRequest.getLoanProvider()));
    }

    public boolean saveConsent(SaveConsentRequest saveConsentRequest) {
        return solarisStoreService.updateConsent(saveConsentRequest.getLoanApplicationId(), saveConsentRequest.getTermsAccepted());
    }
}
