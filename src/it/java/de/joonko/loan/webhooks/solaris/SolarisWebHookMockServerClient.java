package de.joonko.loan.webhooks.solaris;

import com.github.tomakehurst.wiremock.WireMockServer;
import de.joonko.loan.webhooks.solaris.enums.WebhookIdentificationStatus;
import de.joonko.loan.webhooks.solaris.model.SolarisIdNowWebhookRequest;

public class SolarisWebHookMockServerClient {
    private final WireMockServer mockServer;

    SolarisWebHookMockServerClient(WireMockServer mockServer) {
        this.mockServer = mockServer;
    }
    public SolarisIdNowWebhookRequest fakeSolarisIdNowWebhookRequest() {
        SolarisIdNowWebhookRequest request = new SolarisIdNowWebhookRequest();
        request.setIdentificationId("ident-1234");
        request.setStatus(WebhookIdentificationStatus.successful);
        return request;
    }
}
