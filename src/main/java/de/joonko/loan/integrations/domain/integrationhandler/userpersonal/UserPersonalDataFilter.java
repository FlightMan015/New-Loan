package de.joonko.loan.integrations.domain.integrationhandler.userpersonal;

import de.joonko.loan.integrations.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.function.Predicate;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class UserPersonalDataFilter implements Predicate<OfferRequest> {

    @Override
    public boolean test(@NotNull OfferRequest offerRequest) {
        if (!hasRequiredNonNullFields(offerRequest)) {
            log.debug("userId: {}, userPersonal mutation failed, required non null fields", offerRequest.getUserUUID());
            return false;
        }

        boolean validUserState = isStateValid(offerRequest.getUserState());

        log.debug("userId: {}, validUserState: {}", offerRequest.getUserUUID(), validUserState);

        return validUserState;
    }

    private boolean isStateValid(UserState userState) {
        return userState.getPersonalDataState() == PersonalDataState.MISSING_OR_STALE &&
                userState.getOffersState() != OffersState.OFFERS_EXIST;
    }

    private boolean hasRequiredNonNullFields(OfferRequest offerRequest) {
        return nonNull(offerRequest.getUserState()) &&
                nonNull(offerRequest.getUserState().getOffersState()) &&
                nonNull(offerRequest.getUserState().getPersonalDataState());
    }
}
