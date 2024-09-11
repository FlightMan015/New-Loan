package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
@Builder
public class SolarisUploadAccoutSnapRequest {

    private String source;

    @JsonProperty("snapshot_data")
    @NotNull(message = "Account snapshot cannot be null")
    private AccountSnapshot accountSnapshot;

}
