package de.joonko.loan.integrations.model;

public enum DacDataState {
    MISSING_OR_STALE, // The user is identified as a bonify user with verified bank account and we need to request the account data from DS
    FETCHING_FROM_DS, // In progress state
    FETCHING_FROM_FTS, // In progress state
    ERROR,// Any situation we cannot recover from, use ERROR status
    FTS_DATA_EXISTS, // Success case
    FETCHING_FROM_FUSIONAUTH, // Should wait for the user integration to get the necessary details for the user, specifically the bonify user id and if the user is verified by bank
    MISSING_ACCOUNT_CLASSIFICATION, // Will send the data received from DS to Finleap for classification
    MISSING_SALARY_ACCOUNT, // The user has provided an account, but we identified it as non-salary, will request the user to add a different account
    NO_ACCOUNT_ADDED // Will request the user to add an account
}
