package de.joonko.loan.customer.support.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferAcceptedEvent extends Event {
    private String loanApplicationStatus;
    private String bank;
    private BigDecimal monthlyRate;
    private BigDecimal interestRate;
    private Integer duration;
    private BigDecimal totalInterestPayment;
    private BigDecimal totalPayment;
    private String kycUrl;
    private String loanApplicationId;
    private Integer loanAmountAsked;

    @Builder
    public OfferAcceptedEvent(String email, Long createdAt, String comment, String loanApplicationId, Integer loanAmountAsked, String loanApplicationStatus, String bank, BigDecimal monthlyRate, BigDecimal interestRate, Integer duration, BigDecimal totalInterestPayment, BigDecimal totalPayment, String kycUrl) {
        super(email, createdAt, comment);
        this.loanApplicationStatus = loanApplicationStatus;
        this.bank = bank;
        this.monthlyRate = monthlyRate;
        this.interestRate = interestRate;
        this.duration = duration;
        this.totalInterestPayment = totalInterestPayment;
        this.totalPayment = totalPayment;
        this.kycUrl = kycUrl;
        this.loanApplicationId = loanApplicationId;
        this.loanAmountAsked = loanAmountAsked;
    }
}
