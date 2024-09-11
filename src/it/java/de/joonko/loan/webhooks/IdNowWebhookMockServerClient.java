package de.joonko.loan.webhooks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import de.joonko.loan.webhooks.idnow.model.Address;
import de.joonko.loan.webhooks.idnow.model.ContactData;
import de.joonko.loan.webhooks.idnow.model.Identification;
import de.joonko.loan.webhooks.idnow.model.IdentificationDocument;
import de.joonko.loan.webhooks.idnow.model.IdentificationProcess;
import de.joonko.loan.webhooks.idnow.model.UserAttribute;
import de.joonko.loan.webhooks.idnow.model.UserData;
import de.joonko.loan.webhooks.idnow.model.enums.IdentificationResult;
import de.joonko.loan.webhooks.idnow.model.enums.MatchStatus;
import de.joonko.loan.webhooks.idnow.model.enums.ReviewStatus;

import org.apache.http.entity.ContentType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class IdNowWebhookMockServerClient {

    private final WireMockServer mockServer;

    IdNowWebhookMockServerClient(WireMockServer mockServer) {
        this.mockServer = mockServer;
    }

    public void fakeConsumerWebHookNotification() {
        mockServer.stubFor(
                WireMock.post("/loan/id-now/identification-notification")
                        .willReturn(aResponse()
                                .withBody(ResponseEntity.ok().build().toString())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())));
    }

    public Identification fakeIdentificationRequest() {
        return Identification.builder()
                .contactData(ContactData.builder()
                        .email("example@example.com")
                        .mobilePhone("")
                        .build())
                .userData(buildUserData())
                .attachments(Map.of("key", "value"))
                .customData(Map.of("key", "value"))
                .identificationDocument(IdentificationDocument.builder()
                        .build())
                .identificationProcess(IdentificationProcess.builder()
                        .result(IdentificationResult.SUCCESS.name())
                        .transactionNumber("ext123456")
                        .build())
                .questions(Map.of("key", new de.joonko.loan.webhooks.idnow.model.Question("someValue")))
                .build();
    }

    private UserData buildUserData() {
        return UserData.builder()
                .address(Address.builder()
                        .streetNumber(new UserAttribute<>(MatchStatus.ORIGINAL, ReviewStatus.MATCH, "41 B", "41 B", "true"))
                        .build())
                .build();
    }

}
