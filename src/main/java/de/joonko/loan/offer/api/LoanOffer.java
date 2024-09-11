package de.joonko.loan.offer.api;

import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LoanOffer implements Serializable {
    @Indexed
    private int amount;
    private int durationInMonth;
    private BigDecimal effectiveInterestRate;
    private BigDecimal nominalInterestRate;
    private BigDecimal monthlyRate;
    private BigDecimal totalPayment;
    private LoanProvider loanProvider;

}
