package de.joonko.loan.identification.service.webid;

import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.CreateIdentResponse;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.model.webid.uploaddocument.UploadDocumentRequest;
import de.joonko.loan.identification.model.webid.useractionrequest.CreateUserActionRequest;
import de.joonko.loan.identification.model.webid.useractionresponse.CreateUserActionResponse;
import de.joonko.loan.identification.service.IdentService;
import de.joonko.loan.identification.service.IdentificationAuditService;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.metric.ApiMetric;
import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Base64;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public abstract class WebIDIdentService implements IdentService {

    @Qualifier("WebIdWebClient")
    protected final WebClient webIdWebClient;
    protected final IdentificationAuditService identificationAuditService;
    protected final IdentificationLinkService identificationLinkService;
    protected final LoanOfferStoreService loanOfferStoreService;
    private final ApiMetric apiMetric;


    protected abstract CreateUserActionRequest getIdentRequest(CreateIdentRequest createIdentRequest);

    protected abstract Mono<byte[]> fetchContract(CreateIdentRequest createIdentRequest, CreateUserActionResponse createUserActionResponse);

    @Override
    public Mono<CreateIdentResponse> createIdent(CreateIdentRequest createIdentRequest) {
        return sendCreateUserActionRequest(createIdentRequest)
                .zipWhen(createUserActionResponse -> this.uploadContract(createIdentRequest, createUserActionResponse))
                .doOnSuccess(tuple -> identificationAuditService.contractUploadSuccess(createIdentRequest))
                .doOnSuccess(tuple -> identificationLinkService.add(createIdentRequest.getApplicationId(), createIdentRequest.getLoanOfferId(), createIdentRequest.getLoanProvider(), IdentificationProvider.WEB_ID, createIdentRequest.getApplicationId(), tuple.getT1().getUrl()))
                .doOnError(throwable -> identificationAuditService.contractUploadFail(createIdentRequest, throwable.getMessage(), getProvider()))
                .map(tuple -> new CreateIdentResponse(tuple.getT1().getUrl(), IdentificationProvider.WEB_ID, Documents.builder().documents(List.of(tuple.getT2())).build()));
    }

    @Override
    public Mono<String> getIdentStatus(String externalIdentId) {
        String acceptedOfferId = identificationLinkService.getByExternalIdentId(externalIdentId).getOfferId();
        LoanOfferStore acceptedOffer = loanOfferStoreService.findByLoanOfferId(acceptedOfferId);
        return Mono.justOrEmpty(acceptedOffer.getKycStatus());
    }

    @Override
    public IdentificationProvider getProvider() {
        return IdentificationProvider.WEB_ID;
    }

    private Mono<CreateUserActionResponse> sendCreateUserActionRequest(CreateIdentRequest createIdentRequest) {
        return this.webIdWebClient.post()
                .uri("/api/v2/user-actions")
                .bodyValue(getIdentRequest(createIdentRequest))
                .retrieve()
                .bodyToMono(CreateUserActionResponse.class)
                .doOnSuccess(res -> log.info("Created WebId Ident {} for application id {}", res.getActionId(), createIdentRequest.getApplicationId()))
                .doOnSuccess(res -> identificationAuditService.identCreatedSuccess(res.getActionId(), createIdentRequest, getProvider()))
                .doOnError(throwable -> logWebException(throwable, "error while creating user action"))
                .doOnError(throwable -> identificationAuditService.identCreationFailure(throwable, createIdentRequest, getProvider()));
    }

    public Mono<Document> uploadContract(CreateIdentRequest createIdentRequest, CreateUserActionResponse createUserActionResponse) {
        String fileName = String.format("bonify-%s-%s.pdf", createIdentRequest.getLoanOfferId(), createUserActionResponse.getActionId());
        return this.fetchContract(createIdentRequest, createUserActionResponse)
                .doOnSuccess(response -> log.info("Contract fetched successfully, now uploading to webid"))
                .flatMap(bytes -> sendDocument(bytes, fileName, createUserActionResponse.getActionId(), createIdentRequest.getLoanOfferId()));
    }

    private Mono<Document> sendDocument(final byte[] contract, final String filename, final String actionId, final String loanOfferId) {
        UploadDocumentRequest uploadDocumentRequest = UploadDocumentRequest.builder()
                .fileName(filename)
                .fileContent(Base64.getEncoder().encodeToString(contract))
                .build();

        return this.webIdWebClient.put()
                .uri(uriBuilder -> uriBuilder.path("/api/v2/user-actions").pathSegment(actionId).path("documents").build())
                .bodyValue(List.of(uploadDocumentRequest))
                .retrieve()
                .onStatus(httpStatus -> {
                    apiMetric.incrementStatusCounter(httpStatus, ApiComponent.WEB_ID, ApiName.UPLOAD_DOCUMENT);
                    return false;
                }, clientResponse -> Mono.empty())
                .toBodilessEntity()
                .doOnSuccess(any -> log.info("WEBID: User contract uploaded for loan offer with id- {}", loanOfferId))
                .doOnError(ex -> logWebException(ex, String.format("WEBID: Error while uploading document for loan offer with id - %s", loanOfferId)))
                .map(any -> Document.builder()
                        .content(contract)
                        .documentId("Contract")
                        .build());
    }

    private void logWebException(Throwable throwable, String message) {
        if (throwable instanceof WebClientResponseException) {
            log.info("WebId: {} : {} - {}", message, throwable.getMessage(), ((WebClientResponseException) throwable).getResponseBodyAsString());
        } else {
            log.info("WebId: {} : {}", message, throwable.getMessage());
        }
    }
}