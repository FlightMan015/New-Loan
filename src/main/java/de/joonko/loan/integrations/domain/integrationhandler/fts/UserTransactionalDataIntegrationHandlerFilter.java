package de.joonko.loan.integrations.domain.integrationhandler.fts;

import de.joonko.loan.integrations.model.DacDataState;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.integrations.model.UserState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class UserTransactionalDataIntegrationHandlerFilter implements Predicate<OfferRequest> {

    @Override
    public boolean test(@NotNull OfferRequest offerRequest) {
        if (!hasRequiredNonNullFields(offerRequest)) {
            log.debug("userId: {}, transactional data mutation failed, required non null fields", offerRequest.getUserUUID());
            return false;
        }

        boolean validUserState = isStateValid(offerRequest.getUserState());

        log.debug("userId: {}, valid state for applying UserTransactionalDataIntegrationHandler: {}", offerRequest.getUserUUID(), validUserState);

        return validUserState;
    }

    private boolean isStateValid(final UserState userState) {
        return Set.of(DacDataState.MISSING_OR_STALE, DacDataState.MISSING_ACCOUNT_CLASSIFICATION).contains(userState.getDacDataState());
    }

    private boolean hasRequiredNonNullFields(final OfferRequest offerRequest) {
        return nonNull(offerRequest.getUserState()) &&
                nonNull(offerRequest.getUserState().getDacDataState());
    }
}
