package de.joonko.loan.offer.domain;

import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.data.support.DataSolutionCommunicationManager;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.integrations.model.OfferUpdateType;
import de.joonko.loan.metric.OfferStatusMetric;
import de.joonko.loan.metric.kyc.KycMetric;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OfferStatusUpdateServiceTest {

    private OfferStatusUpdateService offerStatusUpdateService;

    private LoanOfferStoreService loanOfferStoreService;
    private OfferStatusMetric offerStatusMetric;
    private DataSolutionCommunicationManager dataSupportService;
    private KycMetric kycMetric;

    @BeforeEach
    void setUp() {
        loanOfferStoreService = mock(LoanOfferStoreService.class);
        offerStatusMetric = mock(OfferStatusMetric.class);
        dataSupportService = mock(DataSolutionCommunicationManager.class);
        kycMetric = mock(KycMetric.class);

        offerStatusUpdateService = new OfferStatusUpdateService(loanOfferStoreService, offerStatusMetric, dataSupportService, kycMetric);
    }

    @Test
    void updateOfferStatus() {
        // given
        final var loanOfferId = "12345";
        final var offerReq = OfferRequest.builder()
                .loanOfferId(loanOfferId)
                .loanProvider("AION")
                .build();
        when(loanOfferStoreService.updateOfferStatus(anyString(), anyString())).thenReturn(Mono.just(getUpdatedLoanOffer(loanOfferId)));

        // when
        var updatedOfferMono = offerStatusUpdateService.updateOfferStatus(offerReq, LoanApplicationStatus.PAID_OUT);

        // then
        assertAll(
                () -> StepVerifier.create(updatedOfferMono).consumeNextWith(updatedOffer -> assertAll(
                        () -> assertEquals(loanOfferId, updatedOffer.getLoanOfferId()),
                        () -> assertEquals(LoanApplicationStatus.PAID_OUT.name(), updatedOffer.getOfferStatus())
                )).verifyComplete(),
                () -> verify(loanOfferStoreService).updateOfferStatus(anyString(), anyString()),
                () -> verify(offerStatusMetric).incrementOfferStatusCounter(anyString(), anyString()),
                () -> verify(dataSupportService).updateLoanOffers(anyString(), anyString(), anyString(), eq(OfferUpdateType.LOAN_STATUS_UPDATE))
        );
    }

    @Test
    void updateKycStatus() {
        // given
        final var loanOfferId = "12345";
        final var newStatus = "SUCCESS";
        final var loanOfferStore = getUpdatedLoanOffer(loanOfferId).toBuilder().kycStatus(newStatus).build();
        when(loanOfferStoreService.updateKycStatus(loanOfferId, LoanApplicationStatus.SUCCESS.name())).thenReturn(Mono.just(loanOfferStore));

        // when
        var updatedOfferMono = offerStatusUpdateService.updateKycStatus(loanOfferStore, LoanApplicationStatus.SUCCESS);

        // then
        assertAll(
                () -> StepVerifier.create(updatedOfferMono).consumeNextWith(updatedOffer -> assertAll(
                        () -> assertEquals(loanOfferId, updatedOffer.getLoanOfferId()),
                        () -> assertEquals(LoanApplicationStatus.SUCCESS.name(), updatedOffer.getKycStatus())
                )).verifyComplete(),
                () -> verify(loanOfferStoreService).updateKycStatus(loanOfferId, newStatus),
                () -> verify(kycMetric).incrementKycCounter(newStatus, loanOfferStore.getOffer().getLoanProvider().getName(), loanOfferStore.getKycProvider()),
                () -> verify(dataSupportService).updateLoanOffers(loanOfferStore.getUserUUID(), loanOfferStore.getApplicationId(), loanOfferId, OfferUpdateType.KYC_UPDATE)
        );
    }

    private LoanOfferStore getUpdatedLoanOffer(final String loanOfferId) {
        return LoanOfferStore.builder()
                .loanOfferId(loanOfferId)
                .offer(LoanOffer.builder().loanProvider(new LoanProvider("AION")).build())
                .kycProvider(IdentificationProvider.ID_NOW)
                .offerStatus(LoanApplicationStatus.PAID_OUT.name())
                .userUUID("11402163-e071-4cf0-a293-62dadd5acfc5")
                .applicationId("uh729837h293")
                .build();
    }
}
