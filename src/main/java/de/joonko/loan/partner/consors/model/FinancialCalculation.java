package de.joonko.loan.partner.consors.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinancialCalculation {
    private Integer index;
    private Integer creditAmount;
    private Integer duration;
    private Double monthlyRate;
    private Double effectiveRate;
    private Double nominalRate;
    private Double totalInterestAmount;
    private Double totalPayment;
}
