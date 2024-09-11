package de.joonko.loan.webhooks.aion.repositories;

import de.joonko.loan.webhooks.aion.model.AionOfferStatus;

import lombok.Data;

@Data
public class Payload {
    private String processInstanceId;
    private String iban;
    private String offerId;
    private String customerId;
    private AionOfferStatus status;
    private String customerInfo;
}
