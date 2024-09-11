package de.joonko.loan.user.domain.fts;

import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.user.states.TransactionalDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("integration")
@SpringBootTest
class UsersFtsDeletionServiceTest {

    @Autowired
    private UsersFtsDeletionService subject;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void delete() {
        // given
        mongoTemplate.insertAll(List.of(
                buildUserStatesStore("fbbd6b46-64de-44d3-8a9a-82a4b641af1f", OffsetDateTime.now().minusDays(65)),
                buildUserStatesStore("3bc6f7eb-b114-4fe5-bc01-470506ea5510", OffsetDateTime.now().minusDays(20)),
                buildUserStatesStore("3f3cc02d-6c56-4209-8870-9bedd7bd70c7", OffsetDateTime.now().minusDays(80)),
                buildUserStatesStoreWithEmptyFtsState("2bfccaff-b93c-4d57-89a0-1cd022a5505f"),
                buildUserTransactionalDataStore("fbbd6b46-64de-44d3-8a9a-82a4b641af1f")
        ));

        // when
        var actualDeleted = subject.delete();


        // then
        assertAll(
                () -> StepVerifier.create(actualDeleted).consumeNextWith(l -> assertEquals(2, l.size())).verifyComplete(),
                () -> assertTrue(mongoTemplate.find(new Query(Criteria.where("userUUID").is("fbbd6b46-64de-44d3-8a9a-82a4b641af1f")), UserTransactionalDataStore.class).isEmpty()),
                () -> assertEquals(4, mongoTemplate.findAll(UserStatesStore.class).size()),
                () -> assertFalse(mongoTemplate.find(new Query(Criteria.where("userUUID").is("fbbd6b46-64de-44d3-8a9a-82a4b641af1f")), UserStatesStore.class).get(0).getTransactionalDataStateDetails().getUserVerifiedByBankAccount())
        );
    }

    private UserStatesStore buildUserStatesStore(String userUuid, OffsetDateTime responseFromDataSolution) {
        var userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);
        userStatesStore.setTransactionalDataStateDetails(
                TransactionalDataStateDetails.builder().responseFromDataSolution(responseFromDataSolution).build()
        );

        return userStatesStore;
    }

    private UserStatesStore buildUserStatesStoreWithEmptyFtsState(String userUuid) {
        var userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);

        return userStatesStore;
    }

    private UserTransactionalDataStore buildUserTransactionalDataStore(String userUuid) {
        var userTransactionalDataStore = new UserTransactionalDataStore();
        userTransactionalDataStore.setUserUUID(userUuid);
        return userTransactionalDataStore;
    }
}
