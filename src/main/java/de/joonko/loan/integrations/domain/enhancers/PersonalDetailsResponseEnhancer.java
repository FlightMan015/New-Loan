package de.joonko.loan.integrations.domain.enhancers;

import de.joonko.loan.integrations.domain.integrationhandler.loandemand.LoanDemandRequestBuilder;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.api.model.OfferResponseState;
import de.joonko.loan.offer.api.model.OffersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Component
public class PersonalDetailsResponseEnhancer implements ResponseEnhancer<LoanDemandRequest> {

    @Autowired
    private LoanDemandRequestBuilder loanDemandRequestBuilder;

    @Override
    public Mono<OffersResponse<LoanDemandRequest>> buildResponseData(final @NotNull OfferRequest offerRequest) {
        return Mono.just(offerRequest)
                .flatMap(offerReq -> loanDemandRequestBuilder.build(offerRequest))
                .map(body -> OffersResponse.<LoanDemandRequest>builder().state(getState()).data(body).build());
    }

    @Override
    public OfferResponseState getState() {
        return OfferResponseState.MISSING_PERSONAL_DATA;
    }
}
