package de.joonko.loan.partner.auxmoney;

import com.google.common.annotations.VisibleForTesting;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.config.AuxmoneyConfig;
import de.joonko.loan.data.support.DataSupportService;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDemandGateway;
import de.joonko.loan.partner.auxmoney.mapper.AuxmoneyApiMapper;
import de.joonko.loan.partner.auxmoney.model.AuxmoneyGetOffersRequest;
import de.joonko.loan.partner.auxmoney.model.AuxmoneySingleCallResponse;
import de.joonko.loan.partner.auxmoney.model.LoanDuration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
@VisibleForTesting
@ConditionalOnProperty(
        value = "auxmoney.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class AuxmoneyLoanDemandGateway implements LoanDemandGateway<AuxmoneyApiMapper, AuxmoneyGetOffersRequest, List<AuxmoneySingleCallResponse>> {

    private final AuxmoneyConfig auxmoneyConfig;
    @Qualifier("auxmoneyWebClient")
    private final WebClient auxmoneyWebClient;
    private final AuxmoneyApiMapper auxmoneyGetOffersMapper;
    private final AuxmoneySingleOfferCallResponseStoreService auxmoneySingleOfferCallResponseStoreService;
    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private final DataSupportService dataSupportService;

    @Override
    public LoanProvider getLoanProvider() {
        return LoanProvider.builder().name(Bank.AUXMONEY.getLabel()).build();
    }

    @Override
    public AuxmoneyApiMapper getMapper() {
        return auxmoneyGetOffersMapper;
    }

    @Override
    public Mono<List<AuxmoneySingleCallResponse>> callApi(final AuxmoneyGetOffersRequest auxmoneyGetOffersRequest, String loanApplicationId) {
        loanApplicationAuditTrailService.sendingLoanDemandRequest(loanApplicationId, Bank.AUXMONEY);
        return Flux.fromStream(Stream.of(LoanDuration.FORTY_EIGHT))
                .flatMap(loanDuration ->
                        geSingleOfferCall(auxmoneyGetOffersRequest.toBuilder()
                                .duration(loanDuration)
                                .build(), loanApplicationId)
                )
                .collectList();
    }

    @Override
    public Boolean filterGateway(LoanDemand loanDemand) {
        return false;
    }

    @Override
    public List<de.joonko.loan.offer.domain.LoanDuration> getDurations(Integer loanAsked) {
        return List.of(de.joonko.loan.offer.domain.LoanDuration.FORTY_EIGHT);
    }

    public Mono<AuxmoneySingleCallResponse> geSingleOfferCall(AuxmoneyGetOffersRequest auxmoneyGetOffersRequest, String id) {
        log.info("Requesting to Auxmoney {} on Server {}", auxmoneyGetOffersRequest.getExternalId(), auxmoneyConfig.getHost());
        return auxmoneyWebClient
                .post()
                .uri(auxmoneyConfig.getOffersEndpoint())
                .bodyValue(auxmoneyGetOffersRequest)
                .retrieve()
                .bodyToMono(AuxmoneySingleCallResponse.class)
                .doOnSuccess(auxmoneySingleCallResponse -> auxmoneySingleOfferCallResponseStoreService.saveSingleOfferResponse(auxmoneySingleCallResponse, id.toString()))
                .doOnSuccess(auxmoneySingleCallResponse -> loanApplicationAuditTrailService.receivedLoanDemandResponseAuxmoney(id, auxmoneySingleCallResponse))
                .doOnSuccess(auxmoneySingleCallResponse -> {
                    if (auxmoneySingleCallResponse.getManualQualityAssurance()) {
                        dataSupportService.pushYellowOffersReceivedTopic(auxmoneyGetOffersMapper.fromLoanProviderResponse(List.of(auxmoneySingleCallResponse)), id, "manualQualityAssurance :" + auxmoneySingleCallResponse.getManualQualityAssurance());
                    } else if (!auxmoneySingleCallResponse.getIsSuccess()) {
                        String errorMessage = "";
                        String errorCode = "";
                        if (auxmoneySingleCallResponse.getViolations().size() > 0) {
                            errorMessage = auxmoneySingleCallResponse.getViolations().get(0).getMessage();
                            errorCode = auxmoneySingleCallResponse.getViolations().get(0).getCode();
                        }
                        dataSupportService.pushRedOffersReceivedTopic(id, Bank.AUXMONEY.toString(), errorMessage, errorCode, auxmoneySingleCallResponse.toString());
                    }
                })
                .doOnError(throwable -> logError(throwable, auxmoneyGetOffersRequest.getDuration(), auxmoneyGetOffersRequest.getExternalId()))
                .doOnError(throwable -> loanApplicationAuditTrailService.receivedLoanDemandResponseError(id, throwable.getMessage(), Bank.AUXMONEY))
                .onErrorResume(throwable -> Mono.empty())
                .doOnNext(auxmoneyAcceptOfferResponse -> log.info("Response from Auxmoney isError:{} isSuccess:{} manualQualityAssurance:{} ", auxmoneyAcceptOfferResponse.getIsError(), auxmoneyAcceptOfferResponse.getIsSuccess(), auxmoneyAcceptOfferResponse.getManualQualityAssurance()))
                .filter(AuxmoneySingleCallResponse::getIsSuccess)
                .filter(auxmoneySingleCallResponse -> !auxmoneySingleCallResponse.getManualQualityAssurance());

    }

    private void logError(Throwable ex, LoanDuration loanDuration, String loanApplicationId) {
        log.error("Error while getting offer for duration {} for application Id {}", loanDuration, loanApplicationId, ex);
        log.error(getMessage(ex, loanApplicationId, loanDuration), ex);
    }

    private String getMessage(Throwable ex, String loanApplicationId, LoanDuration loanDuration) {
        String message = "could not get loan offers from provider (%s)";
        String exceptionMessage;
        if (ex instanceof WebClientResponseException) {
            String responseBodyAsString = ((WebClientResponseException) ex).getResponseBodyAsString();
            exceptionMessage = String.format("%s - Response body: %s", message, responseBodyAsString);
            try {
                JSONObject errorJson = new JSONObject(responseBodyAsString);
                JSONArray violationsArray = errorJson.getJSONArray("violations");
                if (violationsArray != null) {
                    JSONObject violation = new JSONObject(violationsArray.get(0).toString());
                    String errorMessage = (violation.get("message") != null) ? violation.get("message").toString() : "";
                    String errorCode = (violation.get("code") != null) ? violation.get("code").toString() : "";
                    dataSupportService.pushRedOffersReceivedTopic(loanApplicationId, Bank.AUXMONEY.toString(), errorMessage, errorCode, responseBodyAsString);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            exceptionMessage = String.format(message, this.getClass());
        }
        loanApplicationAuditTrailService.saveApplicationError(loanApplicationId, "could not get loan offers from provider for duration " + loanDuration.name() + " " + exceptionMessage, Bank.AUXMONEY.name());
        return exceptionMessage;
    }
}

