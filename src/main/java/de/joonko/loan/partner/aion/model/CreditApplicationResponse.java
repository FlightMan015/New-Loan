package de.joonko.loan.partner.aion.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditApplicationResponse {

    @JsonProperty("processId")
    private String processId;

    private List<Variable> variables;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Variable {

        @JsonProperty("name")
        private AionResponseValueType name;

        @JsonProperty("value")
        private String value;
    }
}
