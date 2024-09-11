package de.joonko.loan.offer.domain;

import de.joonko.loan.acceptoffer.api.OfferRequestMapper;
import de.joonko.loan.acceptoffer.api.OfferRequestMapperImpl;
import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.acceptoffer.domain.LoanApplicationStatusService;
import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.avro.dto.dls_reports_data.DigitalLoansReportsDataTopic;
import de.joonko.loan.avro.dto.dls_reports_data.Offer;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class OffersStatusSyncingServiceTest {
    private OffersStatusSyncingService offersStatusSyncingService;

    private LoanApplicationStatusService applicationStatusService;
    private LoanOfferStoreService offerStoreService;
    private OfferRequestMapper mapper;
    private OfferStatusUpdateService offerStatusUpdateService;

    @BeforeEach
    void setUp() {
        applicationStatusService = mock(LoanApplicationStatusService.class);
        offerStoreService = mock(LoanOfferStoreService.class);
        mapper = new OfferRequestMapperImpl();
        offerStatusUpdateService = mock(OfferStatusUpdateService.class);
        offersStatusSyncingService = new OffersStatusSyncingService(applicationStatusService, offerStoreService, mapper, offerStatusUpdateService);
    }

    @Test
    void syncNoneOffersStatusWhenEmptySet() {
        // given

        // when
        var synced = offersStatusSyncingService.sync(Set.of());

        // then
        assertAll(
                () -> StepVerifier.create(synced).expectNextCount(0).verifyComplete(),
                () -> verifyNoInteractions(offerStoreService, offerStatusUpdateService)
        );
    }

    @Test
    void syncNoneOffersStatusWhenNotFoundOnDb() {
        // given
        var banks = Set.of(Bank.SANTANDER);
        when(offerStoreService.findByOfferStatusAndLoanProvider(eq(banks), any())).thenReturn(Flux.empty());

        // when
        var synced = offersStatusSyncingService.sync(banks);

        // then
        assertAll(
                () -> StepVerifier.create(synced).expectNextCount(0).verifyComplete(),
                () -> verifyNoInteractions(applicationStatusService, offerStatusUpdateService)
        );
    }

    @Test
    void syncNoneOffersStatusWhenPassedNumberOfDays() {
        // given
        var banks = Set.of(Bank.SANTANDER);
        var offers = List.of(
                LoanOfferStore.builder().statusLastUpdateDate(OffsetDateTime.now().minusDays(35)).build(),
                LoanOfferStore.builder().statusLastUpdateDate(OffsetDateTime.now().minusDays(50)).build()
        );
        when(offerStoreService.findByOfferStatusAndLoanProvider(eq(banks), any())).thenReturn(Flux.fromIterable(offers));

        // when
        var synced = offersStatusSyncingService.sync(banks);

        // then
        assertAll(
                () -> StepVerifier.create(synced).expectNextCount(0).verifyComplete(),
                () -> verifyNoInteractions(applicationStatusService, offerStatusUpdateService)
        );
    }

    @Test
    void syncAllOffersStatus() {
        // given
        var banks = Set.of(Bank.SANTANDER, Bank.SWK_BANK, Bank.CONSORS);
        var offers = getLoanOffers();
        when(offerStoreService.findByOfferStatusAndLoanProvider(eq(banks), any())).thenReturn(Flux.fromIterable(offers));
        when(applicationStatusService.getStatus(any(OfferRequest.class))).thenReturn(
                Mono.just(LoanApplicationStatus.PAID_OUT),
                Mono.just(LoanApplicationStatus.REJECTED),
                Mono.just(LoanApplicationStatus.APPROVED));
        when(offerStatusUpdateService.updateOfferStatus(any(OfferRequest.class), any(LoanApplicationStatus.class))).thenReturn(
                Mono.just(offers.get(0)),
                Mono.just(offers.get(1)),
                Mono.just(offers.get(2)));

        // when
        var synced = offersStatusSyncingService.sync(banks);

        // then
        assertAll(
                () -> StepVerifier.create(synced).expectNextCount(0).verifyComplete(),
                () -> verify(applicationStatusService, times(3)).getStatus(any(OfferRequest.class)),
                () -> verify(offerStatusUpdateService, times(3)).updateOfferStatus(any(OfferRequest.class), any(LoanApplicationStatus.class))
        );
    }

    private List<LoanOfferStore> getLoanOffers() {
        return List.of(
                LoanOfferStore.builder()
                        .loanOfferId("145")
                        .userUUID("abc-123-edf")
                        .applicationId("pqr-asd-ads")
                        .offerStatus(LoanApplicationStatus.ESIGN_PENDING.name())
                        .statusLastUpdateDate(OffsetDateTime.now().minusDays(15))
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build(),
                LoanOfferStore.builder()
                        .loanOfferId("146")
                        .userUUID("abc-123-edf")
                        .applicationId("sfsd-asd-ads")
                        .offerStatus(LoanApplicationStatus.OFFER_ACCEPTED.name())
                        .statusLastUpdateDate(OffsetDateTime.now().minusDays(10))
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build(),
                LoanOfferStore.builder()
                        .loanOfferId("147")
                        .userUUID("xyz-123-edf")
                        .applicationId("rtyt-asd-ads")
                        .offerStatus(LoanApplicationStatus.REVIEW_PENDING.name())
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider("SWK_BANK")).build()).build()
        );
    }

    @Test
    void syncFromDS() {
        final var topic = DigitalLoansReportsDataTopic.newBuilder()
                .setOffers(List.of(
                        Offer.newBuilder()
                                .setDuration(6)
                                .setAmount(5000)
                                .setEffInterestRate(1.2)
                                .setPartnerId("AION")
                                .setStatus("SUCCESS")
                                .setReferenceId("3")
                                .setTimestamp(34567)
                                .build(),
                        Offer.newBuilder()
                                .setDuration(12)
                                .setAmount(5000)
                                .setEffInterestRate(1.2)
                                .setPartnerId("SWK_BANK")
                                .setReferenceId("1")
                                .setStatus("erledigt (zurückgezahlt)")
                                .setTimestamp(34567)
                                .build(),
                        Offer.newBuilder()
                                .setDuration(12)
                                .setAmount(5000)
                                .setEffInterestRate(1.2)
                                .setPartnerId("ABC_BANK")
                                .setReferenceId("1")
                                .setStatus("ausgezahlt")
                                .setTimestamp(34567)
                                .build(),
                        Offer.newBuilder()
                                .setDuration(12)
                                .setAmount(5000)
                                .setEffInterestRate(1.2)
                                .setPartnerId("SANTANDER")
                                .setStatus("PAID_OUT")
                                .setTimestamp(34567)
                                .setReferenceId("4")
                                .build(),
                        Offer.newBuilder()
                                .setDuration(12)
                                .setAmount(5000)
                                .setEffInterestRate(1.2)
                                .setPartnerId("CONSORS")
                                .setStatus("ANN")
                                .setReferenceId("2")
                                .setTimestamp(34567)
                                .build()
                ))
                .build();

        final var swkOffer = LoanOfferStore.builder()
                .loanProviderReferenceNumber("1")
                .build();

        final var consorsOffer = LoanOfferStore.builder()
                .loanProviderReferenceNumber("2")
                .build();

        when(offerStoreService.getSingleLoanOffersByLoanProviderReferenceNumberAndOfferDurationAndLoanProvider("1", 12, "SWK_BANK")).thenReturn(Mono.just(swkOffer));
        when(offerStoreService.getSingleLoanOffersByLoanProviderReferenceNumberAndOfferDurationAndLoanProvider("2", 12, "Consors Finanz")).thenReturn(Mono.just(consorsOffer));
        when(offerStatusUpdateService.updateOfferStatus(any(OfferRequest.class), eq(LoanApplicationStatus.PAID_OUT))).thenReturn(Mono.just(swkOffer));
        when(offerStatusUpdateService.updateOfferStatus(any(OfferRequest.class), eq(LoanApplicationStatus.CANCELED))).thenReturn(Mono.just(consorsOffer));

        Mono<Void> synced = offersStatusSyncingService.syncFromDS(topic);

        assertAll(
                () -> StepVerifier.create(synced).expectNextCount(0).verifyComplete(),
                () -> verify(offerStoreService).getSingleLoanOffersByLoanProviderReferenceNumberAndOfferDurationAndLoanProvider("1", 12, "SWK_BANK"),
                () -> verify(offerStoreService).getSingleLoanOffersByLoanProviderReferenceNumberAndOfferDurationAndLoanProvider("2", 12, "Consors Finanz"),
                () -> verify(offerStatusUpdateService).updateOfferStatus(any(OfferRequest.class), eq(LoanApplicationStatus.PAID_OUT)),
                () -> verify(offerStatusUpdateService).updateOfferStatus(any(OfferRequest.class), eq(LoanApplicationStatus.CANCELED))
        );


    }

}
