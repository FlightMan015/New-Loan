package de.joonko.loan.metric.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Process {
    GET_OFFERS("get_offers"),
    PROCESS_INITIAL_DATA("process_initial_data"),
    PROCESS_OFFERS_TO_BEAT("process_offers_to_beat");


    private final String name;
}
