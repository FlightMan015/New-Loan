package de.joonko.loan.integrations.domain.integrationhandler.testData;

import de.joonko.loan.integrations.model.DacDataState;
import de.joonko.loan.integrations.model.OffersState;
import de.joonko.loan.integrations.model.PersonalDataState;
import de.joonko.loan.integrations.model.UserState;
import de.joonko.loan.user.states.OfferDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanOffer;

import java.time.OffsetDateTime;
import java.util.Set;

import reactor.core.publisher.Flux;

public class LoanDemandIntegrationHandlerTestData {

    public Flux<LoanOffer> getLoanOffers() {
        return Flux.just(new LoanOffer(1, 1, null, null, null, null, null),
                new LoanOffer(12, 1, null, null, null, null, null)
        );
    }

    public de.joonko.loan.offer.api.LoanOffer getLoanOffer() {
        return de.joonko.loan.offer.api.LoanOffer.builder().build();
    }

    public LoanDemand getLoanDemand(String applicationId, String userId, String parentApplicationId) {
        LoanDemand loanDemand = new LoanDemand(applicationId, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, userId);
        loanDemand.setParentLoanApplicationId(parentApplicationId);

        return loanDemand;
    }

    public UserState getValidUserState() {
        return UserState.builder()
                .personalDataState(PersonalDataState.EXISTS)
                .dacDataState(DacDataState.FTS_DATA_EXISTS)
                .offersState(OffersState.MISSING_OR_STALE)
                .build();
    }

    public UserStatesStore getUserStatesStore(String applicationId, Integer loanAsked) {
        UserStatesStore userStatesStore = new UserStatesStore();
        OfferDataStateDetails offerDataStateDetails = OfferDataStateDetails.builder()
                .requestDateTime(OffsetDateTime.now())
                .applicationId(applicationId)
                .amount(loanAsked).build();

        userStatesStore.setOfferDateStateDetailsSet(Set.of(offerDataStateDetails));

        return userStatesStore;
    }

    public UserStatesStore getUserStatesStore(String applicationId, Integer loanAsked, String recommendedApplicationId) {
        UserStatesStore userStatesStore = new UserStatesStore();
        OfferDataStateDetails offerDataStateDetails = OfferDataStateDetails.builder()
                .requestDateTime(OffsetDateTime.now())
                .applicationId(applicationId)
                .amount(loanAsked).build();
        OfferDataStateDetails recommendedOfferDataStateDetails = OfferDataStateDetails.builder()
                .requestDateTime(OffsetDateTime.now())
                .applicationId(recommendedApplicationId)
                .amount(loanAsked).build();

        userStatesStore.setOfferDateStateDetailsSet(Set.of(offerDataStateDetails, recommendedOfferDataStateDetails));

        return userStatesStore;
    }
}
