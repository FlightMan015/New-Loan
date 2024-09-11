package de.joonko.loan.partner.consors.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PersonalizedCalculationsResponse {
    @JsonProperty("financialCalculations")
    private FinancialCalculations financialCalculations;

}
