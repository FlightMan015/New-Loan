package de.joonko.loan.bankaccount.testdata;

import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStore;
import de.joonko.loan.user.states.Status;
import de.joonko.loan.user.states.TransactionalDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;

public class BankAccountTestData {
    public UserStatesStore getUserStatesStoreWithoutTransactionalState(String userUUid) {
        final var userStates = new UserStatesStore();
        userStates.setUserUUID(userUUid);
        return userStates;
    }

    public UserStatesStore getUserStatesStore(String userUUid) {
        final var userStates = new UserStatesStore();
        userStates.setUserUUID(userUUid);
        userStates.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder()
                .state(Status.SUCCESS)
                .build());
        return userStates;
    }

    public UserTransactionalDataStore getUserTransactionalDataStore(String userUuid) {
        final var transactionalData = new UserTransactionalDataStore();
        transactionalData.setUserUUID(userUuid);

        return transactionalData;
    }

    public UserTransactionalDraftDataStore getUserTransactionalDraftDataStore(String userUuid) {
        final var transactionalData = new UserTransactionalDraftDataStore();
        transactionalData.setUserUUID(userUuid);

        return transactionalData;
    }
}
