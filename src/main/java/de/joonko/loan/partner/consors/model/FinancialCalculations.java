package de.joonko.loan.partner.consors.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FinancialCalculations {
    private long defaultIndex;
    private List<FinancialCalculation> financialCalculation;
    private List<String> insuranceTypes;
    private List<Long> durationStepping;
    private List<Long> amountStepping;
    private String subscriptionStatus;
    private String refusalCategory;
    @JsonProperty("_links")
    private List<LinkRelation> links;
    private DebugInfo debugInfo;
}
