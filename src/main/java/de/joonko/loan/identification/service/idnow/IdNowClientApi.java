package de.joonko.loan.identification.service.idnow;

import de.joonko.loan.identification.exception.IdentificationFailureException;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.idnow.*;
import de.joonko.loan.metric.ApiMetric;
import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.print.Doc;

import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdNowClientApi {

    @Qualifier("idNowWebClient")
    private final WebClient idNowWebClient;
    private final IdNowAccountMapper idNowAccountMapper;
    private final ApiMetric apiMetric;

    private static final String API_TOKEN_HEADER = "X-API-LOGIN-TOKEN";

    public Mono<IDNowJwtToken> getJwtToken(IdNowAccount account) {
        IDNowGetTokenRequest tokenRequest = IDNowGetTokenRequest.builder()
                .apiKey(idNowAccountMapper.getApiKey(account))
                .build();

        return idNowWebClient
                .post()
                .uri("/api/v1/" + idNowAccountMapper.getAccountId(account) + "/login")
                .bodyValue(tokenRequest)
                .retrieve()
                .onStatus(httpStatus -> {
                    apiMetric.incrementStatusCounter(httpStatus, ApiComponent.ID_NOW, ApiName.AUTHORIZATION);
                    return false;
                }, clientResponse -> Mono.empty())
                .bodyToMono(IDNowJwtToken.class)
                .doOnError(throwable -> log.error("Failed getting token for account: {}", account, throwable))
                .onErrorMap(throwable -> new IdentificationFailureException("Error while getting token {}", throwable))
                .doOnSuccess(idNowJwtToken -> log.info("Received auth token for account: {}", account));
    }

    public Mono<DocumentDefinition[]> getDocumentDefinitions(IdNowAccount account, String authToken) {
        return idNowWebClient
                .get()
                .uri("/api/v1/" + idNowAccountMapper.getAccountId(account) + "/documentdefinitions")
                .header(API_TOKEN_HEADER, authToken)
                .retrieve()
                .onStatus(httpStatus -> {
                    apiMetric.incrementStatusCounter(httpStatus, ApiComponent.ID_NOW, ApiName.GET_DOCUMENT_DEFINITIONS);
                    return false;
                }, clientResponse -> Mono.empty())
                .bodyToMono(DocumentDefinition[].class)
                .doOnError(throwable -> log.error("Failed getting document definitions for account: {}", account, throwable))
                .doOnSuccess(documentDefinition -> log.info("Received document definitions for account: {}", account));
    }

    public Mono<Void> createDocumentDefinition(IdNowAccount account, String authToken, DocumentDefinition documentDefinition) {
        return idNowWebClient.post()
                .uri("/api/v1/" + idNowAccountMapper.getAccountId(account) + "/documentdefinitions")
                .contentType(MediaType.APPLICATION_JSON)
                .header(API_TOKEN_HEADER, authToken)
                .bodyValue(documentDefinition)
                .retrieve()
                .onStatus(httpStatus -> {
                    apiMetric.incrementStatusCounter(httpStatus, ApiComponent.ID_NOW, ApiName.CREATE_DOCUMENT_DEFINITION);
                    return false;
                }, clientResponse -> Mono.empty())
                .toBodilessEntity()
                .doOnError(throwable -> log.error("Failed creating document definition for account: {}", account, throwable))
                .doOnSuccess(clientResponse -> log.info("Document definition created for account: {}", account))
                .then();
    }

    public Mono<GetIdentResponse> getIdent(IdNowAccount account, String authToken, String transactionId) {
        return idNowWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/")
                        .path(idNowAccountMapper.getAccountId(account))
                        .path("/identifications/")
                        .path(transactionId).build())
                .header(API_TOKEN_HEADER, authToken)
                .retrieve()
                .onStatus(httpStatus -> {
                    apiMetric.incrementStatusCounter(httpStatus, ApiComponent.ID_NOW, ApiName.GET_IDENT);
                    return false;
                }, clientResponse -> Mono.empty())
                .bodyToMono(GetIdentResponse.class)
                .doOnError(throwable -> log.error("Failed getting Ident for account: {}, transactionId: {}", account, transactionId, throwable))
                .doOnSuccess(clientResponse -> log.info("Got ident for account: {}, transactionId: {}", account, transactionId));
    }

    public Mono<IDNowCreateIdentResponse> createIdent(IdNowAccount account, String authToken, String transactionId, CreateIdentRequest createIdentRequest) {
        return idNowWebClient.post()
                .uri("/api/v1/" + idNowAccountMapper.getAccountId(account) + "/identifications/" + transactionId + "/start")
                .header(API_TOKEN_HEADER, authToken)
                .bodyValue(createIdentRequest)
                .retrieve()
                .onStatus(httpStatus -> {
                    apiMetric.incrementStatusCounter(httpStatus, ApiComponent.ID_NOW, ApiName.CREATE_IDENT);
                    return false;
                }, clientResponse -> Mono.empty())
                .bodyToMono(IDNowCreateIdentResponse.class)
                .doOnError(throwable -> log.error("Failed creating Ident for account: {}, transactionId: {}", account, transactionId, throwable))
                .doOnSuccess(idNowCreateIdentResponse -> log.info("Created ident: {}, for account: {}, transactionId: {}", idNowCreateIdentResponse.getId(), account, transactionId));
    }

    public Mono<Document> uploadDocument(IdNowAccount account, String authToken, String transactionId, Document document) {
        return idNowWebClient.post()
                .uri("/api/v1/" + idNowAccountMapper.getAccountId(account) + "/identifications/" + transactionId + "/documents/" + document.getDocumentId() + "/data")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(API_TOKEN_HEADER, authToken)
                .body(BodyInserters.fromValue(document.getContent()))
                .retrieve()
                .onStatus(httpStatus -> {
                    apiMetric.incrementStatusCounter(httpStatus, ApiComponent.ID_NOW, ApiName.UPLOAD_DOCUMENT);
                    return false;
                }, clientResponse -> Mono.empty())
                .toBodilessEntity()
                .doOnError(throwable -> log.error("Failed uploading document for account: {}, transactionId: {}, documentId: {}", account, transactionId, document.getDocumentId(), throwable))
                .doOnSuccess(idNowCreateIdentResponse -> log.info("Uploaded document for account: {}, transactionId: {}, documentId: {}", account, transactionId, document.getDocumentId()))
                .map(any -> document);
    }
}
