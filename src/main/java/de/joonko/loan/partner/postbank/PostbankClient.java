package de.joonko.loan.partner.postbank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.exception.PostBankException;
import de.joonko.loan.filter.LogResponseFilter;
import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;
import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequestSoapEnvelope;
import de.joonko.loan.partner.postbank.model.response.LoanDemandPostbankResponseBody;
import de.joonko.loan.partner.postbank.model.response.LoanDemandPostbankResponseSoapEnvelope;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostbankClient {

    @Qualifier("postbankWebClient")
    private final WebClient postbankWebClient;
    private final PostbankPropertiesConfig postbankPropertiesConfig;
    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private final XmlMapper xmlMapper = new XmlMapper();
    private final LogResponseFilter logResponseFilter;

    public Mono<LoanDemandPostbankResponseBody> requestLoanOffers(final @NotNull LoanDemandPostbankRequestSoapEnvelope request, final String id) {
        return postbankWebClient
                .mutate()
                .filter(logResponseFilter.logResponseBodyAndPublishMetric(ApiComponent.POSTBANK, ApiName.APPLY_FOR_LOAN))
                .build()
                .post()
                .uri(postbankPropertiesConfig.getLoanDemandUri())
                .bodyValue(serializeToXml(request))
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> loanApplicationAuditTrailService.saveApplicationError(id, String.format("POSTBANK: Error While requesting loans %s", e.getMessage()), Bank.POSTBANK))
                .onErrorMap(e -> new PostBankException(String.format("POSTBANK: Error While requesting loans for applicationId - %s", id), e))
                .doOnSuccess(any -> log.info("POSTBANK: Received loan request response for applicationId: {}", id))
                .map(envelope -> deserializeFromXml(envelope).getBody().getResponse().getResponseBody());
    }

    private String serializeToXml(final LoanDemandPostbankRequestSoapEnvelope request) {
        try {
            return xmlMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new PostBankException(String.format("POSTBANK: Error occurred while serializing the request model to XML for loan demand with applicationId - %s, error message - %s, error cause - %s", request.getBody().getContract().getCredit().getRequest().getApplicationId(), e.getMessage(), e.getCause()));
        }
    }

    private LoanDemandPostbankResponseSoapEnvelope deserializeFromXml(final String response) {
        try {
            return xmlMapper.readValue(response, LoanDemandPostbankResponseSoapEnvelope.class);
        } catch (JsonProcessingException e) {
            throw new PostBankException(String.format("POSTBANK: Error occurred while deserializing the response XML to model for loan demand, error message - %s, error cause - %s", e.getMessage(), e.getCause()));
        }
    }
}
