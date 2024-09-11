package de.joonko.loan.integrations.domain.integrationhandler.fts.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("integration")
@SpringBootTest
class UserTransactionalDraftDataStoreServiceTest {

    @Autowired
    private UserTransactionalDraftDataStoreService subject;

    @Autowired
    private MongoTemplate mongoTemplate;

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

    private List<UserTransactionalDraftDataStore> getTestData() {
        return List.of(
                build("19523c4e-be69-4197-b3fb-2e8bc63f8bb4"),
                build("aed201f6-1307-4035-a7fd-758072762e6a"),
                build("ce1cd249-3a47-4fa4-b1cb-83c97762e691")
        );
    }

    private UserTransactionalDraftDataStore build(String userId) {
        var userTransactionalDraftDataStore = new UserTransactionalDraftDataStore();
        userTransactionalDraftDataStore.setUserUUID(userId);
        return userTransactionalDraftDataStore;
    }
}
