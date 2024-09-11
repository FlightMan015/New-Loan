package de.joonko.loan.data.support;

import de.joonko.loan.avro.dto.loan_demand.LoanDemandMessage;
import de.joonko.loan.avro.dto.loan_offers.LoanOffersMessage;
import de.joonko.loan.data.support.mapper.LoanDemandMapper;
import de.joonko.loan.data.support.mapper.LoanOfferMapper;
import de.joonko.loan.db.repositories.LoanOfferStoreRepository;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.integrations.model.OfferUpdateType;
import de.joonko.loan.offer.api.LoanDemandFixtures;
import de.joonko.loan.offer.testdata.LoanDemandTestData;

import de.joonko.loan.user.states.UserStatesStoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSolutionCommunicationManagerTest {

    public static final String USER_UUID = "user-uuid";
    @Mock
    private DataSupportGateway dataSupportGateway;
    @Mock
    private LoanDemandMapper loanDemandMapper;
    @Mock
    private LoanOfferMapper loanOfferMapper;
    @Mock
    private LoanOfferStoreRepository loanOfferStoreRepository;
    @Mock
    private UserStatesStoreService userStatesStoreService;
    @Mock
    private DataSolutionPropertiesConfig dataSolutionPropertiesConfig;

    @Captor
    ArgumentCaptor<LoanOffersMessage> messageCaptor;

    @InjectMocks
    private DataSolutionCommunicationManagerImpl dataSolutionCommunicationManager;

    @Test
    @DisplayName("Should send loan demand kafka message ")
    void positiveLoanDemand() {
        when(loanDemandMapper.mapLoanDemand(anyLong(), any())).thenReturn(Mockito.mock(LoanDemandMessage.class));

        dataSolutionCommunicationManager.sendLoanDemandRequest(123L, LoanDemandFixtures.getLoanDemandRequest());
        verify(dataSupportGateway, timeout(2000)).sendToLoanDemandTopic(any(LoanDemandMessage.class), anyString());
    }

    @Test
    void sendLoanOffer() {
        // given
        final var userId = 87356L;
        final var loanOffer = LoanOfferStore.builder()
                .userUUID("85b36cf1-4cf4-4db7-be78-37fcb207a5dd")
                .applicationId("fh2398f8h239g")
                .build();
        when(loanOfferMapper.mapLoanOffer(userId, loanOffer.getUserUUID(), loanOffer.getApplicationId(), List.of(loanOffer), OfferUpdateType.STALE_OFFERS_NOTIFICATION)).thenReturn(mock(LoanOffersMessage.class));

        // when
        var loanOfferMessage = dataSolutionCommunicationManager.sendLoanOffer(userId, loanOffer, OfferUpdateType.STALE_OFFERS_NOTIFICATION);

        // then
        assertAll(
                () -> StepVerifier.create(loanOfferMessage).expectNextCount(1).verifyComplete(),
                () -> verify(dataSupportGateway).sendToLoanOffersTopic(any(LoanOffersMessage.class), eq(loanOffer.getApplicationId()))
        );
    }

    @Test
    @DisplayName("Should send loan offers kafka message ")
    void positiveLoanOffers() {
        // given
        final var offerRequest = OfferRequest.builder()
                .userUUID(USER_UUID)
                .bonifyUserId(123L)
                .requestedAmount(3500)
                .build();
        when(loanOfferMapper.mapLoanOffer(any(OfferRequest.class), anyString(), any(), eq(0), eq(0))).thenReturn(mock(LoanOffersMessage.class));
        when(dataSolutionPropertiesConfig.getDelayLoanOffer()).thenReturn(0);

        // when
        dataSolutionCommunicationManager.sendLoanOffers(offerRequest, "application-id", Set.of(mock(LoanOfferStore.class)), 0, 0);

        // then
        verify(dataSupportGateway, timeout(2000)).sendToLoanOffersTopic(any(LoanOffersMessage.class), eq("application-id"));
    }

    @Test
    @DisplayName("Should send empty loan offers kafka message")
    void emptyLoanOffers() {
        // given
        final var offerRequest = OfferRequest.builder()
                .userUUID(USER_UUID)
                .bonifyUserId(123L)
                .isRequestedBonifyLoans(true)
                .requestedAmount(5500)
                .build();
        when(loanOfferMapper.mapLoanOffer(any(OfferRequest.class), anyString(), any(), eq(0), eq(0))).thenReturn(mock(LoanOffersMessage.class));
        when(dataSolutionPropertiesConfig.getDelayLoanOffer()).thenReturn(0);

        // when
        dataSolutionCommunicationManager.sendLoanOffers(offerRequest, "application-id", Collections.emptySet(), 0, 0);

        // then
        verify(dataSupportGateway, timeout(2000)).sendToLoanOffersTopic(any(LoanOffersMessage.class), eq("application-id"));
    }

    @Test
    @DisplayName("Should send update kyc status kafka message")
    void updateKYCStatus() {
        // given
        final var loanOffersMessage = new LoanOffersMessage();
        loanOffersMessage.setUpdateType(OfferUpdateType.KYC_UPDATE.name());
        when(userStatesStoreService.findById(USER_UUID)).thenReturn(Mono.just(LoanDemandTestData.getUserStatesStoreForOffersReady(USER_UUID)));
        when(loanOfferMapper.mapLoanOffer(anyLong(), anyString(), anyString(), any(), any())).thenReturn(loanOffersMessage);
        when(loanOfferStoreRepository.getNotDeletedOffers(USER_UUID, "application-id")).thenReturn(Collections.emptyList());

        // when
        dataSolutionCommunicationManager.updateLoanOffers(USER_UUID, "application-id", "updated", OfferUpdateType.KYC_UPDATE);

        // then
        verify(dataSupportGateway, timeout(2000)).sendToLoanOffersTopic(messageCaptor.capture(), ArgumentMatchers.eq("application-id"));
        LoanOffersMessage actual = messageCaptor.getValue();
        assertThat(actual.getUpdatedOfferId()).isEqualTo("updated");
        assertThat(actual.getUpdateType()).isEqualTo("KYC_UPDATE");
    }

    @Test
    @DisplayName("Should send update offer status kafka message")
    void updateOfferStatus() {
        // given
        final var loanOffersMessage = new LoanOffersMessage();
        loanOffersMessage.setUpdateType(OfferUpdateType.LOAN_STATUS_UPDATE.name());
        when(userStatesStoreService.findById(USER_UUID)).thenReturn(Mono.just(LoanDemandTestData.getUserStatesStoreForOffersReady(USER_UUID)));
        when(loanOfferMapper.mapLoanOffer(anyLong(), anyString(), anyString(), any(), any())).thenReturn(loanOffersMessage);
        when(loanOfferStoreRepository.getNotDeletedOffers(USER_UUID, "application-id")).thenReturn(Collections.emptyList());

        // when
        dataSolutionCommunicationManager.updateLoanOffers(USER_UUID, "application-id", "updated", OfferUpdateType.LOAN_STATUS_UPDATE);

        // then
        verify(dataSupportGateway, timeout(2000)).sendToLoanOffersTopic(messageCaptor.capture(), eq("application-id"));
        LoanOffersMessage actual = messageCaptor.getValue();
        assertThat(actual.getUpdatedOfferId()).isEqualTo("updated");
        assertThat(actual.getUpdateType()).isEqualTo("LOAN_STATUS_UPDATE");
    }
}
