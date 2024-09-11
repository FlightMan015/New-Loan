package de.joonko.loan.reporting.api.testdata;

import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.KycStatus;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.user.states.OfferDataStateDetails;
import de.joonko.loan.user.states.StateDetails;
import de.joonko.loan.user.states.TransactionalDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import static java.util.stream.Collectors.toSet;

public class OfferStatusTestData {

    public static UserStatesStore buildUserStatesStore(UUID tenantId, OffsetDateTime... requestStartDateTime) {
        final var userStatesStore = new UserStatesStore();
        final var offersSet = Arrays.stream(requestStartDateTime)
                .map(startDateTime -> OfferDataStateDetails.builder()
                        .applicationId(tenantId.toString() + startDateTime)
                        .requestDateTime(startDateTime)
                        .amount(new Random().nextInt(10000) + 1000)
                        .purpose(RandomStringUtils.randomAlphabetic(5))
                        .build())
                .collect(toSet());
        userStatesStore.setOfferDateStateDetailsSet(offersSet);
        userStatesStore.setTenantId(tenantId.toString());
        userStatesStore.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder().responseDateTime(OffsetDateTime.now().minusDays(5)).build());
        userStatesStore.setUserPersonalInformationStateDetails(StateDetails.builder().responseDateTime(OffsetDateTime.now().minusMinutes(20)).build());

        return userStatesStore;
    }

    public static LoanOfferStore buildLoanOfferStore(String applicationId, OffsetDateTime statusLastUpdate, OffsetDateTime kycStatusLastUpdate,
                                                     OffsetDateTime acceptedDate, LocalDateTime insertTs) {
        return LoanOfferStore.builder()
                .applicationId(applicationId)
                .offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build())
                .insertTS(insertTs)
                .acceptedDate(acceptedDate)
                .kycStatus(KycStatus.PENDING.toString())
                .kycStatusLastUpdateDate(kycStatusLastUpdate)
                .offerStatus("SUCCESS")
                .statusLastUpdateDate(statusLastUpdate)
                .build();
    }
}
