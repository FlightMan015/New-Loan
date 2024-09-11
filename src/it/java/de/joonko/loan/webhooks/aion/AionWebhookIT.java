package de.joonko.loan.webhooks.aion;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.partner.aion.model.CreditApplicationResponseStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.util.List;

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureWebTestClient
class AionWebhookIT {

    private static final String AION_WEBHOOK_URL = "/loan/aion/webhook";

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static AionWebhookFixtures testData;

    @BeforeAll
    static void beforeAll() {
        testData = new AionWebhookFixtures();
    }

    @Test
    void get200WhenHandlingWebhookWithSuccessStatus() {
        mongoTemplate.insertAll(getCreditApplications("cdb18fd5-529c-4dd7-a701-e31b434ec113", "applicationId123"));
        mongoTemplate.insertAll(getLoanOffers("loanOfferId123", "applicationId123"));

        webClient
                .post()
                .uri(URI.create(AION_WEBHOOK_URL))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testData.getSuccessWebhookRequest())
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void get200WhenHandlingWebhookWithFailureStatus() {
        mongoTemplate.insertAll(getCreditApplications("cdb18fd5-529c-4dd7-a701-e31b434ec114", "applicationId124"));
        mongoTemplate.insertAll(getLoanOffers("loanOfferId124", "applicationId124"));

        webClient
                .post()
                .uri(URI.create(AION_WEBHOOK_URL))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testData.getFailureWebhookRequest())
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void get404WhenHandlingWebhookWithNotExistingProcessId() {
        webClient
                .post()
                .uri(URI.create(AION_WEBHOOK_URL))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testData.getSuccessWebhookRequest())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void get400WhenHandlingWebhookWithInvalidType() {
        webClient
                .post()
                .uri(URI.create(AION_WEBHOOK_URL))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testData.getInvalidTypeWebhookRequest())
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    private List<LoanOfferStore> getLoanOffers(String loanOfferId, String applicationId) {
        return List.of(
                LoanOfferStore.builder()
                        .loanOfferId(loanOfferId)
                        .isAccepted(true)
                        .offerStatus("PENDING")
                        .userUUID("77a1dc11-9947-43f3-bee7-c6c21e849356")
                        .applicationId(applicationId)
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.AION.name())).build()).build()
        );
    }

    private List<CreditApplicationResponseStore> getCreditApplications(String processId, String applicationId) {
        return List.of(
                CreditApplicationResponseStore.builder()
                        .processId(processId)
                        .applicationId(applicationId)
                        .build()
        );
    }
}
