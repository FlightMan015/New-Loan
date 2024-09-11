package de.joonko.loan.webhooks.idnow.model.enums;

public enum IdentificationResultReason {
    DATA_APPLICATION_ADDRESS,
    DATA_APPLICATION_DATA,
    DATA_ID_EXPIRED,
    NO_CONNECTION,
    OTHER_ABUSE,
    OTHER_MISCELLANEOUS_PERMANENT,
    OTHER_MISCELLANEOUS_TEMPORARY,
    OTHER_TEST,
    STALLED_TIMEOUT,
    TECH_AUDIO,
    TECH_DISCONNECTED_VIDEO,
    TECH_DISCONNECTED_WEBSOCKET,
    TECH_HOLOGRAM,
    TECH_ID_TYPE,
    TECH_IDENT_CODE_DELIVERY,
    TECH_INTERNAL_SERVER_ERROR,
    TECH_INTERNET_CONNECTION,
    TECH_LIGHTING,
    TECH_PHOTO,
    TECH_TIMEOUT,
    TECH_VIDEO,
    USER_ABORT_WHILE_WAITING,
    USER_CANCELLATION,
    USER_ID_NUMBER,
    USER_IDENT_CODE,
    USER_LANGUAGE,
    USER_NO_ID,
    USER_WRONG_PERSON,
    WARNING_SOCIAL_ENGINEERING,
    WARNING_ID_MANIPULATION,
    WARNING_FAKE_ID,
    WARNING_PHOTO,
    WARNING_DESCRIPTION,
    WARNING_BEHAVIOUR,
    WARNING_INCONSISTENT_DATA,
    WARNING_WRONG_CHECKSUM
}
