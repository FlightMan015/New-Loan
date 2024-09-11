package de.joonko.loan.acceptoffer.api;

import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AcceptOfferResponse {
    private Integer loanAsked;
    private Integer duration;
    private Double monthlyPayment;
    private Double effectiveRate;
    private Double nominalRate;
    private Double totalInterestPayment;
    private Double totalPayment;
    private String kycUrl;
    private String contractDocumentUrl;
    private byte[] contract;
    private byte[] preContract;
    private LoanApplicationStatus status;
    private Map<String, List<String>> errors;
}
