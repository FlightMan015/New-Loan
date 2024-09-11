package de.joonko.loan.partner.creditPlus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreditPlusAcceptOfferRequest {

    private String loanApplicationId;
    private String offerId;
    private Integer duration;
}
