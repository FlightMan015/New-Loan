package de.joonko.loan.integrations.model;

public enum PersonalDataState {
    MISSING_OR_STALE, EXISTS, ERROR,// Any situation we cannot recover from, use ERROR status
    USER_INPUT_REQUIRED
}
