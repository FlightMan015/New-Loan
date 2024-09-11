package de.joonko.loan.identification.model.idnow;

public enum Result {
    SUCCESS,
    SUCCESS_DATA_CHANGED,
    FRAUD_SUSPICION_CONFIRMED,
    REVIEW_PENDING,
    FRAUD_SUSPICION_PENDING,
    ABORTED,
    CANCELED
}
