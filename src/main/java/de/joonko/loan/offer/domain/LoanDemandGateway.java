package de.joonko.loan.offer.domain;

import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.common.utils.CommonUtils;
import de.joonko.loan.metric.model.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public interface LoanDemandGateway<T extends LoanProviderApiMapper<I, O>, I, O> {

    Logger log = LoggerFactory.getLogger(LoanDemandGateway.class);

    T getMapper();

    Mono<O> callApi(I i, String id);

    LoanProvider getLoanProvider();

    default Process getCallApiProcessName() {
        return Process.GET_OFFERS;
    }

    Boolean filterGateway(LoanDemand loanDemand) throws RemoteException;

    List<LoanDuration> getDurations(Integer loanAsked);

    default Flux<LoanOffer> getLoanOffers(LoanDemand loanDemand, LoanDuration loanDuration) {
        CommonUtils.loadLogContext(loanDemand.getLoanApplicationId(), loanDemand.getDacId());
        return Flux.just(loanDemand)
                .map(demand -> getMapper().toLoanProviderRequest(loanDemand, loanDuration))
                .filter(this::validateRequest)
                .flatMap(request -> this.callApi(request, loanDemand.getLoanApplicationId()))
                .flatMap(response -> Flux.fromIterable(getMapper().fromLoanProviderResponse(response)))
                .filter(offer -> isOfferMatchingRequest(offer, loanDemand.getLoanAsked()))
                .doOnComplete(() -> log.info("userId: {} got offers from {} for duration {}", loanDemand.getUserUUID(), getLoanProvider().getName(), loanDuration.getValue()))
                .doOnError(ex -> log.error(getMessage(ex, loanDemand.getUserUUID()), ex));
    }

    default boolean isOfferMatchingRequest(LoanOffer offer, Integer loanAsked) {
        return offer.getAmount() == loanAsked.intValue() && null != LoanDuration.fromNumber(offer.getDurationInMonth());
    }

    private String getMessage(Throwable ex, String userUUID) {
        String message = String.format("user %s could not get loan offers from provider (%s)", userUUID, this.getClass());
        if (ex instanceof WebClientResponseException) {
            String responseBodyAsString = ((WebClientResponseException) ex).getResponseBodyAsString();
            return String.format("%s - Response body: %s", message, responseBodyAsString);
        } else {
            return message;
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
