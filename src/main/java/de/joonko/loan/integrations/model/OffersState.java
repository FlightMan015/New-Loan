package de.joonko.loan.integrations.model;

public enum OffersState {
    MISSING_OR_STALE, IN_PROGRESS, OFFERS_EXIST, FAILURE, // If the situation is recoverable, use FAILURE status
    ERROR // Any situation we cannot recover from, use ERROR status
}
