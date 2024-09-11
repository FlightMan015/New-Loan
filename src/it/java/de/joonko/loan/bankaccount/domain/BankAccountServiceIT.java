package de.joonko.loan.bankaccount.domain;

import de.joonko.loan.bankaccount.testdata.BankAccountTestData;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStore;
import de.joonko.loan.user.states.UserStatesStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("integration")
@ExtendWith({MockitoExtension.class})
@SpringBootTest
class BankAccountServiceIT {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BankAccountService bankAccountService;

    private BankAccountTestData testData;

    @BeforeEach
    void setUp() {
        testData = new BankAccountTestData();
    }

    @Test
    void getErrorWhenUserStatesMissing() {
        // given
        final var userUuid = "93663700-9404-45b3-b24c-be3bf8c683d5";

        // when
        var deletedAccount = bankAccountService.delete(userUuid);

        // then
        StepVerifier.create(deletedAccount).verifyErrorMessage("Missing user transactional data state with userUuid: " + userUuid);
    }

    @Test
    void getErrorWhenUserTransactionalStateMissing() {
        // given
        final var userUuid = "4d9ccd79-a85f-40e8-8ddf-d873bc3fcac4";
        mongoTemplate.insert(testData.getUserStatesStoreWithoutTransactionalState(userUuid));

        // when
        var deletedAccount = bankAccountService.delete(userUuid);

        // then
        StepVerifier.create(deletedAccount).verifyErrorMessage("Missing user transactional data state with userUuid: " + userUuid);
    }

    @Test
    void invalidateState() {
        // given
        final var userUuid = "55dddcfe-d352-4da4-bb12-a6febf3f7e04";
        mongoTemplate.insert(testData.getUserStatesStore(userUuid));
        mongoTemplate.insert(testData.getUserTransactionalDataStore(userUuid));
        mongoTemplate.insert(testData.getUserTransactionalDraftDataStore(userUuid));

        // when
        var deletedAccount = bankAccountService.delete(userUuid);

        // then
        assertAll(
                () -> StepVerifier.create(deletedAccount).expectNextCount(0).verifyComplete(),
                () -> assertNull(mongoTemplate.findById(userUuid, UserTransactionalDataStore.class)),
                () -> assertNull(mongoTemplate.findById(userUuid, UserTransactionalDraftDataStore.class)),
                () -> assertFalse(mongoTemplate.findById(userUuid, UserStatesStore.class).getTransactionalDataStateDetails().getUserVerifiedByBankAccount()),
                () -> assertNull(mongoTemplate.findById(userUuid, UserStatesStore.class).getTransactionalDataStateDetails().getState())
        );
    }


}
