package de.joonko.loan.offer.testdata;

import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStore;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.user.service.UserAdditionalInformationStore;
import de.joonko.loan.user.service.UserPersonalInformationStore;
import de.joonko.loan.user.states.TransactionalDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.OffsetDateTime;

public class StorageTestData {

    public static UserPersonalInformationStore getUserPersonalInformationStoreTestData(String userUuid) {
        UserPersonalInformationStore userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setUserUUID(userUuid);
        userPersonalInformationStore.setAddressCity("Berlin");
        return userPersonalInformationStore;
    }

    public static UserAdditionalInformationStore getUserAdditionalInformationStoreTestData(String userUuid) {
        UserAdditionalInformationStore userAdditionalInformationStore = new UserAdditionalInformationStore();
        userAdditionalInformationStore.setUserUUID(userUuid);

        return userAdditionalInformationStore;
    }

    public static UserTransactionalDataStore getUserTransactionalDataStoreTestData(String userUuid) {
        UserTransactionalDataStore userTransactionalDataStore = new UserTransactionalDataStore();
        userTransactionalDataStore.setUserUUID(userUuid);

        return userTransactionalDataStore;
    }
    public static UserTransactionalDraftDataStore getUserTransactionalDraftDataStoreTestData(String userUuid) {
        UserTransactionalDraftDataStore userTransactionalDataStore = new UserTransactionalDraftDataStore();
        userTransactionalDataStore.setUserUUID(userUuid);

        return userTransactionalDataStore;
    }

    public static UserStatesStore getUserStatesStoreTestData(String userUuid) {
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);

        return userStatesStore;
    }

    public static UserStatesStore getUserStateStoreWithTransactionalData(String userUuid) {
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);
        userStatesStore.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder()
                .requestFromDataSolution(OffsetDateTime.now())
                .build());

        return userStatesStore;
    }

    public static LoanOfferStore getloanOfferStoreTestData(String userUuid, LoanOffer loanOffer) {
        return LoanOfferStore.builder()
                .userUUID(userUuid)
                .offer(loanOffer)
                .build();
    }

    public static Query getQueryFindByUserUuid(String userUuid) {
        return new Query(Criteria.where("userUUID").is(userUuid));
    }
}
