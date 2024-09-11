package de.joonko.loan.acceptoffer.domain;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public enum LoanApplicationStatus {
    APPROVED,
    PAID_OUT, //Loan final status
    PENDING, //Loan intermediate status
    REJECTED, //Loan final status
    WAITING_FOR_DOCUMENT,
    ACCOUNT_SNAPSHOT_VERIFICATION,
    ESIGN_PENDING,
    OFFER_ACCEPTED,
    SUCCESS, // KYC completed
    INITIATED, //KYC Pending
    REVIEW_PENDING, // KYC Pending
    ABORTED,
    CANCELED,
    FAILURE,
    FRAUD_SUSPICION_PENDING, // KYC Pending
    UNDEFINED;

    public static Set<LoanApplicationStatus> getIntermediateStatuses() {
        return EnumSet.of(PENDING, INITIATED, OFFER_ACCEPTED, ESIGN_PENDING, REVIEW_PENDING, SUCCESS, FRAUD_SUSPICION_PENDING);
    }

    public static Set<LoanApplicationStatus> getFinalOfferStatuses() {
        return EnumSet.of(PAID_OUT, REJECTED, CANCELED);
    }

    public static Optional<LoanApplicationStatus> fromValue(final String status) {
        for (LoanApplicationStatus st : LoanApplicationStatus.values()) {
            if (st.name().equals(status)) {
                return Optional.of(st);
            }
        }
        return Optional.empty();
    }
}
