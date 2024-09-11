package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SolarisGetApplicationStatusResponse {

    private String id;

    @JsonProperty("status_description")
    private String statusDescription;

    private String status;

    @JsonProperty("signing_id")
    private String signingId;

    @JsonProperty("loan_id")
    private String loanId;

    @JsonProperty("approximate_total_loan_expenses")
    private AmountValue approximateTotalExpenses;

    private SolarisGetOffersResponse solarisGetOffersResponse;
}
