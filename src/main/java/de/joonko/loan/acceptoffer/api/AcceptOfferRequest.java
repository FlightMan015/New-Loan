package de.joonko.loan.acceptoffer.api;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class AcceptOfferRequest {

    @NotNull(message = "Loan Offer id must not be null")
    private String loanOfferId;

}
