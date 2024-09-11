package de.joonko.loan.partner.postbank;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.dac.fts.FTSAccountSnapshotGateway;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanDemandRequestService;
import de.joonko.loan.exception.PostBankException;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDemandGateway;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.postbank.mapper.PostbankLoanProviderApiMapper;
import de.joonko.loan.partner.postbank.model.exceptions.PostbankOfferNotAvailableRuntimeException;
import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequestSoapEnvelope;
import de.joonko.loan.partner.postbank.model.response.LoanDemandPostbankResponseBody;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStore;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStoreService;

import de.joonko.loan.webhooks.postbank.model.ContractState;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.rmi.RemoteException;
import java.time.Duration;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import static de.joonko.loan.util.JsonMapperUtil.getBase64Encoded;


@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        value = "postbank.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class PostbankLoanDemandGateway implements LoanDemandGateway<PostbankLoanProviderApiMapper, LoanDemandPostbankRequestSoapEnvelope, PostbankLoanDemandStore> {

    private final PostbankLoanProviderApiMapper postbankLoanProviderApiMapper;

    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;

    private final PostbankClient postbankClient;

    private final PostbankPrecheckFilter precheckFilter;

    private final PostbankLoanDemandStoreService postbankLoanDemandStoreService;

    private final PostbankPropertiesConfig postbankPropertiesConfig;

    private final LoanDemandRequestService loanDemandRequestService;

    private final FTSAccountSnapshotGateway ftsAccountSnapshotGateway;

    @Override
    public PostbankLoanProviderApiMapper getMapper() {
        return postbankLoanProviderApiMapper;
    }

    @Override
    public Mono<PostbankLoanDemandStore> callApi(LoanDemandPostbankRequestSoapEnvelope loanDemandPostbankRequestSoapEnvelope, String applicationId) {
        loanApplicationAuditTrailService.sendingLoanDemandRequest(applicationId, Bank.POSTBANK);
        log.info("Requesting to Postbank for applicationId {}", applicationId);

        return fetchFtsDataAndSetInRequest(loanDemandPostbankRequestSoapEnvelope, applicationId)
                .flatMap(request -> requestLoanOffers(request, applicationId))
                .flatMap(store -> pollForOffersWithRetry(applicationId));
    }

    private Mono<PostbankLoanDemandStore> requestLoanOffers(LoanDemandPostbankRequestSoapEnvelope request, String applicationId) {
        return postbankClient.requestLoanOffers(request, applicationId)
                .flatMap(this::mapToStoreSaveAndReturn)
                .filter(this::isSuccessfulCall)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new PostBankException("Failed sending request loan offers for applicationId: " + applicationId))))
                .doOnError(e -> log.error("Failed sending request loan offers for applicationId: {}", applicationId));
    }

    private Mono<PostbankLoanDemandStore> pollForOffersWithRetry(final String applicationId) {
        return pollForOffers(applicationId)
                .retryWhen(Retry.backoff(postbankPropertiesConfig.getOfferResponseRetryMaxAttempts(), Duration.ofSeconds(postbankPropertiesConfig.getOfferResponseRetryMaxDelay()))
                        .filter(PostbankOfferNotAvailableRuntimeException.class::isInstance));
    }

    private Mono<PostbankLoanDemandStore> pollForOffers(final String applicationId) {
        return postbankLoanDemandStoreService.findByApplicationId(applicationId)
                .doOnNext(postbankLoanDemandStore -> log.debug(String.format("Polling for offers from Postbank for applicationId - %s", applicationId)))
                .filter(postbankLoanDemandStore -> postbankLoanDemandStore.getCreditResults().stream()
                        .anyMatch(credit -> credit.getContractState() == ContractState.getSuccessState()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new PostbankOfferNotAvailableRuntimeException(String.format("POSTBANK: No loan offer is available for applicationId - %s", applicationId)))));
    }

    private boolean isSuccessfulCall(PostbankLoanDemandStore postbankLoanDemandStore) {
        final var postbankResponseMessages = postbankLoanDemandStore.getStatus().getMessages();

        final var successfulRequest = postbankResponseMessages.stream()
                .anyMatch(msg -> "Auszahlungsdatum wurde korrigiert!".equals(msg.getMessage()));

        final var noDacError = postbankResponseMessages.stream()
                .noneMatch(msg -> msg.getMessage().startsWith("dac-error"));

        return successfulRequest && noDacError;
    }

    private Mono<PostbankLoanDemandStore> mapToStoreSaveAndReturn(final LoanDemandPostbankResponseBody response) {
        return Mono.just(response)
                .map(this::mapToStore)
                .flatMap(postbankLoanDemandStoreService::save);
    }

    private Mono<LoanDemandPostbankRequestSoapEnvelope> fetchFtsDataAndSetInRequest(LoanDemandPostbankRequestSoapEnvelope loanDemandPostbankRequestSoapEnvelope, String applicationId) {
        final var ftsData = loanDemandPostbankRequestSoapEnvelope.getBody().getContract().getCredit().getRequest().getFtsData();

        return loanDemandRequestService.findLoanDemandRequest(applicationId)
                .map(LoanDemandRequest::getFtsTransactionId)
                .doOnNext(transactionId -> log.info("POSTBANK : Fetching accountSnapshot for transactionId {}", transactionId))
                .flatMap(ftsAccountSnapshotGateway::fetchAccountSnapshotJson)
                .map(ftsRawData -> {
                    try {
                        ftsData.setDocument(getBase64Encoded(ftsRawData));
                    } catch (JsonProcessingException e) {
                        throw new PostBankException(String.format("POSTBANK: Error occurred while mapping ftsRawData to json for applicationId - %s, error message - %s, error cause - %s", applicationId, e.getMessage(), e.getCause()));
                    }
                    return loanDemandPostbankRequestSoapEnvelope;
                });
    }

    private PostbankLoanDemandStore mapToStore(final LoanDemandPostbankResponseBody response) {
        return PostbankLoanDemandStore.builder()
                .applicationId(response.getApplicationId())
                .contractNumber(response.getContractNumber())
                .status(response.getStatus())
                .build();
    }

    @Override
    public LoanProvider getLoanProvider() {
        return LoanProvider.builder().name(Bank.POSTBANK.getLabel()).build();
    }

    @Override
    public Boolean filterGateway(LoanDemand loanDemand) throws RemoteException {
        return !precheckFilter.test(loanDemand);
    }

    @Override
    public List<LoanDuration> getDurations(Integer loanAsked) {
        if (loanAsked < 10000) {
            return List.of(LoanDuration.FORTY_EIGHT);
        } else if (loanAsked < 30000) {
            return List.of(LoanDuration.SEVENTY_TWO);
        }
        return List.of(LoanDuration.EIGHTY_FOUR);
    }
}
