package de.joonko.loan.identification.service.idnow;

import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.CreateIdentResponse;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.model.idnow.IDNowCreateIdentResponse;
import de.joonko.loan.identification.model.idnow.IDNowJwtToken;
import de.joonko.loan.identification.model.idnow.IdNowAccount;
import de.joonko.loan.identification.service.IdentService;
import de.joonko.loan.identification.service.IdentificationAuditService;
import de.joonko.loan.identification.service.IdentificationLinkService;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public abstract class IDNowIdentService implements IdentService {

    protected final IdNowClientApi idNowClientApi;
    protected final IdentificationAuditService identificationAuditService;
    private final IdentificationPropConfig identificationPropConfig;
    private final IdentificationLinkService identificationLinkService;

    public abstract Mono<de.joonko.loan.identification.model.idnow.CreateIdentRequest> getIdentRequest(CreateIdentRequest createIdentRequest);

    public abstract IdNowAccount getAccountId();

    public abstract Mono<Documents> getDocuments(CreateIdentRequest createIdentRequest);

    public Mono<String> partnerSpecificPostProcessing(String identId, CreateIdentRequest createIdentRequest) {
        log.debug("No partner Specific Post Processing Needed! ");
        return Mono.just(identId);
    }

    public String getTransactionId(String applicationId) {
        return applicationId;
    }

    @Override
    public Mono<CreateIdentResponse> createIdent(CreateIdentRequest createIdentRequest) {
        return idNowClientApi.getJwtToken(getAccountId())
                .map(IDNowJwtToken::getAuthToken)
                .zipWhen(authToken -> sendCreateIdent(authToken, createIdentRequest))
                .zipWhen(tuple -> uploadDocuments(createIdentRequest, tuple.getT1(), tuple.getT2()))
                .flatMap(tuple2 -> partnerSpecificPostProcessing(tuple2.getT1().getT2(), createIdentRequest).map(any -> tuple2))
                .map(tuple2 -> new CreateIdentResponse(buildKycUrl(tuple2.getT1().getT2()), getProvider(), Documents.builder().documents(tuple2.getT2()).build()));
    }

    @Override
    public Mono<String> getIdentStatus(String transactionId) {
        return idNowClientApi.getJwtToken(getAccountId())
                .flatMap(idNowJwtToken -> idNowClientApi.getIdent(getAccountId(), idNowJwtToken.getAuthToken(), transactionId))
                .map(getIdentResponse -> getIdentResponse.getIdentificationProcess().getResult())
                .doOnError(throwable -> log.error("Failed getting ident status for {}, transactionId: {}", getAccountId(), transactionId, throwable))
                .onErrorResume(WebClientResponseException.NotFound.class, throwable -> Mono.just("CANCELLED"));
    }

    @Override
    public IdentificationProvider getProvider() {
        return IdentificationProvider.ID_NOW;
    }

    private Mono<String> sendCreateIdent(String authToken, CreateIdentRequest createIdentRequest) {
        return getIdentRequest(createIdentRequest)
                .flatMap(identRequest -> idNowClientApi.createIdent(getAccountId(), authToken, getTransactionId(createIdentRequest.getApplicationId()), identRequest))
                .map(IDNowCreateIdentResponse::getId)
                .doOnSuccess(identId -> log.info("Created Ident for applicationId: {} and identId: {}", createIdentRequest.getApplicationId(), identId))
                .doOnSuccess(identId -> identificationAuditService.identCreatedSuccess(identId, createIdentRequest, getProvider()))
                .doOnSuccess(identId -> identificationLinkService.add(
                        createIdentRequest.getApplicationId(),
                        createIdentRequest.getLoanOfferId(),
                        createIdentRequest.getLoanProvider(),
                        getProvider(),
                        getTransactionId(createIdentRequest.getApplicationId()),
                        buildKycUrl(identId))
                ).doOnError(throwable -> identificationAuditService.identCreationFailure(throwable, createIdentRequest, getProvider()));
    }

    private Mono<List<Document>> uploadDocuments(CreateIdentRequest createIdentRequest, String authToken, String identId) {
        log.info("Start uploading bank documents for {} for applicationId: {}, identId: {}", getAccountId(), createIdentRequest.getApplicationId(), identId);

        return getDocuments(createIdentRequest)
                .doOnError(e -> log.error("Failed getting bank documents for {}, applicationId: {}", getAccountId(), createIdentRequest.getApplicationId(), e))
                .doOnSuccess(contract -> log.info("Successfully getting {} bank documents for applicationId: {}", getAccountId(), createIdentRequest.getApplicationId()))
                .flatMapIterable(Documents::getDocuments)
                .flatMap(document -> idNowClientApi.uploadDocument(getAccountId(), authToken, getTransactionId(createIdentRequest.getApplicationId()), document))
                .doOnNext(any -> identificationAuditService.contractUploadSuccess(createIdentRequest))
                .doOnNext(any -> log.info("Documents uploaded for {}, applicationId: {}, identId: {}", getAccountId(), createIdentRequest.getApplicationId(), identId))
                .doOnError(throwable -> log.error("Failed uploading documents for {}, applicationId: {}", getAccountId(), createIdentRequest.getApplicationId(), throwable))
                .doOnError(throwable -> identificationAuditService.contractUploadFail(createIdentRequest, throwable.getMessage(), getProvider()))
                .collectList();
    }

    private String buildKycUrl(String identId) {
        return identificationPropConfig.getIdentificationHost() + identId;
    }


}
