package de.joonko.loan.userdata.domain.infrastructure.draft;

import de.joonko.loan.userdata.domain.draft.UserDataDraftProvider;
import de.joonko.loan.userdata.domain.model.UserData;
import de.joonko.loan.userdata.infrastructure.draft.model.UserDraftInformationStore;
import de.joonko.loan.userdata.infrastructure.draft.model.UserEmploymentStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("integration")
class UserDataDraftStorageServiceTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserDataDraftProvider userDataDraftProvider;

    @Test
    void gerErrorWhenMissingUserDraft() {
        // given
        final var USER_ID = "4259e88c-2d50-487a-9c60-2c2c91fde4f7";

        // when
        var monoUserDraft = userDataDraftProvider.get(USER_ID);

        // then
        StepVerifier.create(monoUserDraft).verifyError();
    }

    @Test
    void getUserDraft() {
        // given
        final var USER_ID = "3270b1c8-962c-4c37-83f0-f877feb99d01";
        mongoTemplate.insert(getDraftData(USER_ID));

        // when
        var monoUserDraft = userDataDraftProvider.get(USER_ID);

        // then
        StepVerifier.create(monoUserDraft).expectNextCount(1).verifyComplete();
    }

    @Test
    void saveUserDraft() {
        // given
        final var USER_ID = "22a94126-a393-45b0-a461-fe78c7ef5b90";
        final var userData = new UserData();

        // when
        var monoUserDraft = userDataDraftProvider.save(USER_ID, userData);

        // then
        assertAll(
                () -> StepVerifier.create(monoUserDraft).expectNextCount(1).verifyComplete(),
                () -> assertNotNull(mongoTemplate.findById(USER_ID, UserDraftInformationStore.class))
        );
    }

    @Test
    void removeUserEmployment() {
        // given
        mongoTemplate.insertAll(List.of(
                getDraftData("e394747f-7a1e-4fa8-b5be-e572f1087d59"),
                getDraftData("02b2fd38-8df9-4f67-8e06-b3bae42cfb69"),
                getDraftData("92e1b3ba-eec5-4b16-831a-b9cb4cffae7a")
                )
        );


        // when
        var monoUserDraft = userDataDraftProvider.removeFtsData(List.of(
                "e394747f-7a1e-4fa8-b5be-e572f1087d59",
                "02b2fd38-8df9-4f67-8e06-b3bae42cfb69"
        ));

        // then
        assertAll(
                () -> StepVerifier.create(monoUserDraft)
                        .consumeNextWith(l -> assertEquals(2, l.size())).verifyComplete(),
                () -> assertNull(mongoTemplate.findById("e394747f-7a1e-4fa8-b5be-e572f1087d59", UserDraftInformationStore.class).getUserEmployment())
        );
    }

    private UserDraftInformationStore getDraftData(String userUuid) {
        var userDraft = new UserDraftInformationStore();
        userDraft.setUserUUID(userUuid);
        userDraft.setUserEmployment(UserEmploymentStore.builder().build());

        return userDraft;
    }
}
