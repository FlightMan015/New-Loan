package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.Valid;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolarisAllApiRequest {
    @Valid
    private SolarisCreatePersonRequest solarisCreatePersonRequest;
    @Valid
    private SolarisGetOffersRequest solarisGetOffersRequest;
}
