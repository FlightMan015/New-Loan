package de.joonko.loan.identification.model;

public enum KycStatus {
    SUCCESS,
    PENDING,
    INITIATED,
    FAILURE_RETRYABLE,
    FAILURE_NON_RETRYABLE
}
