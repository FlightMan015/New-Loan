package de.joonko.loan.webhooks.aion.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Payload {

    @NotNull(message = "processInstance must not be null")
    private String processInstanceId;

    private String iban;

    private String offerId;

    private String customerId;

    @NotNull(message = "status must not be null")
    private AionOfferStatus status;

    private String customerInfo;
}
