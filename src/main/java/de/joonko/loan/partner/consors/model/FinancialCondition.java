package de.joonko.loan.partner.consors.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class FinancialCondition {
    private Integer creditAmount;
    private Integer duration;
}
