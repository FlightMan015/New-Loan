package de.joonko.loan.webhooks.idnow.model.enums;

public enum IdentificationResult {
    SUCCESS,
    SUCCESS_DATA_CHANGED,
    FRAUD_SUSPICION_CONFIRMED,
    REVIEW_PENDING,
    FRAUD_SUSPICION_PENDING,
    ABORTED,
    CANCELED
}
