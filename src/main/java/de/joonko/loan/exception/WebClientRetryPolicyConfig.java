package de.joonko.loan.exception;

import de.joonko.loan.config.RetryConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.retry.Retry;
import reactor.retry.RetryContext;

import java.time.Duration;

@Configuration
@Slf4j
public class WebClientRetryPolicyConfig {

    @Bean
    public Retry<Object> getWebclientRetryPolicy(RetryConfigProperties retryConfigProperties) {
        return Retry.onlyIf(this::isInternalServerError)
                .fixedBackoff(Duration.ofSeconds(retryConfigProperties.getFixedBackoffSeconds()))
                .retryMax(retryConfigProperties.getFixedBackoffSeconds());
    }

    private boolean isInternalServerError(RetryContext<Object> retryContext) {
        Throwable ex = retryContext.exception();
        if (isInternalServerError(ex)) {
            log.warn("Retry is in Progress  for {}", ex.getMessage());
            return true;
        }
        return false;
    }

    private boolean isInternalServerError(Throwable ex) {
        return (ex instanceof WebClientResponseException) && ((WebClientResponseException) ex).getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR;
    }


}
