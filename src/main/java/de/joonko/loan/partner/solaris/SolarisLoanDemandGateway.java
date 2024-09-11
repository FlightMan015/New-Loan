package de.joonko.loan.partner.solaris;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.common.partner.FakeAccountSnapshotGenerator;
import de.joonko.loan.common.partner.solaris.auth.SolarisAuthService;
import de.joonko.loan.dac.fts.FTSAccountSnapshotGateway;
import de.joonko.loan.data.support.DataSupportService;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.exception.LoanDemandGatewayException;
import de.joonko.loan.offer.domain.DomainDefault;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDemandGateway;
import de.joonko.loan.partner.solaris.mapper.SolarisApiMapper;
import de.joonko.loan.partner.solaris.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.retry.Repeat;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
@VisibleForTesting
@ConditionalOnProperty(
        value = "solaris.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class SolarisLoanDemandGateway implements LoanDemandGateway<SolarisApiMapper, SolarisAllApiRequest, List<SolarisGetOffersResponse>> {

    private final SolarisApiMapper solarisApiMapper;
    private final SolarisPropertiesConfig solarisPropertiesConfig;
    private final SolarisAuthService solarisAuthService;
    private final SolarisStoreService solarisStoreService;
    private final FTSAccountSnapshotGateway ftsAccountSnapshotGateway;
    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private final DataSupportService dataSupportService;

    @Qualifier("solarisWebClientBuilder")
    private final WebClient.Builder solarisWebClientBuilder;

    @Override
    public LoanProvider getLoanProvider() {
        return LoanProvider.builder().name(Bank.DEUTSCHE_FINANZ_SOZIETÄT.getLabel()).build();
    }

    @Override
    public SolarisApiMapper getMapper() {
        return solarisApiMapper;
    }

    @Override
    /**
     * 1. Create Person Object
     * 2. Create creditRecord against person
     * 3. Get loan offer
     * 4. Upload accountSnapshot
     * 5. Update accountSnapshot against personId.
     * 6. Loop 5 times with an interval of 5 seconds until status changes from Offered to approved
     * 7. Filter the offer and return
     */
    public Mono<List<SolarisGetOffersResponse>> callApi(SolarisAllApiRequest solarisAllApiRequest, String applicationId) {
        loanApplicationAuditTrailService.sendingLoanDemandRequest(applicationId, Bank.DEUTSCHE_FINANZ_SOZIETÄT);
        log.info("Requesting to Solarisbank {} on Server {}", solarisPropertiesConfig.getHost());
        AtomicReference<WebClient> webClientAtomicReference = new AtomicReference<>();
        return solarisAuthService.getToken(applicationId)
                .flatMap(accessToken -> {
                    WebClient client = solarisWebClientBuilder
                            .defaultHeaders(accessToken.bearer())
                            .build();
                    webClientAtomicReference.set(client);
                    return createPerson(solarisAllApiRequest.getSolarisCreatePersonRequest(), webClientAtomicReference.get(), applicationId);
                })
                .flatMap(personObj -> postCreditRecord(webClientAtomicReference.get(), personObj.getId(), applicationId))
                .flatMap(creditRecord -> Flux.fromStream(solarisPropertiesConfig.getTweakSnapshot() ? Stream.of(LoanDuration.FORTY_EIGHT) : Stream.of(LoanDuration.values()))
                        .flatMap(loanDuration ->
                                getInitialOffers(solarisAllApiRequest.getSolarisGetOffersRequest().toBuilder().duration(loanDuration.value).creditRecordId(creditRecord.getId()).build(), webClientAtomicReference.get(), creditRecord.getPersonId(), applicationId)
                                        .doOnSuccess(solarisGetOffersResponse -> solarisStoreService.saveGetOfferResponse(solarisGetOffersResponse, applicationId)))
                        .collectList())
                .flatMap(offers -> {
                            log.info("Received initial offers from Solaris {} ", offers);
                            if (!offers.isEmpty()) {
                                return uploadAccountSnapshot(
                                        webClientAtomicReference.get(),
                                        solarisAllApiRequest.getSolarisGetOffersRequest().getFtsTransactionId(),
                                        offers.get(0).getPersonId(),
                                        applicationId,
                                        solarisAllApiRequest.getSolarisCreatePersonRequest().getFirstName())
                                        .flatMap(response -> Flux.fromStream(offers.stream()).delayElements(Duration.ofSeconds(1))
                                                .flatMap(offer -> checkForGreenOffer(webClientAtomicReference.get(), offer, response.getAccountSnapshotId(), offer.getPersonId(), applicationId))
                                                .collectList());
                            } else {
                                dataSupportService.pushRedOffersReceivedTopic(applicationId, Bank.DEUTSCHE_FINANZ_SOZIETÄT.toString(), "Profile Not Matched", "REJECT", "");
                                return Mono.empty();
                            }
                        }
                )
                .doOnError(throwable -> loanApplicationAuditTrailService.receivedLoanDemandResponseError(applicationId, throwable.getMessage(), Bank.DEUTSCHE_FINANZ_SOZIETÄT));
    }

    @Override
    public Boolean filterGateway(LoanDemand loanDemand) {
        return false;
    }

    @Override
    public List<de.joonko.loan.offer.domain.LoanDuration> getDurations(Integer loanAsked) {
        return List.of(de.joonko.loan.offer.domain.LoanDuration.FORTY_EIGHT);
    }

    private Mono<SolarisCreatePersonResponse> createPerson(SolarisCreatePersonRequest solarisCreatePersonRequest, WebClient client, String applicationId) {

        return client.post()
                .uri(solarisPropertiesConfig.getPersonsEndpoint())
                .bodyValue(solarisCreatePersonRequest)
                .retrieve()
                .bodyToMono(SolarisCreatePersonResponse.class)
                .onErrorMap(e -> {
                    updateApplicationStatus(applicationId, "Error while creating Person Object " + e.getMessage());
                    return new LoanDemandGatewayException("Error while creating Person Object ", e.getCause());
                })
                .doOnNext(res -> log.info("Person created with id {} ", res.getId()));

    }

    private Mono<SolarisGetOffersResponse> checkForGreenOffer(WebClient client, SolarisGetOffersResponse offer, String accountSnapshotId, String personId, String applicationId) {
        return updateAccountSnapshot(client, accountSnapshotId, personId, offer.getId(), applicationId)
                .flatMap(s -> getApplicationStatus(client, personId, offer, applicationId))
                .flatMap(applicationStatus -> {
                    if (Stream.of(LoanStatus.APPROVED.getStatus(), LoanStatus.ESIGN_PENDING.getStatus()).anyMatch(applicationStatus.getStatus()::equalsIgnoreCase)) {
                        applicationStatus.getSolarisGetOffersResponse()
                                .setLoanDecision(LoanStatus.APPROVED.getStatus());
                        loanApplicationAuditTrailService.receivedAsGreenProfileSolaris(applicationId, offer.getId());
                        return Mono.just(applicationStatus.getSolarisGetOffersResponse());
                    }
                    loanApplicationAuditTrailService.receivedAsRedProfileSolaris(applicationId, applicationStatus.getStatus(), offer.getId());
                    dataSupportService.pushRedOffersReceivedTopic(applicationId, Bank.DEUTSCHE_FINANZ_SOZIETÄT.toString(), applicationStatus.getStatusDescription(), applicationStatus.getStatus(), applicationStatus.toString());
                    return Mono.empty();
                })
                .onErrorMap(e -> {
                    updateApplicationStatus(applicationId, "Error while Checking for Green offer " + e.getMessage());
                    return new LoanDemandGatewayException("Error while Checking for Green offer ", e.getCause());
                });

    }

    private Mono<SolarisCreateCreditRecordResponse> postCreditRecord(WebClient client, String personId, String applicationId) {
        SolarisCreateCreditRecordRequest solarisCreateCreditRecordRequest = new SolarisCreateCreditRecordRequest("solarisBank");

        return client.post()
                .uri(uriBuilder -> buildUri(solarisPropertiesConfig.getPersonsEndpoint(), solarisPropertiesConfig.getCreditEndpoint(), personId, uriBuilder))
                .bodyValue(solarisCreateCreditRecordRequest)
                .retrieve()
                .bodyToMono(SolarisCreateCreditRecordResponse.class)
                .doOnError(e -> updateApplicationStatus(applicationId, "Error while creating Credit Request " + e.getMessage()))
                .onErrorMap(e -> new LoanDemandGatewayException("Error while creating Credit Request ", e))
                .doOnNext(res -> log.info("Credit record posted with id {} ", res.getId()));
    }

    private Mono<SolarisGetOffersResponse> getInitialOffers(SolarisGetOffersRequest offerRequest, WebClient client, String personId, String applicationId) {
        return client.post()
                .uri(uriBuilder -> buildUri(solarisPropertiesConfig.getPersonsEndpoint(), solarisPropertiesConfig.getLoanEndpoint(), personId, uriBuilder))
                .bodyValue(offerRequest)
                .retrieve()
                .bodyToMono(SolarisGetOffersResponse.class)
                .filter(offer -> offer.getLoanDecision()
                        .equalsIgnoreCase("OFFERED"))
                .doOnError(e -> updateApplicationStatus(applicationId, "Error while getting offers " + e.getMessage()))
                .onErrorMap(e -> new LoanDemandGatewayException("Error while getting offers ", e))
                .doOnNext(offer -> {
                    log.info("Received offer {}", offer);
                    offer.setPersonId(personId);
                });
    }

    private Mono<SolarisCreateAccountSnapshotResponse> uploadAccountSnapshot(WebClient client, String ftsTransactionId, String personId, String applicationId, String firstName) {
        log.info("Uploading account Snapshot for person {} ", personId);
        SolarisUploadAccoutSnapRequest solarisUploadAccoutSnapRequest = SolarisUploadAccoutSnapRequest.builder().source("PARTNER").build();

        if (solarisPropertiesConfig.getTweakSnapshot()) {
            solarisUploadAccoutSnapRequest.setAccountSnapshot(FakeAccountSnapshotGenerator.getFakeSolarisSnapshot());
        } else {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> jsonMap = mapper.readValue(ftsAccountSnapshotGateway.getAccountSnapshot(ftsTransactionId, DomainDefault.FTS_QUERY_PARAM_VALUE_JSON), Map.class);
                AccountSnapshot accountSnapshot = mapper.convertValue(jsonMap, AccountSnapshot.class);
                //TODO This can be deleted once we get confirmation from FTS regarding this.
                if (null == accountSnapshot.getBalance().getLimit()) {
                    accountSnapshot.getBalance().setLimit(Double.valueOf(0));
                    log.info("Limit is null {} ", accountSnapshot.getBalance().getLimit());
                }
                solarisUploadAccoutSnapRequest.setAccountSnapshot(accountSnapshot);
            } catch (IOException e) {
                updateApplicationStatus(applicationId, "Error while reading snapshot from FTS" + e.getMessage());
                throw new LoanDemandGatewayException("Error while reading snapshot from FTS");
            }
        }

        return client.post()
                .uri(uriBuilder -> buildUriForUploadAccountSnap(personId, uriBuilder))
                .bodyValue(solarisUploadAccoutSnapRequest)
                .retrieve()
                .bodyToMono(SolarisCreateAccountSnapshotResponse.class)
                .map(createAccountSnapRes -> {
                    createAccountSnapRes.setPersonId(personId);
                    return createAccountSnapRes;
                })
                .doOnError(e -> updateApplicationStatus(applicationId, e.getMessage()));
    }

    private Mono<String> updateAccountSnapshot(WebClient client, String accountSnapshotId, String personId, String solarisApplicationId, String applicationId) {
        log.info("Updating snapshot against solarisApplictionId {} and personId {} ", solarisApplicationId, personId);
        SolarisAccountSnapshotUpdateRequest solarisAccountSnapshotUpdateRequest = new SolarisAccountSnapshotUpdateRequest(accountSnapshotId);

        return client.put()
                .uri(uriBuilder -> buildUriForUpdateAccountSnap(personId, solarisApplicationId, uriBuilder))
                .bodyValue(solarisAccountSnapshotUpdateRequest)
                .exchange()
                .flatMap(response -> {
                    if (response.statusCode()
                            .is2xxSuccessful()) {
                        return Mono.just("SUCCESS");
                    } else {
                        String exceptionMsg = "Error updating the account snapshot";
                        updateApplicationStatus(applicationId, exceptionMsg);
                        throw new RuntimeException(exceptionMsg);
                    }
                });
    }

    private Mono<SolarisGetApplicationStatusResponse> getApplicationStatus(WebClient client, String personId, SolarisGetOffersResponse offer, String applicationId) {
        log.info("Fetching status for person {} having SolarisapplicationId {} and offerId {} ", personId, offer.getId(), offer.getOffer()
                .getId());

        return client.get()
                .uri(uriBuilder -> buildUriForApplicationAndKycStatus(solarisPropertiesConfig.getPersonsEndpoint(), solarisPropertiesConfig.getLoanEndpoint(), personId, offer.getId(), uriBuilder))
                .retrieve()
                .bodyToMono(SolarisGetApplicationStatusResponse.class)
                .filter(res -> {
                    log.info("Status is {} with description {}", res.getStatus(), res.getStatusDescription());
                    res.setSolarisGetOffersResponse(offer);
                    return Stream.of(LoanStatus.APPROVED.getStatus(), LoanStatus.ESIGN_PENDING.getStatus()).anyMatch(res.getStatus()::equalsIgnoreCase);
                })
                .repeatWhenEmpty(Repeat.onlyIf(r -> true)
                        .fixedBackoff(Duration.ofSeconds(solarisPropertiesConfig.getTweakSnapshot() ? 20 : 5))
                        .timeout(Duration.ofSeconds(solarisPropertiesConfig.getTweakSnapshot() ? 100 : 30)) //temporary solution until Solaris fixes this
                )
                .switchIfEmpty(Mono.just(SolarisGetApplicationStatusResponse.builder().signingId(null).status(LoanStatus.REJECTED.getStatus()).build()))
                .doOnError(e -> updateApplicationStatus(applicationId, e.getMessage()));

    }

    private void updateApplicationStatus(String applicationId, String message) {
        loanApplicationAuditTrailService.saveApplicationError(applicationId, message, Bank.DEUTSCHE_FINANZ_SOZIETÄT.label);
    }

    private URI buildUri(String endpoint1, String endpoint2, String id1, UriBuilder uriBuilder) {

        return uriBuilder.path(endpoint1)
                .pathSegment(id1)
                .path(endpoint2)
                .build();
    }

    private URI buildUriForUploadAccountSnap(String personId, UriBuilder uriBuilder) {

        return uriBuilder.path(solarisPropertiesConfig.getPersonsEndpoint())
                .pathSegment(personId)
                .path(solarisPropertiesConfig.getAccountSnapshotEndpoint())
                .build();
    }

    private URI buildUriForUpdateAccountSnap(String personId, String solarisApplicationId, UriBuilder uriBuilder) {

        return uriBuilder.path(solarisPropertiesConfig.getPersonsEndpoint())
                .pathSegment(personId)
                .path(solarisPropertiesConfig.getLoanEndpoint())
                .pathSegment(solarisApplicationId)
                .path(solarisPropertiesConfig.getAccountSnapshotUpdateEndpoint())
                .build();

    }

    private URI buildUriForApplicationAndKycStatus(String endpoint1, String endpoint2, String id1, String id2, UriBuilder uriBuilder) {

        return uriBuilder.path(endpoint1)
                .pathSegment(id1)
                .path(endpoint2)
                .pathSegment(id2)
                .build();

    }

}
