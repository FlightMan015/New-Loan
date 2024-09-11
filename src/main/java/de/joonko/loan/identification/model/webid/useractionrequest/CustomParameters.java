
package de.joonko.loan.identification.model.webid.useractionrequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CustomParameters {
    private String md;
    @JsonProperty("md_ti")
    private String mdTi;
}
