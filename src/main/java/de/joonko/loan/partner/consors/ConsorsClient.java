package de.joonko.loan.partner.consors;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.partner.consors.auth.JwtToken;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.exception.ConsorsBankException;
import de.joonko.loan.filter.LogResponseFilter;
import de.joonko.loan.metric.ApiMetric;
import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;
import de.joonko.loan.partner.consors.model.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConsorsClient {

    @Qualifier("consorsAuthWebClient")
    private final WebClient consorsWebClient;
    private final ConsorsPropertiesConfig propertiesConfig;
    private final ApiMetric apiMetric;
    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private static final ApiComponent apiComponent = ApiComponent.CONSORS;
    private final LogResponseFilter logResponseFilter;

    public Mono<JwtToken> getToken(final String applicationId) {
        return consorsWebClient
                .mutate()
                .filter(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.CONSORS, ApiName.AUTHORIZATION))
                .build()
                .post()
                .uri(propertiesConfig.getTokenUri())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(
                        "username", propertiesConfig.getUsername()).with("password", propertiesConfig.getPassword())
                ).retrieve()
                .bodyToMono(JwtToken.class)
                .doOnError(e -> loanApplicationAuditTrailService.saveApplicationError(applicationId, "Error While getting Token " + e.getMessage(), Bank.CONSORS.name()))
                .onErrorMap(e -> new ConsorsBankException("Error While getting Token", e))
                .doOnSuccess(any -> log.info("Consors: Received JwtToken response for applicationId: {}", applicationId));
    }

    public Mono<String> getProducts(final @NotNull JwtToken jwtToken, final String applicationId) {
        return consorsWebClient
                .mutate()
                .filter(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.CONSORS, ApiName.GET_PRODUCTS))
                .build()
                .get()
                .uri(propertiesConfig.buildProductUri())
                .headers(jwtToken.bearer())
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> log.error("Failed getting products", e))
                .doOnSuccess(any -> log.info("Consors: Received products response for applicationId: {}", applicationId));
    }

    public Mono<String> validateRules(final @NotNull JwtToken jwtToken, Link link, final String applicationId) {
        return consorsWebClient
                .mutate()
                .filter(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.CONSORS, ApiName.VALIDATE_RULES))
                .build()
                .method(link.getMethod())
                .uri(link.getUri())
                .headers(jwtToken.bearer())
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> log.error("Failed validating rules", e))
                .doOnSuccess(any -> log.info("Consors: Received validate rules response for applicationId: {}", applicationId));
    }

    public Mono<String> validateSubscription(final @NotNull JwtToken jwtToken, Link link, ValidateSubscriptionRequest validateSubscriptionRequest, final String applicationId) {
        return consorsWebClient
                .mutate()
                .filter(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.CONSORS, ApiName.VALIDATE_SUBSCRIPTION))
                .build()
                .method(link.getMethod())
                .uri(link.getUri())
                .headers(jwtToken.bearer())
                .bodyValue(validateSubscriptionRequest)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> log.error("Failed validating subscription", e))
                .doOnSuccess(any -> log.info("Consors: Received validate subscription response for applicationId: {}", applicationId));
    }

    public Mono<PersonalizedCalculationsResponse> getPersonalizedCalculations(final @NotNull JwtToken jwtToken, Link link, final String applicationId) {
        return consorsWebClient
                .mutate()
                .filter(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.CONSORS, ApiName.GET_PERSONALIZED_CALCULATIONS))
                .build()
                .method(link.getMethod())
                .uri(link.getUri())
                .headers(jwtToken.bearer())
                .retrieve()
                .bodyToMono(PersonalizedCalculationsResponse.class)
                .doOnError(e -> log.error("Failed getting personalized calculations", e))
                .doOnSuccess(any -> log.info("Consors: Received personalized calculations response for applicationId: {}", applicationId));
    }

    public Mono<ConsorsAcceptOfferResponse> finalizeSubscription(final @NotNull JwtToken jwtToken, LinkRelation link, ConsorsAcceptOfferRequest acceptOfferRequest, final String applicationId) {
        return consorsWebClient
                .mutate()
                .filter(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.CONSORS, ApiName.FINALIZE_SUBSCRIPTION))
                .build()
                .method(HttpMethod.valueOf(link.getMethod()))
                .uri(propertiesConfig.buildUriFromUrlLink(link.getHref()))
                .headers(jwtToken.bearer())
                .bodyValue(acceptOfferRequest)
                .retrieve()
                .bodyToMono(ConsorsAcceptOfferResponse.class)
                .doOnError(e -> log.error("Failed finalizing subscription for application - {}, error message - {}, error cause - {}", applicationId, e.getMessage(), e.getCause()))
                .doOnSuccess(any -> log.info("Consors: Received finalize subscription response for applicationId: {}", applicationId));
    }

    public Mono<ResponseEntity<Void>> cancelSubscription(final @NotNull JwtToken jwtToken, de.joonko.loan.partner.consors.model.Link link, final String applicationId) {
        return consorsWebClient
                .mutate()
                .filter(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.CONSORS, ApiName.CANCEL_SUBSCRIPTION))
                .build()
                .method(HttpMethod.valueOf(link.getMethod()))
                .uri(propertiesConfig.buildUriFromUrlLink(link.getHref()))
                .headers(jwtToken.bearer())
                .retrieve()
                .toBodilessEntity()
                .doOnError(throwable -> log.error("Error while cancelling subscription", throwable))
                .doOnSuccess(clientResponse -> log.info("Consors: Received cancel subscription response for applicationId: {}", applicationId));
    }

    public Mono<byte[]> getContract(final @NotNull JwtToken jwtToken, @NotNull String url, final String applicationId) {
        return consorsWebClient
                .mutate()
                .filter(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.CONSORS, ApiName.GET_CONTRACT))
                .build()
                .get()
                .uri(propertiesConfig.buildUriFromUrlLink(url))
                .headers(jwtToken.bearer())
                .retrieve()
                .bodyToMono(byte[].class)
                .doOnError(e -> log.error("Failed getting contract", e))
                .doOnSuccess(clientResponse -> log.info("Consors: Received contract response for applicationId: {}", applicationId));
    }
}
