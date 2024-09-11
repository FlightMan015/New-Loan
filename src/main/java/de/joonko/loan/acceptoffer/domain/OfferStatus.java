package de.joonko.loan.acceptoffer.domain;

import de.joonko.loan.offer.domain.LoanDuration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferStatus {
    private Integer loanAsked;
    private LoanDuration duration;
    private Double monthlyPayment;
    private Double effectiveRate;
    private Double nominalRate;
    private Double totalInterestPayment;
    private Double totalPayment;
    private String kycUrl;
    private String contractDocumentUrl;
    private byte[] preContract;
    private byte[] contract;
    private LoanApplicationStatus status;
    private Map<String, List<String>> errors;
}
