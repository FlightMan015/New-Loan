package de.joonko.loan.customer.support.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KycStatusEvent extends Event {

    private String loanApplicationId;
    private String kycStatus;
    private String kycReason;
    private String loanBankReferenceNumber;
    private String bank;
    private Double monthlyRate;
    private Double interestRate;
    private Integer duration;
    private Double totalInterestPayment;
    private Double totalPayment;
    private String kycLink;
    private Integer loanAmountAsked;
    private String loanContract;

    @Builder
    public KycStatusEvent(String email, Long createdAt, String comment, String loanApplicationId, String kycStatus, String loanBankReferenceNumber, String bank, Double monthlyRate, Double interestRate, Integer duration, Double totalInterestPayment, Double totalPayment, String kycLink, Integer loanAmountAsked, String loanContract, String kycReason) {
        super(email, createdAt, comment);
        this.loanApplicationId = loanApplicationId;
        this.kycStatus = kycStatus;
        this.kycReason = kycReason;
        this.loanBankReferenceNumber = loanBankReferenceNumber;
        this.bank = bank;
        this.monthlyRate = monthlyRate;
        this.interestRate = interestRate;
        this.duration = duration;
        this.totalInterestPayment = totalInterestPayment;
        this.totalPayment = totalPayment;
        this.kycLink = kycLink;
        this.loanAmountAsked = loanAmountAsked;
        this.loanContract = loanContract;
    }
}
