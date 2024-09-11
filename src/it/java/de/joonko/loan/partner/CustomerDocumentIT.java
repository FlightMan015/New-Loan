package de.joonko.loan.partner;

import de.joonko.loan.partner.solaris.SolarisAcceptOfferResponseStore;
import de.joonko.loan.partner.solaris.model.SolarisSignedDocTrail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;

import static de.joonko.loan.offer.testdata.SecurityTestData.mockEmailVerifiedJwt;
import static java.util.UUID.randomUUID;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@AutoConfigureWebTestClient
@SpringBootTest
@ActiveProfiles("integration")
public class CustomerDocumentIT {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WebTestClient webClient;

    private static final String CUSTOMER_DOC_URI = "/api/v1/loan/customer/document";
    private static final String USER_UUID1 = "35004d2f-ee8a-45fe-97e9-0542e1a0160b";
    private static final String USER_UUID2 = "35004d2f-ee8a-45fe-97e9-0542e1a0160b";

    @Test
    void sendCustomerDocWithEmptyData() {
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID1);

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(URI.create(CUSTOMER_DOC_URI))
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void sendCustomerDocWithOneData() {
        final String applicationId = randomUUID().toString();
        Jwt jwt = mockEmailVerifiedJwt(USER_UUID2);
        mongoTemplate.insert(getSolarisSignedDoc(applicationId));
        mongoTemplate.insert(getSolarisAcceptOfferResponse(applicationId));

        webClient
                .mutateWith(mockJwt().jwt(jwt))
                .get()
                .uri(URI.create(CUSTOMER_DOC_URI))
                .exchange()
                .expectStatus()
                .isOk();
    }

    private SolarisSignedDocTrail getSolarisSignedDoc(String applicationId) {
        return SolarisSignedDocTrail.builder()
                .applicationId(applicationId)
                .emailSent(false)
                .build();
    }

    private SolarisAcceptOfferResponseStore getSolarisAcceptOfferResponse(String applicationId) {
        return SolarisAcceptOfferResponseStore.builder()
                .applicationId(applicationId)
                .build();
    }
}
