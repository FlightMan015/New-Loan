package de.joonko.loan.reporting.api;

import de.joonko.loan.data.support.mapper.MapstructBaseTest;
import de.joonko.loan.reporting.domain.OfferStatus;
import de.joonko.loan.user.states.OfferDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = OfferStatusMapperImpl.class)
class OfferStatusMapperTest extends MapstructBaseTest {

    @Autowired
    private OfferStatusMapper offerStatusMapper;

    @Test
    void mapFrom(@Random OfferStatus offerStatus) {
        // given
        // when
        final var getOfferStatusResp = offerStatusMapper.from(offerStatus);


        // then
        assertAll(
                () -> assertEquals(offerStatus.getUserUUID(), getOfferStatusResp.getUserUUID()),
                () -> assertEquals(offerStatus.getDistributionChannelUUID(), getOfferStatusResp.getDistributionChannelUUID()),
                () -> assertEquals(offerStatus.getBankAccountAddedAt(), getOfferStatusResp.getBankAccountAddedAt()),
                () -> assertEquals(offerStatus.getPersonalDataAddedAt(), getOfferStatusResp.getPersonalDataAddedAt()),
                () -> assertEquals(offerStatus.getPurpose(), getOfferStatusResp.getPurpose()),
                () -> assertEquals(offerStatus.getLoanAmountRequested(), getOfferStatusResp.getLoanAmountRequested()),
                () -> assertEquals(offerStatus.getLoanAmountRequestedAt(), getOfferStatusResp.getLoanAmountRequestedAt()),
                () -> assertEquals(offerStatus.getOfferProvider().getName(), getOfferStatusResp.getOfferProvider()),
                () -> assertEquals(offerStatus.getOffersReceivedAt(), getOfferStatusResp.getOffersReceivedAt()),
                () -> assertEquals(offerStatus.getOfferAcceptedAt(), getOfferStatusResp.getOfferAcceptedAt()),
                () -> assertEquals(offerStatus.getKycStatus(), getOfferStatusResp.getKycStatus()),
                () -> assertEquals(offerStatus.getKycStatusLastUpdatedAt(), getOfferStatusResp.getKycStatusLastUpdatedAt()),
                () -> assertEquals(offerStatus.getOfferStatus(), getOfferStatusResp.getOfferStatus()),
                () -> assertEquals(offerStatus.getOfferStatusLastUpdatedAt(), getOfferStatusResp.getOfferStatusLastUpdatedAt())
        );
    }

    @Test
    void mapTo(@Random UserStatesStore userStatesStore, @Random OfferDataStateDetails stateDetails) {
        // given
        // when
        final var getOfferStatus = offerStatusMapper.to(userStatesStore, stateDetails);

        // then
        assertAll(
                () -> assertEquals(userStatesStore.getUserUUID(), getOfferStatus.getUserUUID()),
                () -> assertEquals(userStatesStore.getTenantId(), getOfferStatus.getDistributionChannelUUID()),
                () -> assertEquals(userStatesStore.getTransactionalDataStateDetails().getResponseDateTime(), getOfferStatus.getBankAccountAddedAt()),
                () -> assertEquals(userStatesStore.getUserPersonalInformationStateDetails().getResponseDateTime(), getOfferStatus.getPersonalDataAddedAt()),
                () -> assertEquals(stateDetails.getPurpose(), getOfferStatus.getPurpose()),
                () -> assertEquals(stateDetails.getAmount(), getOfferStatus.getLoanAmountRequested()),
                () -> assertEquals(stateDetails.getRequestDateTime(), getOfferStatus.getLoanAmountRequestedAt())
        );
    }
}