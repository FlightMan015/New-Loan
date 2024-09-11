package de.joonko.loan.acceptoffer.domain;

import de.joonko.loan.common.domain.Bank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.util.Objects;
import java.util.Set;

public interface LoanApplicationStatusGateway<T extends LoanApplicationStatusApiMapper<I, O>, I, O> {

    Logger log = LoggerFactory.getLogger(LoanApplicationStatusGateway.class);

    T getMapper();

    Mono<O> callApi(I request, String loanApplicationId, String loanOfferId);

    String getLoanProviderReferenceNumber(String loanApplicationId, String loanOfferId);

    Bank getBank();

    default Mono<LoanApplicationStatus> getStatus(OfferRequest offerRequest) {
        return Mono.just(offerRequest)
                .map(getMapper()::toLoanApplicationStatusRequest)
                .filter(this::validateRequest)
                .flatMap(request -> callApi(request, offerRequest.getApplicationId(), offerRequest.getLoanOfferId()))
                .filter(Objects::nonNull)
                .map(response -> getMapper().fromLoanApplicationStatusResponse(response))
                .doOnError(ex -> log.error(getMessage(ex), ex));
    }

    private String getMessage(Throwable ex) {
        String message = "could not get status of loan application from provider (%s)";
        if (ex instanceof WebClientResponseException) {
            String responseBodyAsString = ((WebClientResponseException) ex).getResponseBodyAsString();
            return String.format("%s - Response body: %s", message, responseBodyAsString);
        } else {
            return String.format(message, this.getClass());
        }
    }

    private boolean validateRequest(Object object) {
        Set<ConstraintViolation<Object>> validate = Validation.buildDefaultValidatorFactory()
                .getValidator()
                .validate(object);
        boolean validationStatus = validate
                .isEmpty();
        if (!validationStatus) {
            log.info("Validation Errors  {} ", validate);
        }
        return validationStatus;
    }
}
