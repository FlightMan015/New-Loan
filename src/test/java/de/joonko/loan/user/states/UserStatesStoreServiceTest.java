package de.joonko.loan.user.states;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("integration")
class UserStatesStoreServiceTest {

    @Autowired
    private UserStatesStoreService userStatesStoreService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void getUserStatesStores() {
        // given
        mongoTemplate.insertAll(List.of(
                buildUserStatesStore("80dc78e1-6ef4-40d1-b8d0-3fb8bd742324"),
                buildUserStatesStore("539898af-cb62-44bc-aed6-9f8057623d4b"),
                buildUserStatesStore("9b1aab2e-1a0e-4c1a-9569-050af1d2f899")
        ));

        // when
        var userStatesMono = userStatesStoreService.findAllByUserUUID(List.of("80dc78e1-6ef4-40d1-b8d0-3fb8bd742324", "539898af-cb62-44bc-aed6-9f8057623d4b"));

        // then
        StepVerifier.create(userStatesMono).consumeNextWith(userStates -> assertEquals(2, userStates.size())).verifyComplete();
    }

    @Test
    void findAllBeforeDate() {
        // given
        mongoTemplate.insertAll(List.of(
                buildUserStates("4f2a09ec-93e7-4531-89dc-aacb0f0032a6", null, null),
                buildUserStates("f20df415-45a2-4556-81d3-ca6aa4c8ad61", null, OffsetDateTime.now().minusDays(65)),
                buildUserStates("ef5c6fd6-71b9-45e2-b972-4bb71678a5da", null, OffsetDateTime.now().minusDays(35)),
                buildUserStates("370ca508-4813-4265-9c3d-27a441b8b768", OffsetDateTime.now().minusDays(65), null),
                buildUserStates("59a48689-ddbe-4b11-86e7-1b6f44882cf2", OffsetDateTime.now().minusDays(35), null),
                buildUserStates("7c55f1d2-cacf-4d5e-817c-c39310dc8c8d", OffsetDateTime.now().minusDays(65), OffsetDateTime.now().minusDays(65)),
                buildUserStates("9f38f4d8-c86d-4020-b90a-bb92a847c549", OffsetDateTime.now().minusDays(35), OffsetDateTime.now().minusDays(35))
        ));
        var expectedIds = List.of("f20df415-45a2-4556-81d3-ca6aa4c8ad61", "370ca508-4813-4265-9c3d-27a441b8b768", "7c55f1d2-cacf-4d5e-817c-c39310dc8c8d");

        // when
        var userStatesMono = userStatesStoreService.findAllBeforeDate(OffsetDateTime.now().minusDays(60));

        // then
        StepVerifier.create(userStatesMono).consumeNextWith(userStates -> assertAll(
                () -> assertEquals(3, userStates.size()),
                () -> assertTrue(userStates.stream().map(UserStatesStore::getUserUUID).collect(toList()).containsAll(expectedIds))
        )).verifyComplete();
    }

    @Test
    void updateAll() {
        // given
        var list = List.of(
                buildUserStatesStore("d45d1dd7-81b9-497b-8721-8c0dd4e19c88"),
                buildUserStatesStore("ba5e6d61-96de-4252-823e-c8a902db944e"),
                buildUserStatesStore("aac24593-94f2-4af3-9e0e-0e292b00e614")
        );
        mongoTemplate.insertAll(list);
        list.forEach(l -> l.setTransactionalDataStateDetails(null));
        mongoTemplate.insert(buildUserStatesStore("9b6e9473-fd50-4573-966a-965845377a3a"));

        // when
        var userStatesMono = userStatesStoreService.updateAll(list);

        // then
        assertAll(
                () -> StepVerifier.create(userStatesMono)
                        .consumeNextWith(userStates -> assertEquals(3, userStates.size()))
                        .verifyComplete(),
                () -> assertNull(mongoTemplate.find(new Query(Criteria.where("userUUID").is("aac24593-94f2-4af3-9e0e-0e292b00e614")), UserStatesStore.class)
                        .get(0).getTransactionalDataStateDetails()),
                () -> assertNotNull(mongoTemplate.find(new Query(Criteria.where("userUUID").is("9b6e9473-fd50-4573-966a-965845377a3a")), UserStatesStore.class)
                        .get(0).getTransactionalDataStateDetails())
        );

    }

    @Test
    void saveUserStatesStore() {
        // given
        final var userStatesStore = buildUserStatesStore("4f60ad57-1ffa-4e40-9526-23baab926686");
        userStatesStore.setTenantId(UUID.randomUUID().toString());

        // when
        var userStatesStoreMono = userStatesStoreService.save(userStatesStore);

        // then
        StepVerifier.create(userStatesStoreMono)
                .consumeNextWith(userStates ->
                        assertEquals(userStatesStore.getTenantId(), mongoTemplate.findById("4f60ad57-1ffa-4e40-9526-23baab926686", UserStatesStore.class).getTenantId()))
                .verifyComplete();
    }

    @Test
    void filterUserStatesStore() {
        // given
        final var tenantId = UUID.randomUUID();
        final var endDateTime = OffsetDateTime.now();
        final var startDateTime = endDateTime.minusMonths(3);

        mongoTemplate.insertAll(List.of(
                buildUserStatesStore(tenantId, endDateTime.minusMonths(2), endDateTime.minusMonths(4)),
                buildUserStatesStore(tenantId, endDateTime.minusMonths(1)),

                buildUserStatesStore(tenantId, endDateTime.minusMonths(5), endDateTime.minusMonths(4)),
                buildUserStatesStore(tenantId, endDateTime.minusMonths(6)),
                buildUserStatesStore(tenantId),
                buildUserStatesStore(UUID.fromString("3998e936-6943-41f9-bdf2-db9f9ba3d5b5"), endDateTime.minusMonths(2), endDateTime.minusMonths(4)),
                buildUserStatesStore(UUID.fromString("5f3e8670-42d6-41d8-980d-ee3cfbbad5ca"), endDateTime.minusMonths(1)),
                buildUserStatesStore(UUID.fromString("e37647af-9171-466e-b533-08daad956aa3")),
                buildUserStatesStore(UUID.fromString("5bc6b355-8eb5-4b94-a313-ed24de494e8a"), endDateTime.minusMonths(5)),
                buildUserStatesStore(UUID.fromString("7c560aca-6a76-45cf-acba-782a40a131dc"), endDateTime.minusMonths(5), endDateTime.minusMonths(7))
        ));

        // when
        var userStatesMono = userStatesStoreService.findAll(tenantId, startDateTime, endDateTime);

        // then
        StepVerifier.create(userStatesMono)
                .consumeNextWith(userStates -> assertEquals(2, userStates.size()))
                .verifyComplete();
    }

    private UserStatesStore buildUserStatesStore(UUID tenantId, OffsetDateTime... requestStartDateTime) {
        final var userStatesStore = new UserStatesStore();
        final var offersSet = Arrays.stream(requestStartDateTime)
                .map(startDateTime -> OfferDataStateDetails.builder().requestDateTime(startDateTime).build())
                .collect(toSet());
        userStatesStore.setOfferDateStateDetailsSet(offersSet);
        userStatesStore.setTenantId(tenantId.toString());

        return userStatesStore;
    }

    private UserStatesStore buildUserStatesStore(String userUuid) {
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);
        userStatesStore.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder().state(Status.SUCCESS).build());

        return userStatesStore;
    }

    private UserStatesStore buildUserStates(String userUuid, OffsetDateTime responseDateTime, OffsetDateTime responseFromDataSolution) {
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);
        userStatesStore.setTransactionalDataStateDetails(buildFtsState(responseDateTime, responseFromDataSolution));

        return userStatesStore;
    }

    private TransactionalDataStateDetails buildFtsState(OffsetDateTime responseDateTime, OffsetDateTime responseFromDataSolution) {
        return TransactionalDataStateDetails.builder().state(Status.SUCCESS)
                .responseDateTime(responseDateTime)
                .responseFromDataSolution(responseFromDataSolution)
                .build();
    }
}
