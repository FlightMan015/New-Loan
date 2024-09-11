package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class SolarisAccountSnapshotUpdateRequest {

    @JsonProperty("account_snapshot_id")
    private String accountSnapshotId;
}
