package de.joonko.loan.partner.consors.testData;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.joonko.loan.partner.consors.ConsorsFixtures;
import de.joonko.loan.partner.consors.model.SubscriptionStatus;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static de.joonko.loan.partner.consors.ConsorsFixtures.*;

public class ConsorsClientMocks {

    private final WireMockServer mockServer;

    public ConsorsClientMocks(WireMockServer mockServer) {
        this.mockServer = mockServer;
    }

    public void setGetTokenExpectations() {
        mockServer.stubFor(
                WireMock.post("/consors/common-services/cfg/token/neu_test?version=5.0")
                        .withRequestBody(equalTo("username=someUser&password=somePassword"))
                        .willReturn(aResponse()
                                .withBody(ConsorsFixtures.get200Auth())
                                .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    public void setGetProductsExpectations() {
        mockServer.stubFor(
                WireMock.get("/consors/ratanet-api/cfg/partner/neu_test/products?version=5.0")
                        .withHeader("Authorization", equalTo("Bearer jwtToken"))
                        .willReturn(aResponse()
                                .withBody(ConsorsFixtures.PRODUCT_RESPONSE)
                                .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    public void setValidationRulesExpectations() {
        mockServer.stubFor(
                WireMock.get("/consors/ratanet-api/cfg/partner/freie_verfuegung/validationrules?version=5.0")
                        .withHeader("Authorization", equalTo("Bearer jwtToken"))
                        .willReturn(aResponse()
                                .withBody(ConsorsFixtures.VALIDATION_RULES_RESPONSE)
                                .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    public void setValidateSubscriptionExpectations() {
        mockServer.stubFor(
                WireMock.post("/consors/ratanet-api/cfg/subscription/freie_verfuegung?version=5.0")
                        .withHeader("Authorization", equalTo("Bearer jwtToken"))
                        .willReturn(aResponse()
                                .withBody(VALIDATE_SUBSCRIPTION_RESPONSE)
                                .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    public void setPersonalizeCalculationsExpectations() {
        mockServer.stubFor(
                WireMock.put("/consors/ratanet-api/cfg/subscription/freie_verfuegung/744151656e5743456b614730422b7379626a7464356d42736f6f7a38527779327a777a6c6f754d4f7645413d/personalizedcalculations?version=5.0")
                        .withHeader("Authorization", equalTo("Bearer jwtToken"))
                        .willReturn(aResponse()
                                .withBody(PERSONALIZED_CALCULATION_RESPONSE)
                                .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    public void setFinalizeCalculationsExpectations(SubscriptionStatus subscriptionStatus) {
        mockServer.stubFor(
                WireMock.put("/consors/ratanet-api/cfg/subscription/freie_verfuegung/6178796253616d6f6d70505045356c394f4f384562536a6953453667564934672b352f55742b57663836303d/finalizesubscription?version=5.0")
                        .withHeader("Authorization", equalTo("Bearer jwtToken"))
                        .willReturn(aResponse()
                                .withBody(getFinalizeCalculationResponse(subscriptionStatus))
                                .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    public void setCancelSubscriptionExpectations() {
        mockServer.stubFor(
                WireMock.delete("/consors/ratanet-api/cfg/subscription/freie_verfuegung/6178796253616d6f6d70505045356c394f4f384562536a6953453667564934672b352f55742b57663836303d?version=5.0")
                        .withHeader("Authorization", equalTo("Bearer jwtToken"))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                        )
        );
    }

    public void setGetContractExpectations() {
        mockServer.stubFor(
                WireMock.get("/consors/ratanet-api/cfg/subscription/freie_verfuegung/6178796253616d6f6d70505045356c394f4f384562536a6953453667564934672b352f55742b57663836303d/documents?version=5.0")
                        .withHeader("Authorization", equalTo("Bearer jwtToken"))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withBody(new byte[]{0})
                                .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_OCTET_STREAM.getMimeType())
                        )

        );
    }
}
