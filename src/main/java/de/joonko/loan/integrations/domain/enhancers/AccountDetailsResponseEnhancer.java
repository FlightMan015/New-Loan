package de.joonko.loan.integrations.domain.enhancers;

import de.joonko.loan.integrations.model.DacDataState;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.integrations.model.UserState;
import de.joonko.loan.offer.api.model.*;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

import reactor.core.publisher.Mono;

@Component
public class AccountDetailsResponseEnhancer implements ResponseEnhancer<CustomErrorResponse> {

    @Override
    public Mono<OffersResponse<CustomErrorResponse>> buildResponseData(final @NotNull OfferRequest offerRequest) {
        return Mono.just(offerRequest)
                .map(OfferRequest::getUserState)
                .map(UserState::getDacDataState)
                .map(dacDataState -> {
                    if (DacDataState.MISSING_SALARY_ACCOUNT == dacDataState) {
                        return addedBankAccountIsNotTheSalaryOneDetails();
                    } else if (DacDataState.NO_ACCOUNT_ADDED == dacDataState) {
                        return noBankAccountIsAdded();
                    }
                    return null;
                });
    }

    @Override
    public OfferResponseState getState() {
        return OfferResponseState.MISSING_SALARY_ACCOUNT;
    }

    private OffersResponse<CustomErrorResponse> addedBankAccountIsNotTheSalaryOneDetails() {
        return new SalaryAccountRequiredResponseModel(getState(), CustomErrorResponse.builder().messageKey(CustomErrorMessageKey.NON_SALARY_ACCOUNT_ADDED).build());
    }

    private OffersResponse<CustomErrorResponse> noBankAccountIsAdded() {
        return new SalaryAccountRequiredResponseModel(getState(), null);
    }
}
