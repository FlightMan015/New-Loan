package de.joonko.loan.metric.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TimerMetricLabel {
    PERSONAL_INFO("personal_info"),
    ADDITIONAL_INFO("additional_info"),
    TRANSACTIONAL_DATA_DS("transactional_data_ds"),
    TRANSACTIONAL_DATA_DAC("transactional_data_dac"),
    OFFER_STATE("offer_state"),
    LOAN_DEMAND("loan_demand");

    private final String name;
}
