package de.joonko.loan.webhooks.solaris.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.joonko.loan.webhooks.solaris.enums.WebhookIdentificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SolarisIdNowWebhookRequest {

    private String loanApplicationId;
    @JsonProperty("id")
    private String identificationId;
    private String method;
    private String url;
    private String reference;

    @JsonProperty("completed_at")
    private String completedAt;

    private WebhookIdentificationStatus status;

    @JsonProperty("person_id")
    private String personId;

}
