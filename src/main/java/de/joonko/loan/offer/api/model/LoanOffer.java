package de.joonko.loan.offer.api.model;

import de.joonko.loan.offer.api.LoanProvider;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanOffer {

    private int amount;
    private int durationInMonth;
    private BigDecimal effectiveInterestRate;
    private BigDecimal nominalInterestRate;
    private BigDecimal monthlyRate;
    private BigDecimal totalPayment;
    private LoanProvider loanProvider;
}
