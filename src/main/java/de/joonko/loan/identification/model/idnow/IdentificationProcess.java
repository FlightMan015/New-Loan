package de.joonko.loan.identification.model.idnow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class IdentificationProcess {

    private String result;

    @JsonProperty("companyid")
    private String companyId;

    @JsonProperty("filename")
    private String fileName;

    @JsonProperty("agentname")
    private String agentName;

    @JsonProperty("identificationtime")
    private String identificationTime;

    private String id;
    private String href;
    private String type;

    @JsonProperty("transactionnumber")
    private String transactionNumber;

}
