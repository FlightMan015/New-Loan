package de.joonko.loan.data.support.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DataLoanOffer {
    private String loanOfferId;
    private int amount;
    private int durationInMonth;
    private BigDecimal effectiveInterestRate;
    private BigDecimal nominalInterestRate;
    private BigDecimal monthlyRate;
    private BigDecimal totalPayment;
}
