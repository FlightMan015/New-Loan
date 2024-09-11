package de.joonko.loan.integrations.domain.integrationhandler.fts;

import de.joonko.loan.config.RetryConfigProperties;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStoreService;
import de.joonko.loan.integrations.domain.integrationhandler.fts.mapper.TransactionalDraftDataToFtsRequestMapper;
import de.joonko.loan.integrations.model.DacDataState;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.integrations.model.UserState;
import de.joonko.loan.integrations.domain.integrationhandler.fts.model.FinleapToFtsTransactionalData;
import de.joonko.loan.data.support.DataSolutionCommunicationManager;
import de.joonko.loan.user.states.Status;
import de.joonko.loan.user.states.TransactionalDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(RandomBeansExtension.class)
public class UserTransactionalDataIntegrationHandlerTest {


    private DataSolutionCommunicationManager dataSolutionCommunicationManager;
    private UserStatesStoreService userStatesStoreService;
    private DacFeignClient dacFeignClient;
    private RetryConfigProperties retryConfigProperties;
    private UserTransactionalDraftDataStoreService userTransactionalDraftDataStoreService;
    private TransactionalDraftDataToFtsRequestMapper transactionalDraftDataToFtsRequestMapper;

    private UserTransactionalDataIntegrationHandler userTransactionalDataIntegrationHandler;

    @BeforeEach
    void setUp() {
        final var handlerFilter = new UserTransactionalDataIntegrationHandlerFilter();
        dataSolutionCommunicationManager = mock(DataSolutionCommunicationManager.class);
        userStatesStoreService = mock(UserStatesStoreService.class);
        dacFeignClient = mock(DacFeignClient.class);
        retryConfigProperties = mock(RetryConfigProperties.class);
        userTransactionalDraftDataStoreService = mock(UserTransactionalDraftDataStoreService.class);
        transactionalDraftDataToFtsRequestMapper = mock(TransactionalDraftDataToFtsRequestMapper.class);


        userTransactionalDataIntegrationHandler = new UserTransactionalDataIntegrationHandler(handlerFilter, dataSolutionCommunicationManager, userStatesStoreService,
                dacFeignClient, retryConfigProperties, userTransactionalDraftDataStoreService, transactionalDraftDataToFtsRequestMapper);
    }

    @Test
    void triggerMutation_notValidFilter_returnsEmpty(@Random OfferRequest offerRequest) {
        // when
        offerRequest.setUserState(null);

        // then
        userTransactionalDataIntegrationHandler.triggerMutation(offerRequest);

        assertAll(() -> verifyNoInteractions(userStatesStoreService),
                () -> verifyNoInteractions(dataSolutionCommunicationManager)
        );
    }

    @Test
    void triggerMutation_validFilterMissingTransactions_noTransactionalDataStore(@Random OfferRequest offerRequest) {
        // when
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setTransactionalDataStateDetails(null);
        userStatesStore.setUserUUID("1");
        userStatesStore.setBonifyUserId(123L);
        offerRequest.setUserState(UserState.builder().dacDataState(DacDataState.MISSING_OR_STALE).build());
        when(userStatesStoreService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));
        doNothing().when(dataSolutionCommunicationManager).queryDataSolutionForSalaryAccount(userStatesStore.getUserUUID(), userStatesStore.getBonifyUserId());

        // then
        final var triggered = userTransactionalDataIntegrationHandler.triggerMutation(offerRequest);

        assertAll(
                () -> StepVerifier.create(triggered).expectNextCount(0).verifyComplete(),
                () -> verify(userStatesStoreService).findById(offerRequest.getUserUUID()),
                () -> verify(userStatesStoreService).save(userStatesStore),
                () -> verify(dataSolutionCommunicationManager).queryDataSolutionForSalaryAccount(userStatesStore.getUserUUID(), userStatesStore.getBonifyUserId()),
                () -> verifyNoMoreInteractions(userStatesStoreService),
                () -> verifyNoMoreInteractions(dataSolutionCommunicationManager)
        );
    }

    @Test
    void triggerMutation_validFilterWaitingForSendingToFTSClassificationRetryCountReached_savesFailedStateT(@Random OfferRequest offerRequest) {
        // when
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID("1");
        userStatesStore.setBonifyUserId(123L);
        userStatesStore.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder()
                .sentForClassificationCounter(11)
                .accountInternalId("123")
                .build());
        ArgumentCaptor<UserStatesStore> argument = ArgumentCaptor.forClass(UserStatesStore.class);


        offerRequest.setUserState(UserState.builder().dacDataState(DacDataState.MISSING_ACCOUNT_CLASSIFICATION).build());
        when(userStatesStoreService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(retryConfigProperties.getMaxRetry()).thenReturn(10);
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));

        // then
        final var triggered = userTransactionalDataIntegrationHandler.triggerMutation(offerRequest);

        assertAll(
                () -> StepVerifier.create(triggered).expectNextCount(0).verifyComplete(),
                () -> verify(userStatesStoreService, times(2)).findById(offerRequest.getUserUUID()),
                () -> verify(userStatesStoreService).save(argument.capture()),
                () -> verifyNoMoreInteractions(userStatesStoreService),
                () -> assertEquals(Status.MISSING_SALARY_ACCOUNT, argument.getValue().getTransactionalDataStateDetails().getState()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getSalaryAccountAdded()),
                () -> assertEquals(11, argument.getValue().getTransactionalDataStateDetails().getSentForClassificationCounter()),
                () -> verifyNoInteractions(dataSolutionCommunicationManager, dacFeignClient, userTransactionalDraftDataStoreService, transactionalDraftDataToFtsRequestMapper)
        );
    }

    @Test
    void triggerMutation_validFilterWaitingForSendingToFTSClassificationRetryCountReached_sendsRequestToFtsGetsException(@Random OfferRequest offerRequest) {
        // when
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(offerRequest.getUserUUID());
        userStatesStore.setBonifyUserId(123L);
        userStatesStore.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder()
                .sentForClassificationCounter(null)
                .state(Status.WAITING_TO_SEND_FOR_CLASSIFICATION)
                .build());
        final var userTransactionalDraftDataStore = new UserTransactionalDraftDataStore();
        final var finleapToFtsTransactionalData = new FinleapToFtsTransactionalData();
        finleapToFtsTransactionalData.setUserUUID(offerRequest.getUserUUID());
        ArgumentCaptor<UserStatesStore> argument = ArgumentCaptor.forClass(UserStatesStore.class);

        offerRequest.setUserState(UserState.builder().dacDataState(DacDataState.MISSING_ACCOUNT_CLASSIFICATION).build());
        when(userStatesStoreService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(retryConfigProperties.getMaxRetry()).thenReturn(10);
        when(userTransactionalDraftDataStoreService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userTransactionalDraftDataStore));
        when(transactionalDraftDataToFtsRequestMapper.map(userTransactionalDraftDataStore)).thenReturn(finleapToFtsTransactionalData);
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));
        doThrow(new RuntimeException("Exception")).when(dacFeignClient).finleapToFts(any(FinleapToFtsTransactionalData.class));

        // then
        final var triggered = userTransactionalDataIntegrationHandler.triggerMutation(offerRequest);

        assertAll(
                () -> StepVerifier.create(triggered).expectNextCount(0).verifyComplete(),
                () -> verify(userStatesStoreService, times(2)).findById(offerRequest.getUserUUID()),
                () -> verify(userStatesStoreService).save(argument.capture()),
                () -> verify(userTransactionalDraftDataStoreService).findById(userStatesStore.getUserUUID()),
                () -> verify(transactionalDraftDataToFtsRequestMapper).map(userTransactionalDraftDataStore),
                () -> verify(dacFeignClient).finleapToFts(finleapToFtsTransactionalData),
                () -> assertEquals(Status.WAITING_TO_SEND_FOR_CLASSIFICATION, argument.getValue().getTransactionalDataStateDetails().getState()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getSalaryAccountAdded()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getResponseDateTime()),
                () -> assertEquals(1, argument.getValue().getTransactionalDataStateDetails().getSentForClassificationCounter())
        );
    }


    @Test
    void triggerMutation_validFilterWaitingForSendingToFTSClassificationRetryCountReached_sendsRequestToFtsSuccessCase(@Random OfferRequest offerRequest) {
        // when
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(offerRequest.getUserUUID());
        userStatesStore.setBonifyUserId(123L);
        userStatesStore.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder()
                .sentForClassificationCounter(null)
                .build());
        final var userTransactionalDraftDataStore = new UserTransactionalDraftDataStore();
        final var finleapToFtsTransactionalData = new FinleapToFtsTransactionalData();
        finleapToFtsTransactionalData.setUserUUID(offerRequest.getUserUUID());
        ArgumentCaptor<UserStatesStore> argument = ArgumentCaptor.forClass(UserStatesStore.class);

        offerRequest.setUserState(UserState.builder().dacDataState(DacDataState.MISSING_ACCOUNT_CLASSIFICATION).build());
        when(userStatesStoreService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(retryConfigProperties.getMaxRetry()).thenReturn(10);
        when(userTransactionalDraftDataStoreService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userTransactionalDraftDataStore));
        when(transactionalDraftDataToFtsRequestMapper.map(userTransactionalDraftDataStore)).thenReturn(finleapToFtsTransactionalData);
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));
        doNothing().when(dacFeignClient).finleapToFts(any(FinleapToFtsTransactionalData.class));

        // then
        final var triggered = userTransactionalDataIntegrationHandler.triggerMutation(offerRequest);

        assertAll(
                () -> StepVerifier.create(triggered).expectNextCount(0).verifyComplete(),
                () -> verify(userStatesStoreService, times(2)).findById(offerRequest.getUserUUID()),
                () -> verify(userStatesStoreService).save(argument.capture()),
                () -> verify(userTransactionalDraftDataStoreService).findById(userStatesStore.getUserUUID()),
                () -> verify(transactionalDraftDataToFtsRequestMapper).map(userTransactionalDraftDataStore),
                () -> verify(dacFeignClient).finleapToFts(finleapToFtsTransactionalData),
                () -> assertEquals(Status.SENT_FOR_CLASSIFICATION, argument.getValue().getTransactionalDataStateDetails().getState()),
                () -> assertNotNull(argument.getValue().getTransactionalDataStateDetails().getSentForClassification()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getSentForClassificationCounter())
        );
    }
}
