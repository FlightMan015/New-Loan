package de.joonko.loan.acceptoffer.domain;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OfferRequest {
    private Integer loanAsked;
    private LoanDuration duration;
    private String loanProvider;
    private String applicationId;
    private String loanOfferId;
    private LoanDemand loanDemand;
}
