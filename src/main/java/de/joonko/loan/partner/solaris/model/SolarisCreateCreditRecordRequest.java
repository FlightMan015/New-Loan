package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class SolarisCreateCreditRecordRequest {

    private String source;
}
