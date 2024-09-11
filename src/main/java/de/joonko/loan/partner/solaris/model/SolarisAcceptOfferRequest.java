package de.joonko.loan.partner.solaris.model;

import de.joonko.loan.offer.domain.LoanDuration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
@Builder
public class SolarisAcceptOfferRequest {

    @NotNull(message = "loanAsked is Mandatory")
    private Integer loanAsked;

    @NotNull(message = "duration is Mandatory")
    private LoanDuration duration;
}
