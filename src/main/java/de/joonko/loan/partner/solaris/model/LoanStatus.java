package de.joonko.loan.partner.solaris.model;

import lombok.Getter;

@Getter
public enum LoanStatus {

    OFFERED("offered"),
    REJECTED("rejected"),
    EXPIRED("expired"),
    ACCOUNT_SNAPSHOT_VERIFICATION("account_snapshot_verification"),
    APPROVED("approved"),
    pending("pending"),
    ESIGN_PENDING("esign_pending"),
    ESIGN_COMPLETE("esign_complete"),
    ESIGN_FAILED("esign_failed"),
    LOAN_CREATED("loan_created");

    private final String status;

    LoanStatus(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
