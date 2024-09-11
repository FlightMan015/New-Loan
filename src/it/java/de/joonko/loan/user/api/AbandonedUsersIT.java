package de.joonko.loan.user.api;

import de.joonko.loan.avro.dto.loan_offers.LoanOffersMessage;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.messaging.config.KafkaTestConfig;
import de.joonko.loan.user.states.UserStatesStore;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@AutoConfigureWebTestClient
class AbandonedUsersIT extends KafkaTestConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private DateTimeProvider dateTimeProvider;

    @SpyBean
    private AuditingHandler auditingHandler;

    private static final String ABANDONED_USERS_URI = "/admin/v1/user/abandoned";
    private static final String USER_USERNAME = "user123";
    private static final String USER_PASSWORD = "pass123";
    private static final String ADMIN_USERNAME = "admin123";
    private static final String ADMIN_PASSWORD = "pass123";

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("loanOfferStore");
        auditingHandler.setDateTimeProvider(dateTimeProvider);
    }

    @Test
    void get401WhenMissingCredentials() {
        webClient
                .post()
                .uri(URI.create(ABANDONED_USERS_URI))
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void get403WhenUserCredentials() {
        webClient
                .post()
                .uri(URI.create(ABANDONED_USERS_URI))
                .headers(headers -> headers.setBasicAuth(USER_USERNAME, USER_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void get200WhenNoAbandonedUsersExist() {
        webClient
                .post()
                .uri(URI.create(ABANDONED_USERS_URI))
                .headers(headers -> headers.setBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void get200WhenAbandonedUsersExist() {
        // given
        mongoTemplate.insertAll(List.of(
                buildUserStatesStore("2eb498cb-ec0c-4cd8-8381-9751b42bf17f", 73277L),
                buildUserStatesStore("3704060c-2a3d-40ae-8842-d51660c41937", 48032L),
                buildUserStatesStore("adb1d947-a944-4aaf-87a0-079640fbd033", 35325L),
                buildUserStatesStore("ff0f26d1-332b-419a-b0ee-b7eec3a72dca", 38392L),
                buildUserStatesStore("7d4baa6d-bd0a-4ea7-9e41-e10e56d193ec", 98597L)
        ));

        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now().minusDays(80)));
        mongoTemplate.insert(LoanOfferStore.builder().userUUID("2eb498cb-ec0c-4cd8-8381-9751b42bf17f").applicationId("h328f9g239").build());

        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now().minusDays(7)));
        mongoTemplate.insert(LoanOfferStore.builder().userUUID("3704060c-2a3d-40ae-8842-d51660c41937").applicationId("3h279fg239f").build());

        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now().minusDays(7)));
        mongoTemplate.insert(LoanOfferStore.builder().userUUID("adb1d947-a944-4aaf-87a0-079640fbd033").applicationId("h2893gh39").build());
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now().minusDays(6)));
        mongoTemplate.insert(LoanOfferStore.builder().userUUID("adb1d947-a944-4aaf-87a0-079640fbd033").applicationId("gj98h59hgr").build());

        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now().minusDays(7)));
        mongoTemplate.insertAll(List.of(
                LoanOfferStore.builder().userUUID("ff0f26d1-332b-419a-b0ee-b7eec3a72dca").applicationId("dh892dfisf").kycStatus("INITIATED").build(),
                LoanOfferStore.builder().userUUID("7d4baa6d-bd0a-4ea7-9e41-e10e56d193ec").applicationId("g34h893vc").offerStatus("REVIEW_PENDING").build()));

        // when
        // then
        webClient
                .post()
                .uri(URI.create(ABANDONED_USERS_URI))
                .headers(headers -> headers.setBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        ConsumerRecords<String, LoanOffersMessage> records = KafkaTestUtils.getRecords(testLoanOfferConsumer, 3000);
        assertEquals(3, records.count());
    }

    private UserStatesStore buildUserStatesStore(String userUuid, Long bonifyUserId) {
        final var userStateStore = new UserStatesStore();
        userStateStore.setUserUUID(userUuid);
        userStateStore.setBonifyUserId(bonifyUserId);

        return userStateStore;
    }
}
