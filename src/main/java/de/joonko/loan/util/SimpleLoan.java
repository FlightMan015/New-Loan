package de.joonko.loan.util;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SimpleLoan {
    private BigDecimal amount;
    private int durationInMonth;
    private BigDecimal effectiveInterestRate;
    private BigDecimal nominalInterestRate;
    private BigDecimal monthlyRate;
    private BigDecimal totalPayment;
}
