package de.joonko.loan.webhooks;

import de.joonko.loan.webhooks.idnow.model.ContactData;
import de.joonko.loan.webhooks.idnow.model.Identification;
import de.joonko.loan.webhooks.idnow.model.IdentificationDocument;
import de.joonko.loan.webhooks.idnow.model.IdentificationProcess;
import de.joonko.loan.webhooks.idnow.model.Question;
import de.joonko.loan.webhooks.idnow.model.enums.IdentificationResult;
import de.joonko.loan.webhooks.webid.model.request.Ident;

import java.util.Map;

public class WebhookFixtures {
    public static Identification getWebhookNotificationRequest() {
        return Identification.builder()
                .contactData(ContactData.builder().email("example@example.com").mobilePhone("").build())
                .attachments(Map.of("key", "value"))
                .customData(Map.of("key", "value"))
                .identificationDocument(IdentificationDocument.builder().build())
                .identificationProcess(IdentificationProcess.builder().result(IdentificationResult.SUCCESS.name()).transactionNumber("abcded").build())
                .questions(Map.of("key", new Question("someValue")))
                .build();
    }

    public static Ident getWebidNotificationRequest() {
        return Ident.builder()
                .success(true)
                .transactionId("5f845546b8335518fab2ddb2")
               .build();
    }
}
