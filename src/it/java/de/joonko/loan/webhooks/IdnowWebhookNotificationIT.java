package de.joonko.loan.webhooks;

import com.github.tomakehurst.wiremock.WireMockServer;

import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.db.vo.OfferAcceptedEnum;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.CreateIdentResponse;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.service.IdentificationAuditService;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.webhooks.idnow.model.Identification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import reactor.core.publisher.Mono;

@ExtendWith(RandomBeansExtension.class)
@AutoConfigureWebTestClient
@SpringBootTest
@ContextConfiguration(initializers = WireMockInitializer.class)
@ActiveProfiles("integration")
public class IdnowWebhookNotificationIT {

    @Autowired
    private WebTestClient webClient;
    @Autowired
    private WireMockServer mockServer;
    @Autowired
    private IdentificationAuditService identificationAuditService;
    @Autowired
    private LoanOfferStoreService loanOfferStoreService;
    @Autowired
    private IdentificationLinkService identificationLinkService;

    private IdNowWebhookMockServerClient idNowWebhookMock;

    @Random
    private Identification identification;
    @Random
    private CreateIdentResponse createIdentResponse;
    @Random
    private CreateIdentRequest createIdentRequest;
    @Random
    private LoanOfferStore loanOfferStore;
    @Random
    private LoanOffer loanOffer;


    private static final String IDENTIFICATION_NOTIFICATION_URL = "/loan/id-now/identification-notification";

    @BeforeEach
    void clearMockServerExpectations() {
        idNowWebhookMock = new IdNowWebhookMockServerClient(mockServer);
    }

    @Test
    void consumesIdnowWebhook() {
        idNowWebhookMock.fakeConsumerWebHookNotification();
        createIdentRequest.setApplicationId("123456");
        identification.getIdentificationProcess().setTransactionNumber("123456");

        identificationAuditService.kycLinkCreated(createIdentResponse, createIdentRequest);
        LoanOfferStore loanOfferStorefromDatabase = loanOfferStoreService.saveAll(List.of(loanOffer), "user uuid", "123456", "parent-123456").blockFirst();
        loanOfferStoreService.updateAcceptedStatus(loanOfferStorefromDatabase.getLoanOfferId(), "12345", OfferAcceptedEnum.USER);
        identificationLinkService.add("123456", loanOfferStorefromDatabase.getLoanOfferId(), "CONSOR", IdentificationProvider.ID_NOW, "ext123456", "https://go.test.idnow.de/TST-HDJBX");

        webClient
                .mutate()
                .responseTimeout(Duration.ofSeconds(10))
                .build()
                .post()
                .uri(URI.create(IDENTIFICATION_NOTIFICATION_URL))
                .body(Mono.just(idNowWebhookMock.fakeIdentificationRequest()), Identification.class)
                .exchange()
                .expectStatus()
                .isOk();
    }
}
