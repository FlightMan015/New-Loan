package de.joonko.loan.offer.api.model;

import de.joonko.loan.integrations.domain.enhancers.KycRelatedPersonalDetails;

import java.util.List;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OffersReadyResponse {

    private KycRelatedPersonalDetails kycRelatedPersonalDetails;
    private Set<Integer> recentQueriedAmounts;
    private List<LoanOfferStore> offers;
    private int requestedOffers;
    private int totalOffers;
}
