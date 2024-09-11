package de.joonko.loan.webhooks.solaris;

import com.github.tomakehurst.wiremock.WireMockServer;
import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.db.repositories.LoanDemandStoreRepository;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.IdentificationLink;
import de.joonko.loan.identification.service.IdentificationLinkRepository;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.partner.solaris.SolarisAcceptOfferResponseStore;
import de.joonko.loan.partner.solaris.SolarisAcceptOfferResponseStoreRepository;
import de.joonko.loan.partner.solaris.SolarisStoreService;
import de.joonko.loan.webhooks.IdNowWebhookMockServerClient;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RandomBeansExtension.class)
@AutoConfigureWebTestClient
@SpringBootTest
@ContextConfiguration(initializers = WireMockInitializer.class)
@ActiveProfiles("integration")
class SolarisWebHookNotificationIT {
    @Autowired
    private WebTestClient webClient;
    @Autowired
    private WireMockServer mockServer;
    @Autowired
    private SolarisStoreService solarisStoreService;
    @Autowired
    private SolarisAcceptOfferResponseStoreRepository solarisAcceptOfferResponseStoreRepository;
    @Autowired
    private IdentificationLinkRepository identificationLinkRepository;
    @Autowired
    private LoanOfferStoreService loanOfferStoreService;
    @Autowired
    private LoanDemandStoreRepository loanApplicationStoreRepository;

    private SolarisWebHookMockServerClient solarisWebhookMock;

    @Random
    private SolarisAcceptOfferResponseStore solarisAcceptOfferResponseStore;
    @Random
    private IdentificationLink identificationLink;
    @Random
    private LoanOfferStore loanOfferStore;
    @Random
    private LoanDemandStore loanDemandStore;


    private static final String SOLARIS_IDENTIFICATION_NOTIFICATION_URL = "/loan/id-now/joonkosolaris/identification-notification";

    @BeforeEach
    void clearMockServerExpectations() {
        solarisWebhookMock = new SolarisWebHookMockServerClient(mockServer);
    }

    @Test
    void shouldSuccessSolarisIdnowWebhook() {
        webClient
                .mutate()
                .responseTimeout(Duration.ofSeconds(10))
                .build()
                .post()
                .uri(URI.create(SOLARIS_IDENTIFICATION_NOTIFICATION_URL))
                .body(BodyInserters.fromValue(solarisWebhookMock.fakeSolarisIdNowWebhookRequest()))
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void consumesSolarisIdnowWebhook() {
        solarisAcceptOfferResponseStore.setApplicationId("1234");
        solarisAcceptOfferResponseStore.setIdentificationId("ident-1234");
        solarisAcceptOfferResponseStoreRepository.save(solarisAcceptOfferResponseStore);

        identificationLink.setApplicationId("1234");
        identificationLink.setOfferId(loanOfferStore.getLoanOfferId());
        identificationLinkRepository.save(identificationLink);

        loanOfferStoreService.save(loanOfferStore);

        loanDemandStore.setApplicationId("1234");
        loanApplicationStoreRepository.save(loanDemandStore);

        webClient
                .mutate()
                .responseTimeout(Duration.ofSeconds(10))
                .build()
                .post()
                .uri(URI.create(SOLARIS_IDENTIFICATION_NOTIFICATION_URL))
                .body(BodyInserters.fromValue(solarisWebhookMock.fakeSolarisIdNowWebhookRequest()))
                .exchange()
                .expectStatus()
                .isOk();
    }
}