package de.joonko.loan.webhooks.idnow.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.joonko.loan.webhooks.idnow.model.enums.IdentificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentificationProcess {

    private String result;
    private String reason;
    @JsonProperty("agentname")
    private String agentName;
    @JsonProperty("identificationtime")
    private String identificationTime;
    private IdentificationType type;
    @JsonProperty("transactionnumber")
    private String transactionNumber;
    @JsonProperty("companyid")
    private String companyId;
    private String id;
}
