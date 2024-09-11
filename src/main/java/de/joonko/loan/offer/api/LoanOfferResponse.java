package de.joonko.loan.offer.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanOfferResponse {
    private String loanOfferId;
    private String loanApplicationId;
    private LoanOffer offer;
}
