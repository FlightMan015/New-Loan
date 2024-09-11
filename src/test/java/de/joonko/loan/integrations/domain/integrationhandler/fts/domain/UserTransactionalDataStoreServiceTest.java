package de.joonko.loan.integrations.domain.integrationhandler.fts.domain;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("integration")
@SpringBootTest
@ExtendWith(RandomBeansExtension.class)
class UserTransactionalDataStoreServiceTest {

    @Autowired
    private UserTransactionalDataStoreService subject;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void getKycRelatedPersonalDetails() {
        // given
        final var USER_ID = "83709587-d6c6-4c48-8bb6-ce584dec2e81";
        final var userTransactionalDataStore = new UserTransactionalDataStore();
        userTransactionalDataStore.setUserUUID(USER_ID);
        mongoTemplate.insert(userTransactionalDataStore);

        // when
        var kycRelatedMono = subject.getKycRelatedPersonalDetails(USER_ID);

        // then
        StepVerifier.create(kycRelatedMono).expectNextCount(1).verifyComplete();
    }

    @Test
    void deleteByIds() {
        // given
        final var userUuids = List.of("19523c4e-be69-4197-b3fb-2e8bc63f8bb4", "aed201f6-1307-4035-a7fd-758072762e6a");
        mongoTemplate.insertAll(getTestData());

        // when
        final var actualResult = subject.deleteByIds(userUuids);

        // then
        StepVerifier.create(actualResult).consumeNextWith(l -> assertEquals(2, l.size())).verifyComplete();
    }

    private List<UserTransactionalDataStore> getTestData() {
        return List.of(
                build("19523c4e-be69-4197-b3fb-2e8bc63f8bb4"),
                build("aed201f6-1307-4035-a7fd-758072762e6a"),
                build("ce1cd249-3a47-4fa4-b1cb-83c97762e691")
        );
    }

    private UserTransactionalDataStore build(String userId) {
        var userTransactionalDataStore = new UserTransactionalDataStore();
        userTransactionalDataStore.setUserUUID(userId);
        return userTransactionalDataStore;
    }
}
