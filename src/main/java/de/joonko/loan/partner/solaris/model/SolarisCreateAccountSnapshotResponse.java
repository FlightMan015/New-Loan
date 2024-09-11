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
public class SolarisCreateAccountSnapshotResponse {

    @JsonProperty("id")
    private String accountSnapshotId;

    @JsonProperty("wizard_session_key")
    private String wizardSessionKey;

    private String location;

    @JsonProperty("account_id")
    private String accountId;

    private String personId;
    private SolarisGetOffersResponse offer;
}
