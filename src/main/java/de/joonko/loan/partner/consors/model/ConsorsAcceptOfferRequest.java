package de.joonko.loan.partner.consors.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@AllArgsConstructor
@Data
@Builder
public class ConsorsAcceptOfferRequest {
    private FinancialCondition financialCondition;
    private Integer paymentDay;
}
