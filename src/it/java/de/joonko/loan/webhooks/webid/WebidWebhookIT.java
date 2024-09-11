package de.joonko.loan.webhooks.webid;

import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.IdentificationLink;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.webhooks.webid.model.request.Ident;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureWebTestClient
class WebidWebhookIT {

    private static final String WEBID_WEBHOOK_URL = "/loan/webid/webhook-notification";
    private static final String USERNAME = "user123";
    private static final String PASSWORD = "pass123";

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void get200WebhookIdent() {
        final var ident = Ident.builder()
                .transactionId("transactionId")
                .responseType("responseType")
                .success(true)
                .build();

        mongoTemplate.insertAll(getIdentificationLinks());
        mongoTemplate.insertAll(getLoanOfferStoreList());

        webClient
                .post()
                .uri(URI.create(WEBID_WEBHOOK_URL))
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .bodyValue(ident)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void get401WebhookIdent() {
        webClient
                .post()
                .uri(URI.create(WEBID_WEBHOOK_URL))
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void get500WhenExternalIdentDoesNotExist() {
        Ident ident = Ident.builder()
                .transactionId("transactionId500")
                .responseType("responseType")
                .success(true)
                .build();

        webClient
                .post()
                .uri(URI.create(WEBID_WEBHOOK_URL))
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ident)
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody(String.class)
                .value(body ->
                        assertEquals("Not able to find IdentificationLink for externalIdentId: " + ident.getTransactionId(), body)
                );
    }

    private List<LoanOfferStore> getLoanOfferStoreList() {
        return List.of(
                LoanOfferStore.builder().loanOfferId("37fh9287hf92").isAccepted(true).userUUID("45546-5f845546b833-5518fab2d")
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build())
                        .build(),
                LoanOfferStore.builder().loanOfferId("cn329c8b").userUUID("45546-5f845546b833-5518fab2d").build()
        );
    }

    private List<IdentificationLink> getIdentificationLinks() {
        return List.of(
                IdentificationLink.builder().externalIdentId("transactionId").offerId("37fh9287hf92").build(),
                IdentificationLink.builder().externalIdentId("transactionId2").offerId("9h8329f").build()
        );
    }
}
