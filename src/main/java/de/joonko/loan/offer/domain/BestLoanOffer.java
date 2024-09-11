package de.joonko.loan.offer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BestLoanOffer {

    private String offerId;
    private OfferCategory offerCategory;
    private int amount;
    private int durationInMonth;
    private BigDecimal effectiveInterestRate;
    private BigDecimal nominalInterestRate;
    private BigDecimal apr;
    private BigDecimal monthlyRate;
    private BigDecimal totalPayment;


}
