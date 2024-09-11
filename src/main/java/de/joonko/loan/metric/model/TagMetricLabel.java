package de.joonko.loan.metric.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TagMetricLabel {
    USER_ID("user_id"),
    OFFER_PROVIDER("offer_provider"),
    STATUS("status"),
    KYC_PROVIDER("kyc_provider"),
    HTTP_CODE("http_code"),
    API_COMPONENT("api_component"),
    API_NAME("api_name"),
    PROCESS("process");

    private final String name;
}
