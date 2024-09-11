package de.joonko.loan.partner.solaris;

import de.joonko.loan.acceptoffer.domain.AcceptOfferGateway;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.partner.solaris.auth.AccessToken;
import de.joonko.loan.common.partner.solaris.auth.SolarisAuthService;
import de.joonko.loan.customer.support.CustomerSupportService;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.service.IdentificationAuditService;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.partner.solaris.mapper.SolarisAcceptOfferApiMapper;
import de.joonko.loan.partner.solaris.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.retry.Repeat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        value = "solaris.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class SolarisAcceptOfferGateway implements AcceptOfferGateway<SolarisAcceptOfferApiMapper, SolarisAcceptOfferRequest, SolarisAcceptOfferResponse> {


    private final SolarisAcceptOfferApiMapper solarisAcceptOfferApiMapper;

    @Qualifier("solarisWebClientBuilder")
    private final WebClient.Builder solarisWebClientBuilder;

    private final SolarisAuthService solarisAuthService;

    private final SolarisPropertiesConfig solarisPropertiesConfig;
    private final SolarisStoreService solarisStoreService;
    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private final IdentificationAuditService identificationAuditService;
    private final CustomerSupportService customerSupportService;
    private final IdentificationLinkService identificationLinkService;

    @Override
    public SolarisAcceptOfferApiMapper getMapper() {
        return solarisAcceptOfferApiMapper;
    }

    @Override
    public Mono<SolarisAcceptOfferResponse> callApi(SolarisAcceptOfferRequest solarisAcceptOfferRequest, String applicationId, String offerId) {
        loanApplicationAuditTrailService.acceptOfferRequestSent(applicationId, Bank.DEUTSCHE_FINANZ_SOZIETÄT);
        log.info("Duration {} loanAsked {} ", solarisAcceptOfferRequest.getDuration(), solarisAcceptOfferRequest.getLoanAsked());

        return solarisAuthService.getToken(applicationId)
                .flatMap(accessToken -> acceptSolarisOffer(solarisAcceptOfferRequest, accessToken, applicationId))
                .doOnSuccess(solarisAcceptOfferResponse -> {
                    loanApplicationAuditTrailService.acceptOfferResponseReceivedSolaris(applicationId, solarisAcceptOfferResponse);
                    identificationAuditService.kycLinkCreatedSolaris(applicationId, solarisAcceptOfferResponse.getUrl());
                    identificationLinkService.add(applicationId, offerId, Bank.DEUTSCHE_FINANZ_SOZIETÄT.label, IdentificationProvider.SOLARIS, solarisAcceptOfferResponse.getIdentificationId(), solarisAcceptOfferResponse.getUrl());
                })
                .doOnError(throwable -> loanApplicationAuditTrailService.acceptOfferErrorResponseReceived(applicationId, throwable.getMessage(), Bank.DEUTSCHE_FINANZ_SOZIETÄT))
                .doOnNext(solarisAcceptOfferResponse -> {
                    log.info("Accept offer status from Solaris {} ", solarisAcceptOfferResponse);
                    if (null != solarisAcceptOfferResponse.getUrl() && null != solarisAcceptOfferResponse.getPreContract()) {
                        customerSupportService.pushKycStatus(applicationId, mapSolarisOfferToApiLoanOffer(applicationId), solarisAcceptOfferResponse.getUrl(), "link_created", applicationId, "");
                        loanApplicationAuditTrailService.solarisKycInitiated(applicationId);
                    }
                });
    }

    @Override
    public Mono<String> getLoanProviderReferenceNumber(String applicationId, String loanOfferId) {
        return Mono.just(applicationId);
    }

    private Mono<SolarisAcceptOfferResponse> acceptSolarisOffer(SolarisAcceptOfferRequest solarisAcceptOfferRequest, AccessToken accessToken, String id) {
        AtomicReference<String> personId = new AtomicReference<>();
        AtomicReference<String> solarisApplicationId = new AtomicReference<>();
        AtomicReference<String> selectedOfferId = new AtomicReference<>();

        List<SolarisGetOfferResponseStore> solarisOffers = solarisStoreService.getSolarisGetOfferResponseStoreByLoanApplicationId(id);
        Optional<SolarisGetOfferResponseStore> solarisGetOfferResponseStore = solarisOffers.stream().filter(offer -> offer.solarisGetOffersResponse.getOffer().getLoanTerm() == (solarisAcceptOfferRequest.getDuration().getValue()) &&
                        offer.solarisGetOffersResponse.getOffer().getLoanAmount().getValue() / 100 == (solarisAcceptOfferRequest.getLoanAsked()))
                .findFirst();

        if (solarisGetOfferResponseStore.isPresent()) {
            SolarisGetOffersResponse solarisGetOffersResponse = solarisGetOfferResponseStore.get().getSolarisGetOffersResponse();
            personId.set(solarisGetOffersResponse.getPersonId());
            selectedOfferId.set(solarisGetOffersResponse.getOffer().getId());
            solarisApplicationId.set(solarisGetOffersResponse.getId());

            WebClient client = solarisWebClientBuilder
                    .defaultHeaders(accessToken.bearer())
                    .build();

            return getApplicationStatus(client, personId.get(), solarisApplicationId.get(), id)
                    .flatMap(applicationStatus -> getKycDetails(client, applicationStatus.getSigningId(), personId.get(), id))
                    .flatMap(kycDetailsRes -> downloadContract(kycDetailsRes, client, personId.get(), solarisApplicationId.get(), selectedOfferId.get(), id, kycDetailsRes.getSigningId()))
                    .flatMap(kycDetailsRes -> downloadPreContract(kycDetailsRes, client, personId.get(), solarisApplicationId.get(), selectedOfferId.get(), id, kycDetailsRes.getSigningId()))
                    .doOnSuccess(solarisAcceptOfferResponse -> solarisStoreService.saveAcceptOfferResponse(solarisAcceptOfferResponse.getIdentificationId(), id, personId.get(), solarisAcceptOfferResponse.getSigningId()));
        } else {
            throw new RuntimeException("Can not find get offer response");
        }
    }

    private Mono<SolarisGetApplicationStatusResponse> getApplicationStatus(WebClient client, String personId, String solarisApplicationId, String applicationId) {
        log.info("Person id {}, applicationId {}", personId, solarisApplicationId);
        return client.get()
                .uri(uriBuilder -> buildUriForApplicationAndKycStatus(solarisPropertiesConfig.getPersonsEndpoint(), solarisPropertiesConfig.getLoanEndpoint(), personId, solarisApplicationId, uriBuilder))
                .retrieve()
                .bodyToMono(SolarisGetApplicationStatusResponse.class)
                .filter(res -> res.getStatus()
                        .equalsIgnoreCase(LoanStatus.ESIGN_PENDING.getStatus()))
                .repeatWhenEmpty(Repeat.onlyIf(r -> true)
                        .fixedBackoff(Duration.ofSeconds(5))
                        .timeout(Duration.ofSeconds(30))
                )
                .switchIfEmpty(Mono.just(SolarisGetApplicationStatusResponse.builder().signingId(null).build()))
                .doOnError(e -> updateApplicationStatus(applicationId, e.getMessage()));


    }

    private Mono<SolarisAcceptOfferResponse> getKycDetails(WebClient client, String signingId, String personId, String id) {
        log.info("Getting kyc status for signing id {} ", signingId);
        if (null == signingId) {
            SolarisAcceptOfferResponse solarisAcceptOfferResponse = new SolarisAcceptOfferResponse();
            solarisAcceptOfferResponse.setStatus(LoanStatus.REJECTED);
            updateApplicationStatus(id, "Unable to fetch signingId in 30 seconds");
            return Mono.just(solarisAcceptOfferResponse);
        }

        return client.get()
                .uri(uriBuilder -> buildUriForApplicationAndKycStatus(solarisPropertiesConfig.getPersonsEndpoint(), solarisPropertiesConfig.getSigningEndPoint(), personId, signingId, uriBuilder))
                .retrieve()
                .bodyToMono(SolarisAcceptOfferResponse.class)
                .doOnError(e -> updateApplicationStatus(id, e.getMessage()))
                .doOnSuccess(response -> response.setSigningId(signingId));
    }

    private Mono<SolarisAcceptOfferResponse> downloadPreContract(SolarisAcceptOfferResponse solarisAcceptOfferResponse, WebClient client, String personId, String solarisApplicationId, String selectedOfferId, String applicationId, String signingId) {

        return client.get()
                .uri(uriBuilder -> buildUriForDownloadContract(personId, solarisApplicationId, selectedOfferId, uriBuilder, true))
                .accept(MediaType.APPLICATION_PDF)
                .retrieve()
                .bodyToMono(byte[].class)
                .flatMap(res -> {
                    solarisAcceptOfferResponse.setPreContract(res);
                    return Mono.just(solarisAcceptOfferResponse);
                })
                .doOnError(e -> updateApplicationStatus(applicationId, e.getMessage()))
                .doOnSuccess(response -> response.setSigningId(signingId));
    }

    private Mono<SolarisAcceptOfferResponse> downloadContract(SolarisAcceptOfferResponse solarisAcceptOfferResponse, WebClient client, String personId, String solarisApplicationId, String selectedOfferId, String applicationId, String signingId) {

        return client.get()
                .uri(uriBuilder -> buildUriForDownloadContract(personId, solarisApplicationId, selectedOfferId, uriBuilder, false))
                .accept(MediaType.APPLICATION_PDF)
                .retrieve()
                .bodyToMono(byte[].class)
                .flatMap(res -> {
                    solarisAcceptOfferResponse.setContract(res);
                    return Mono.just(solarisAcceptOfferResponse);
                })
                .doOnError(e -> updateApplicationStatus(applicationId, e.getMessage()))
                .doOnSuccess(response -> response.setSigningId(signingId));
    }

    private URI buildUriForApplicationAndKycStatus(String endpoint1, String endpoint2, String id1, String id2, UriBuilder uriBuilder) {

        return uriBuilder.path(endpoint1)
                .pathSegment(id1)
                .path(endpoint2)
                .pathSegment(id2)
                .build();
    }

    private URI buildUriForDownloadContract(String personId, String applicationId, String offerid, UriBuilder uriBuilder, Boolean isPreContract) {

        return uriBuilder.path(solarisPropertiesConfig.getPersonsEndpoint())
                .pathSegment(personId)
                .path(solarisPropertiesConfig.getLoanEndpoint())
                .pathSegment(applicationId)
                .path("/offers")
                .pathSegment(offerid)
                .path(isPreContract ? "/pre_contract" : "/contract")
                .build();
    }

    @Override
    public Bank getBank() {
        return Bank.DEUTSCHE_FINANZ_SOZIETÄT;
    }

    private void updateApplicationStatus(String applicationId, String message) {
        loanApplicationAuditTrailService.saveApplicationError(applicationId, message, getBank().name());
    }

    private LoanOffer mapSolarisOfferToApiLoanOffer(String applicationId) {

        Offer offer = solarisStoreService.getSolarisGetOfferResponseStoreByLoanApplicationId(applicationId)
                .get(0)
                .getSolarisGetOffersResponse()
                .getOffer();
        return LoanOffer.builder()
                .amount(offer.getLoanAmount().getValue() / 100)
                .durationInMonth(offer.getLoanTerm())
                .effectiveInterestRate(BigDecimal.valueOf(offer.getEffectiveInterestRate() * 100).setScale(2, RoundingMode.HALF_EVEN))
                .loanProvider(new LoanProvider(Bank.DEUTSCHE_FINANZ_SOZIETÄT.label))
                .monthlyRate(BigDecimal.valueOf(offer.getMonthlyInstallment().getValue() / 100).setScale(2, RoundingMode.HALF_EVEN))
                .nominalInterestRate(BigDecimal.valueOf(offer.getIntertestRate() * 100).setScale(2, RoundingMode.HALF_EVEN))
                .totalPayment(BigDecimal.valueOf(offer.getApproximateTotalLoanExpenses().getValue() / 100).setScale(2, RoundingMode.HALF_EVEN))
                .build();
    }
}
