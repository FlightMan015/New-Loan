package de.joonko.loan.acceptoffer.domain;

import de.joonko.loan.common.domain.Bank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.util.Set;

public interface AcceptOfferGateway<T extends AcceptOfferApiMapper<I, O>, I, O> {

    Logger log = LoggerFactory.getLogger(AcceptOfferGateway.class);

    T getMapper();

    Mono<O> callApi(I i, String applicationId, String offerId);

    Mono<String> getLoanProviderReferenceNumber(String loanApplicationId, String loanOfferId);

    Bank getBank();

    default Mono<OfferStatus> acceptOffer(OfferRequest offerRequest) {
        return Mono.just(offerRequest)
                .map(getMapper()::toAcceptOfferRequest)
                .filter(this::validateRequest)
                .flatMap(request -> this.callApi(request, offerRequest.getApplicationId(), offerRequest.getLoanOfferId()))
                .map(response -> getMapper().fromAcceptOfferResponse(response))
                .doOnError(this::logError);
    }

    private void logError(Throwable ex) {
        log.error(getMessage(ex), ex);
    }

    private String getMessage(Throwable ex) {
        String message = "could not accept offers from provider (%s)";
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
        Boolean validationStatus = validate
                .isEmpty();
        if (!validationStatus) {
            log.info("Validation Errors  {} ", validate);
        }
        return validationStatus;
    }
}
