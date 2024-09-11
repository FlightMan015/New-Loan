package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolarisCreateCreditRecordResponse {


    private String id;

    private String status;

    @JsonProperty("person_id")
    private String personId;

    @JsonProperty("created_at")
    private Instant createdAt;



}
