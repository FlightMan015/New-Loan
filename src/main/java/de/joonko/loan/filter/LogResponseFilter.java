package de.joonko.loan.filter;

import de.joonko.loan.exception.ErrorResponseException;
import de.joonko.loan.metric.ApiMetric;
import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class LogResponseFilter {

    private final ApiMetric apiMetric;

    public ExchangeFilterFunction logResponseStatus(String api) {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response Status {} from {}  API", clientResponse.statusCode(), api);
            return Mono.just(clientResponse);
        });
    }

    // Only logs the error message with body to the log, but will not return the error body back to the main application context, e.g. for saving to loanApplicationAuditTrail
    public ExchangeFilterFunction logResponseBody(String api) {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            HttpStatus status = clientResponse.statusCode();
            if (!status.is2xxSuccessful()) {
                return clientResponse.bodyToMono(String.class)
                        .map(body -> {
                            log.error("{} received for api call for {}, response body was {}", status, api, body);
                            return clientResponse;
                        });
            }
            return Mono.just(clientResponse);
        });
    }

    // Logs error, published metric and returns an exception with the response body, so that we can use it in the main context, e.g. saving to loanApplicationAuditTrail
    public ExchangeFilterFunction logResponseBodyAndPublishMetric(final ApiComponent api, final ApiName apiName) {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            HttpStatus status = clientResponse.statusCode();
            apiMetric.incrementStatusCounter(status, api, apiName);
            if (!status.is2xxSuccessful()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new ErrorResponseException(String.format("%s: %s received for api call for %s endpoint, response body was %s", api, status, apiName, body))));
            }
            return Mono.just(clientResponse);
        });
    }
}
