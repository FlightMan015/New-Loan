package de.joonko.loan.webhooks.aion.testdata;

import de.joonko.loan.webhooks.aion.model.AionOfferStatus;
import de.joonko.loan.webhooks.aion.model.AionWebhookRequest;
import de.joonko.loan.webhooks.aion.model.AionWebhookType;
import de.joonko.loan.webhooks.aion.model.Payload;

import java.time.ZonedDateTime;
import java.util.UUID;

import static java.util.UUID.randomUUID;

public class AionWebHookServiceTestData {

    public AionWebhookRequest getRequestWithSuccessStatus(final AionWebhookType type) {
        return AionWebhookRequest.builder()
                .id(randomUUID().toString())
                .sourceSystem("BPM")
                .eventDateTime(ZonedDateTime.now())
                .type(type.getValue())
                .payload(Payload.builder()
                        .processInstanceId(randomUUID().toString())
                        .iban("DExxxx")
                        .offerId("f5684d24-3682-44c0-8e2b-190b2a30c491")
                        .customerId("3928639")
                        .status(AionOfferStatus.SUCCESS)
                        .customerInfo("Your loan was disbursed and money transferred to your account")
                        .build())
                .build();
    }

    public AionWebhookRequest getRequestWithFailureStatus(final AionWebhookType type) {
        return AionWebhookRequest.builder()
                .id(randomUUID().toString())
                .sourceSystem("BPM")
                .eventDateTime(ZonedDateTime.now())
                .type(type.getValue())
                .payload(Payload.builder()
                        .processInstanceId(randomUUID().toString())
                        .iban("DExxxx")
                        .offerId("f5684d24-3682-44c0-8e2b-190b2a30c491")
                        .customerId("3928639")
                        .status(AionOfferStatus.FAILED)
                        .customerInfo("Unfortunately we can not grant you the loan")
                        .build())
                .build();
    }

    public AionWebhookRequest getRequestWithManualAMLStatus(final AionWebhookType type) {
        return AionWebhookRequest.builder()
                .id(randomUUID().toString())
                .sourceSystem("BPM")
                .eventDateTime(ZonedDateTime.now())
                .type(type.getValue())
                .payload(Payload.builder()
                        .processInstanceId(randomUUID().toString())
                        .iban("DExxxx")
                        .offerId("f5684d24-3682-44c0-8e2b-190b2a30c491")
                        .customerId("3928639")
                        .status(AionOfferStatus.MANUAL_AML)
                        .customerInfo("Unfortunately we can not grant you the loan")
                        .build())
                .build();
    }

    public AionWebhookRequest getRequest(String processId) {
        return AionWebhookRequest.builder()
                .id(randomUUID().toString())
                .sourceSystem("BPM")
                .eventDateTime(ZonedDateTime.now())
                .type(AionWebhookType.CASHLOAN_OPEN.getValue())
                .payload(Payload.builder()
                        .processInstanceId(processId)
                        .iban("DExxxx")
                        .offerId("f5684d24-3682-44c0-8e2b-190b2a30c491")
                        .customerId("3928639")
                        .status(AionOfferStatus.FAILED)
                        .customerInfo("Unfortunately we can not grant you the loan")
                        .build())
                .build();
    }
}
