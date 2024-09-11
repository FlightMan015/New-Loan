package de.joonko.loan.partner.aion;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.exception.AionBankException;
import de.joonko.loan.filter.LogResponseFilter;
import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;
import de.joonko.loan.partner.aion.model.AionAuthToken;
import de.joonko.loan.partner.aion.model.BestOffersRequest;
import de.joonko.loan.partner.aion.model.CreditApplicationRequest;
import de.joonko.loan.partner.aion.model.CreditApplicationResponse;
import de.joonko.loan.partner.aion.model.OffersToBeatResponse;
import de.joonko.loan.partner.aion.model.offerchoice.OfferChoiceRawResponse;
import de.joonko.loan.partner.aion.model.offerchoice.OfferChoiceRequest;
import de.joonko.loan.partner.aion.model.offerchoice.OfferChoiceResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class AionClient {

    @Qualifier("aionWebClient")
    private final WebClient aionWebClient;
    private final AionPropertiesConfig aionPropertiesConfig;
    private final AionClientMapper aionClientMapper;
    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;

    private final LogResponseFilter logResponseFilter;

    private static final String API_TOKEN_HEADER = "X-Token";

    public Mono<AionAuthToken> getToken(String id) {
        return aionWebClient
                .mutate()
                .filter(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.AUTHORIZATION))
                .build()
                .post()
                .uri(aionPropertiesConfig.getTokenUri())
                .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("client_id", aionPropertiesConfig.getAuthClientId())
                        .with("client_secret", aionPropertiesConfig.getAuthClientSecret())
                        .with("grant_type", "client_credentials")
                        .with("audience", aionPropertiesConfig.getAuthAudience())
                )
                .retrieve()
                .bodyToMono(AionAuthToken.class)
                .doOnError(e -> loanApplicationAuditTrailService.saveApplicationError(id, String.format("AION: Error While getting Token %s", e.getMessage()), Bank.AION.name()))
                .onErrorMap(e -> new AionBankException("AION: Error While getting Aion auth Token", e))
                .doOnSuccess(any -> log.info("AION: Received authorization response for applicationId: {}", id));
    }

    public Mono<CreditApplicationResponse> processInitialData(final @NotNull AionAuthToken authToken, final @NotNull CreditApplicationRequest creditApplicationRequest, final String id) {
        return aionWebClient
                .mutate()
                .filter(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.PROCESS_INITIAL_DATA))
                .build()
                .post()
                .uri(aionPropertiesConfig.getProcessUri())
                .header(API_TOKEN_HEADER, authToken.getToken())
                .bodyValue(creditApplicationRequest)
                .retrieve()
                .bodyToMono(CreditApplicationResponse.class)
                .doOnError(e -> loanApplicationAuditTrailService.saveApplicationError(id, String.format("AION: Error While passing data to process endpoint %s", e.getMessage()), Bank.AION.name()))
                .onErrorMap(e -> new AionBankException(String.format("AION: Error While passing data to process endpoint for applicationId - %s", id), e))
                .doOnSuccess(any -> log.info("AION: Received process initial data response for applicationId: {}", id));
    }

    public Mono<OffersToBeatResponse> processOffersToBeat(final @NotNull AionAuthToken authToken, final BestOffersRequest[] bestOffersRequestArray, final String id, final @NotNull String processId) {
        return aionWebClient
                .mutate()
                .filter(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.PROCESS_OFFERS_TO_BEAT))
                .build()
                .put()
                .uri(aionPropertiesConfig.getOffersToBeatUri(processId))
                .header(API_TOKEN_HEADER, authToken.getToken())
                .bodyValue(bestOffersRequestArray)
                .retrieve()
                .bodyToMono(OffersToBeatResponse.class)
                .doOnError(e -> loanApplicationAuditTrailService.saveApplicationError(id, String.format("AION: Error While passing data to offers to beat endpoint %s", e.getMessage()), Bank.AION.name()))
                .doOnError(e -> log.error("AION: Error While passing data to offers to beat endpoint for applicationId - {}, exception cause - {}, exception message - {}", id, e.getCause(), e.getMessage()))
                .doOnSuccess(any -> log.info("AION: Received process offers to beat response for processId: {}", processId));
    }

    public Mono<OfferChoiceResponse> sendOfferChoice(final @NotNull AionAuthToken authToken, final @NotNull String processId, final @NotNull OfferChoiceRequest offerChoiceRequest) {
        return aionWebClient
                .mutate()
                .filter(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.SEND_OFFER_CHOICE))
                .build()
                .put()
                .uri(aionPropertiesConfig.getOfferChoiceUri(processId))
                .header(API_TOKEN_HEADER, authToken.getToken())
                .bodyValue(List.of(offerChoiceRequest))
                .retrieve()
                .bodyToMono(OfferChoiceRawResponse.class)
                .map(aionClientMapper::mapToOfferChoice)
                .doOnError(throwable -> log.error("AION: Failed sending offer choice for processId: {}", processId, throwable))
                .doOnSuccess(customerData -> log.info("AION: Received offer choice response for processId: {}", processId));
    }

    public Mono<OfferChoiceResponse> getOfferStatus(final @NotNull AionAuthToken authToken, final @NotNull String processId) {
        return aionWebClient
                .mutate()
                .filter(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.AION, ApiName.GET_OFFER_STATUS))
                .build()
                .get()
                .uri(aionPropertiesConfig.getOfferStatusUri(processId))
                .header(API_TOKEN_HEADER, authToken.getToken())
                .retrieve()
                .bodyToMono(OfferChoiceRawResponse.class)
                .map(aionClientMapper::mapToOfferChoice)
                .doOnError(throwable -> log.error("AION: Failed getting offer status for processId: {}", processId, throwable))
                .doOnSuccess(customerData -> log.info("AION: Received offer status response for processId: {}", processId));
    }
}
