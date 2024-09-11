package de.joonko.loan.metric.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiName {
    AUTHORIZATION("authorization"),

    GET_USER_TRAITS("get_user_traits"),

    SEND_CREATE_USER_ACTION_REQUEST("sendCreateUserActionRequest"),
    SEND_DOCUMENT("sendDocument"),
    SET_DOCUMENT("set_document"),
    GET_APPLICATION_STATUS("get_application_status"),
    GET_OFFER("get_offer"),

    APPLY_FOR_CREDIT("apply_for_credit"),
    CHECK_FOR_CREDIT("check_for_credit"),

    CREATE_CONTRACT("create_contract"),
    GET_CONTRACT("get_contract"),

    GET_PRODUCTS("get_products"),
    VALIDATE_RULES("validate_rules"),
    VALIDATE_SUBSCRIPTION("validate_subscription"),
    GET_PERSONALIZED_CALCULATIONS("get_personalized_calculations"),
    FINALIZE_SUBSCRIPTION("finalize_subscription"),
    CANCEL_SUBSCRIPTION("cancel_subscription"),

    GET_DOCUMENT_DEFINITIONS("get_document_definitions"),
    CREATE_DOCUMENT_DEFINITION("create_document_definition"),
    UPLOAD_DOCUMENT("upload_document"),
    GET_IDENT("get_ident"),
    CREATE_IDENT("create_ident"),
    GET_USER_DATA("get_user_data"),
    UPDATE_USER_DATA("update_user_data"),

    PROCESS_INITIAL_DATA("process_initial_data"),
    PROCESS_OFFERS_TO_BEAT("process_offers_to_beat"),
    GET_OFFER_STATUS("get_offer_status"),
    SEND_OFFER_CHOICE("send_offer_choice"),


    APPLY_FOR_LOAN("apply_for_loan");

    private final String name;
}
