package de.joonko.loan.webhooks.postbank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStore;
import de.joonko.loan.webhooks.postbank.model.PostbankOfferResponseEnvelope;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureWebTestClient(timeout = "360000")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostbankWebhookIT {

    private static final String POSTBANK_WEBHOOK_URL = "/loan/postbank/offers-notification";

    private static final String applicationIdForOffers = "6206867949933e389f2deb9d";
    private static final String loanReferenceNumber = "5716082";
    private static PostbankLoanDemandStore insertedPostbankLoanDemand;
    private static LoanDemandRequest loanDemandRequest;

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static ObjectMapper xmlMapper;

    private static PostbankWebhookFixtures testData;

    @BeforeAll
    static void beforeAll() {
        xmlMapper = new XmlMapper();
        testData = new PostbankWebhookFixtures();
    }

    @Test
    void shouldUnmarshallXML() throws JsonProcessingException {
        PostbankOfferResponseEnvelope value = xmlMapper.readValue(testData.getCreditResultOfferWebhook(), PostbankOfferResponseEnvelope.class);
        assertAll(
                () -> assertThat(value).isNotNull(),
                () -> assertThat(value.getBody()).isNotNull(),
                () -> assertThat(value.getBody().getUpdate()).isNotNull(),
                () -> assertThat(value.getBody().getUpdate().getArg0()).isNotNull()
        );

    }

    @Test
    @Order(1)
    void get401WhenHandlingWebhookWithoutAuthorization() {
        webClient
                .post()
                .uri(URI.create(POSTBANK_WEBHOOK_URL))
                .contentType(MediaType.TEXT_XML)
                .bodyValue(testData.getCreditResultOfferWebhook())
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @Order(2)
    @WithMockUser(roles = "ADMIN")
    void get403WhenHandlingWebhookForNotAllowedRoles() {
        webClient
                .post()
                .uri(URI.create(POSTBANK_WEBHOOK_URL))
                .contentType(MediaType.TEXT_XML)
                .bodyValue(testData.getCreditResultOfferWebhook())
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @Order(3)
    @WithMockUser(roles = "PB_USER")
    void get200AndErrorWhenHandlingWebhookForNonExistingApplication() {
        webClient
                .post()
                .uri(URI.create(POSTBANK_WEBHOOK_URL))
                .contentType(MediaType.TEXT_XML)
                .accept(MediaType.TEXT_XML)
                .bodyValue(testData.getCreditResultOfferWebhook())
                .exchange()
                .expectStatus()
                .isOk().expectBody()
                .xpath("/").exists()
                .xpath("/Envelope").exists()
                .xpath("/Envelope/Body").exists()
                .xpath("/Envelope/Body/updateResponse").exists()
                .xpath("/Envelope/Body/updateResponse/return").exists()
                .xpath("/Envelope/Body/updateResponse/return/success").exists()
                .xpath("/Envelope/Body/updateResponse/return/error").exists()
                .xpath("/Envelope/Body/updateResponse/return/error/code").exists()
                .xpath("/Envelope/Body/updateResponse/return/error/description").exists()
                .xpath("/Envelope/Body/updateResponse/return/success").isEqualTo("false")
                .xpath("/Envelope/Body/updateResponse/return/error/code").isEqualTo("-1")
                .xpath("/Envelope/Body/updateResponse/return/error/description").isEqualTo("general error");
    }

    @Disabled
    @Test
    @Order(4)
    @WithMockUser(roles = "PB_USER")
    void get200WhenHandlingWebhookForLoanOfferWithSuccessStatus() {
        insertedPostbankLoanDemand = mongoTemplate.insert(getPostbankLoanDemandStore(applicationIdForOffers, loanReferenceNumber));
        loanDemandRequest = mongoTemplate.insert(getLoanDemandRequest(insertedPostbankLoanDemand.getApplicationId()));

        webClient
                .post()
                .uri(URI.create(POSTBANK_WEBHOOK_URL))
                .contentType(MediaType.TEXT_XML)
                .accept(MediaType.TEXT_XML)
                .bodyValue(testData.getCreditResultOfferWebhook())
                .exchange()
                .expectStatus()
                .isOk().expectBody().xpath("/Envelope/Body/updateResponse/return/success").isEqualTo("true");

        final var saved = mongoTemplate.findById(insertedPostbankLoanDemand.getId(), PostbankLoanDemandStore.class);

        assertAll(
                () -> assertNotNull(saved),
                () -> assertEquals(applicationIdForOffers, saved.getApplicationId()),
                () -> assertEquals(1, saved.getCreditResults().size()),
                () -> assertEquals(2, saved.getCreditResults().stream().findFirst().get().getSavedContracts().size())
        );
    }

    @Disabled
    @Test
    @Order(5)
    @WithMockUser(roles = "PB_USER")
    void get200WhenHandlingWebhookForLoanOfferContractsWithSuccessStatus() {
        webClient
                .post()
                .uri(URI.create(POSTBANK_WEBHOOK_URL))
                .contentType(MediaType.TEXT_XML)
                .accept(MediaType.TEXT_XML)
                .bodyValue(testData.getCreditDocumentsReceivedWebhook())
                .exchange()
                .expectStatus()
                .isOk().expectBody().xpath("/Envelope/Body/updateResponse/return/success").isEqualTo("true");

        final var saved = mongoTemplate.findById(insertedPostbankLoanDemand.getId(), PostbankLoanDemandStore.class);

        assertAll(
                () -> assertNotNull(saved),
                () -> assertEquals(applicationIdForOffers, saved.getApplicationId()),
                () -> assertEquals(2, saved.getCreditResults().size())
        );
    }


    private PostbankLoanDemandStore getPostbankLoanDemandStore(final String applicationId, final String loanReferenceNumber) {
        return PostbankLoanDemandStore.builder()
                .contractNumber(loanReferenceNumber)
                .applicationId(applicationId)
                .build();
    }

    private LoanDemandRequest getLoanDemandRequest(final String applicationId) {
        return LoanDemandRequest.builder()
                .applicationId(applicationId)
                .userUUID(randomUUID().toString())
                .build();
    }
}
