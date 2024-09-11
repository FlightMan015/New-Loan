package de.joonko.loan.user.domain.abandoned;

import de.joonko.loan.config.AbandonedUsersConfig;
import de.joonko.loan.data.support.DataSolutionCommunicationManager;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.integrations.model.OfferUpdateType;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

class AbandonedUsersNotifierServiceTest {

    private AbandonedUsersNotifierService abandonedUsersNotifierService;

    private LoanOfferStoreService loanOfferStoreService;
    private DataSolutionCommunicationManager dataSolutionCommunicationManager;
    private UserStatesStoreService userStatesStoreService;
    private AbandonedUsersConfig abandonedUsersConfig;

    @BeforeEach
    void setUp() {
        loanOfferStoreService = mock(LoanOfferStoreService.class);
        dataSolutionCommunicationManager = mock(DataSolutionCommunicationManager.class);
        userStatesStoreService = mock(UserStatesStoreService.class);
        abandonedUsersConfig = mock(AbandonedUsersConfig.class);

        abandonedUsersNotifierService = new AbandonedUsersNotifierService(loanOfferStoreService, dataSolutionCommunicationManager, userStatesStoreService, abandonedUsersConfig);
    }

    @Test
    void doNotSendNotificationsWhenEmptyAbandonedList() {
        // given
        when(abandonedUsersConfig.getDaysAgo()).thenReturn(7);
        when(loanOfferStoreService.findAnyOfferForEachAbandonedUser(7)).thenReturn(Mono.just(List.of()));

        // when
        var sentMono = abandonedUsersNotifierService.send();

        // then
        assertAll(
                () -> StepVerifier.create(sentMono).verifyComplete(),
                () -> verify(abandonedUsersConfig).getDaysAgo(),
                () -> verify(loanOfferStoreService).findAnyOfferForEachAbandonedUser(7),
                () -> verifyNoInteractions(userStatesStoreService, dataSolutionCommunicationManager)
        );
    }

    @Test
    void sendNotifications() {
        // given
        when(abandonedUsersConfig.getDaysAgo()).thenReturn(7);
        when(loanOfferStoreService.findAnyOfferForEachAbandonedUser(7)).thenReturn(Mono.just(List.of(
                LoanOfferStore.builder().userUUID("33bd4522-a450-42e4-a099-60dc82c19305").build(),
                LoanOfferStore.builder().userUUID("18a6f92b-f241-4dc7-8ec2-179493838eb9").build(),
                LoanOfferStore.builder().userUUID("d3266413-5f24-46cb-901d-c999e4051140").build(),
                LoanOfferStore.builder().userUUID("d138bf99-d498-4b8a-93ec-79513e24d4b5").build(),
                LoanOfferStore.builder().userUUID("6800b13e-5d31-41dc-b063-d0842f35b390").build()
        )));
        when(userStatesStoreService.findAllByUserUUID(anyList())).thenReturn(Mono.just(List.of(
                buildUserStatesStore("33bd4522-a450-42e4-a099-60dc82c19305", 73277L),
                buildUserStatesStore("18a6f92b-f241-4dc7-8ec2-179493838eb9", 48032L),
                buildUserStatesStore("d3266413-5f24-46cb-901d-c999e4051140", 35325L),
                buildUserStatesStore("d138bf99-d498-4b8a-93ec-79513e24d4b5", 38392L),
                buildUserStatesStore("6800b13e-5d31-41dc-b063-d0842f35b390", 98597L)
        )));
        when(dataSolutionCommunicationManager.sendLoanOffer(anyLong(), any(LoanOfferStore.class), eq(OfferUpdateType.STALE_OFFERS_NOTIFICATION))).thenReturn(Mono.empty());

        // when
        var sentMono = abandonedUsersNotifierService.send();

        // then
        assertAll(
                () -> StepVerifier.create(sentMono).verifyComplete(),
                () -> verify(abandonedUsersConfig).getDaysAgo(),
                () -> verify(loanOfferStoreService).findAnyOfferForEachAbandonedUser(7),
                () -> verify(userStatesStoreService).findAllByUserUUID(anyList()),
                () -> verify(dataSolutionCommunicationManager, times(5)).sendLoanOffer(anyLong(), any(LoanOfferStore.class), eq(OfferUpdateType.STALE_OFFERS_NOTIFICATION))
        );
    }

    private UserStatesStore buildUserStatesStore(String userUuid, Long bonifyUserId) {
        final var userStateStore = new UserStatesStore();
        userStateStore.setUserUUID(userUuid);
        userStateStore.setBonifyUserId(bonifyUserId);

        return userStateStore;
    }
}
