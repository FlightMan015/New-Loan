package de.joonko.loan.partner.aion.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreditApplicationRequest {

    @JsonProperty("processName")
    @Builder.Default
    private ProcessName processName = ProcessName.DE_CASH_LOAN;

    @JsonProperty("variables")
    private List<Variable> variables;

    @Data
    @Builder
    public static class Variable {

        @JsonProperty("name")
        private TransmissionDataType transmissionDataType;

        @JsonProperty("value")
        private TransmissionData transmissionData;
    }
}
