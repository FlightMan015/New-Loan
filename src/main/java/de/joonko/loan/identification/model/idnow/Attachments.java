package de.joonko.loan.identification.model.idnow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Attachments {
    private String pdf;
    private String xml;
    private String videolog;
    private String idbackside;
    private String idfrontside;
    private String security1;
    private String userface;
    private String security2;

    @JsonProperty("security_covered")
    private String securityCovered;

    private String security3;
}
