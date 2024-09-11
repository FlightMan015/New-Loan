package de.joonko.loan.common.messaging;

public interface KafkaTopicNames {

    String DAC_ACCOUNT_SNAPSHOT = "b2b.dac_account";
    String DIGITAL_LOANS_REPORT_DATA = "b2b.silver.digital_loans_reports_data";


    String LOAN_OFFERS = "b2b.loan_offers";
    String LOAN_DEMAND = "b2b.loan_demand";


    String KYC_STATUS = "b2b.kyc-status";
    String KYC_INITIALIZATION = "b2b.kyc-initialization";

    String PERSONAL_USER_DETAILS = "b2b.personal-user-details";

    String USER_ADDITIONAL_INFO_REQUEST = "b2b.user_additional_information_request";
    String USER_ADDITIONAL_INFO_RESPONSE = "b2b.user_additional_information_response";

    String SALARY_ACCOUNT_REQUEST = "b2b.query_salary_account_request";
    String SALARY_ACCOUNT_RESPONSE = "b2b.query_salary_account_response";

    String USER_LOGIN_EVENT = "b2b.user_login_avro";
    String USER_DELETE_EVENT = "b2b.user_deletion_avro";
    String USER_UPDATE_EVENT = "b2b.user_update";
    String USER_CREATE_EVENT = "b2b.user_creation";

    String FINLEAP_TO_FTS = "b2b.finleap_to_fts";
}
