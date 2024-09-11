package de.joonko.loan.partner.consors.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DebugInfo {
    private String mDebtFactor;
    private String mMinimumRate;
    private String customerSpecificDiscount;
    private String mBasicDiscount;
    private String mBasicRate;
    private String mAgencyDecisionCode;
    private String mESDecision;
    private String mESDecisionAml;
    private String mESDecisionAmlCode;
    private String mDecisionCode;
    private String mExpectedLossOfInstallmentCredit;
    private String mMaxDurationForInstallmentLoans;
    private String mMaxLineForInstallmentLoanPreNoDocs;
    private String mMaxLineForInstallmentLoanPreDocs;
    private String mMaxLineForInstallmentLoanNoDocs;
    private String mMaxLineForInstallmentLoanDocs;
    private String mBICDMA;
    private String mDMA;
    private String insuranceCommision;
    private String issueCommision;
    private String variableCommision;
    private List<Object> customerRunningProducts;
}
