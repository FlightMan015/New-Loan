package de.joonko.loan.metric.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CounterMetricLabel {
    PRE_CHECK("pre_check"),
    OFFER_DEMAND("offer_demand"),
    OFFER_DEMAND_RECOMMENDED("offer_demand_recommended"),
    KYC("kyc"),
    OFFER_STATUS("offer_status"),
    API_STATUS("api_status");

    private final String name;
}
