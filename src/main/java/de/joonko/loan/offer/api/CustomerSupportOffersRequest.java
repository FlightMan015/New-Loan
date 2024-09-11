package de.joonko.loan.offer.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CustomerSupportOffersRequest {

    private String loanApplicationId;
}
