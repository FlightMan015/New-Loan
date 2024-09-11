package de.joonko.loan.partner.consors.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsorsAcceptOfferResponse {
    private List<Integer> supportingDocumentsRequired;
    private SubscriptionStatus subscriptionStatus;
    private String contractIdentifier;
    private FinancialCalculation financialCalculation;
    private Map<String, List<String>> errors;
    @JsonProperty("_links")
    private List<Link> links;
    private Boolean daarequired;
}
