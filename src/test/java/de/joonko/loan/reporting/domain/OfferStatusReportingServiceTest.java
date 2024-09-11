package de.joonko.loan.reporting.domain;

import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.reporting.api.OfferStatusMapper;
import de.joonko.loan.reporting.api.OfferStatusMapperImpl;
import de.joonko.loan.user.states.OfferDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OfferStatusReportingServiceTest {

    private OfferStatusReportingService offerStatusReportingService;

    private UserStatesStoreService userStatesStoreService;
    private LoanOfferStoreService loanOfferStoreService;
    private OfferStatusMapper offerStatusMapper;

    @BeforeEach
    void setUp() {
        userStatesStoreService = mock(UserStatesStoreService.class);
        loanOfferStoreService = mock(LoanOfferStoreService.class);
        offerStatusMapper = new OfferStatusMapperImpl();

        offerStatusReportingService = new OfferStatusReportingService(userStatesStoreService, loanOfferStoreService, offerStatusMapper);
    }

    @Test
    void getEmptyListWhenNoDataFoundInUserStatesStore() {
        // given
        final var tenantId = UUID.randomUUID();
        final var endDateTime = OffsetDateTime.now();
        final var startDateTime = endDateTime.minusMonths(3);
        when(userStatesStoreService.findAll(tenantId, startDateTime, endDateTime)).thenReturn(Mono.just(List.of()));

        // when
        final var offersStatusMono = offerStatusReportingService.get(tenantId, startDateTime, endDateTime);

        // then
        StepVerifier.create(offersStatusMono)
                .consumeNextWith(offersStatus -> assertTrue(offersStatus.isEmpty()))
                .verifyComplete();
    }

    @Test
    void getWhenNoDataFoundInLoanOfferStore() {
        // given
        final var tenantId = UUID.randomUUID();
        final var endDateTime = OffsetDateTime.now();
        final var startDateTime = endDateTime.minusMonths(3);
        when(userStatesStoreService.findAll(tenantId, startDateTime, endDateTime)).thenReturn(Mono.just(List.of(
                buildUserStatesStore(tenantId, endDateTime.minusMonths(2), endDateTime.minusMonths(1)),
                buildUserStatesStore(tenantId, endDateTime.minusDays(5))
        )));
        when(loanOfferStoreService.getLatestUpdatedOffersGroupedByApplication(anySet())).thenReturn(Mono.just(Map.of()));

        // when
        final var offersStatusMono = offerStatusReportingService.get(tenantId, startDateTime, endDateTime);

        // then
        StepVerifier.create(offersStatusMono)
                .consumeNextWith(offersStatus -> assertEquals(3, offersStatus.size()))
                .verifyComplete();
    }

    @Test
    void getOfferStatuses() {
        // given
        final var tenantId = UUID.randomUUID();
        final var endDateTime = OffsetDateTime.now();
        final var startDateTime = endDateTime.minusMonths(3);
        when(userStatesStoreService.findAll(tenantId, startDateTime, endDateTime)).thenReturn(Mono.just(List.of(
                buildUserStatesStore(tenantId, endDateTime.minusMonths(2), endDateTime.minusMonths(1)),
                buildUserStatesStore(tenantId, endDateTime.minusDays(5))
        )));
        when(loanOfferStoreService.getLatestUpdatedOffersGroupedByApplication(anySet())).thenReturn(Mono.just(Map.of(
                tenantId.toString() + endDateTime.minusMonths(2), LoanOfferStore.builder().offerStatus("PENDING")
                        .insertTS(LocalDateTime.now())
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build(),
                tenantId.toString() + endDateTime.minusMonths(1), LoanOfferStore.builder().offerStatus("REJECTED")
                        .insertTS(LocalDateTime.now())
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider("CONSORS")).build()).build(),
                tenantId.toString() + endDateTime.minusDays(5), LoanOfferStore.builder().offerStatus("ACCEPTED")
                        .insertTS(LocalDateTime.now())
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider("SWK")).build()).build()
        )));

        // when
        final var offersStatusMono = offerStatusReportingService.get(tenantId, startDateTime, endDateTime);

        // then
        StepVerifier.create(offersStatusMono)
                .consumeNextWith(offersStatus -> assertAll(
                        () -> assertEquals(3, offersStatus.size()),
                        () -> assertTrue(offersStatus.stream().map(OfferStatus::getOfferStatus).noneMatch(Objects::isNull))
                ))
                .verifyComplete();
    }

    private UserStatesStore buildUserStatesStore(UUID tenantId, OffsetDateTime... requestStartDateTime) {
        final var userStatesStore = new UserStatesStore();
        final var offersSet = Arrays.stream(requestStartDateTime)
                .map(startDateTime -> OfferDataStateDetails.builder()
                        .applicationId(tenantId.toString() + startDateTime)
                        .requestDateTime(startDateTime).build())
                .collect(toSet());
        userStatesStore.setOfferDateStateDetailsSet(offersSet);
        userStatesStore.setTenantId(tenantId.toString());

        return userStatesStore;
    }
}
