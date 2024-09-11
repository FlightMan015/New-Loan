package de.joonko.loan.integrations.domain.integrationhandler.fts;

import de.joonko.loan.avro.dto.dac.CustomDACData;
import de.joonko.loan.avro.dto.dac.DacAccountSnapshot;
import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountResponse;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStoreService;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStoreService;
import de.joonko.loan.integrations.domain.integrationhandler.fts.mapper.SalaryAccountResponseToTransactionalDraftDataMapper;
import de.joonko.loan.metric.OffersStateMetric;
import de.joonko.loan.user.states.Status;
import de.joonko.loan.user.states.TransactionalDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStoreMapper;
import feign.FeignException;
import feign.Response;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(RandomBeansExtension.class)
public class UserTransactionalDataIngesterImplTest {

    private UserStatesStoreService userStatesStoreService;
    private OffersStateMetric offersStateMetric;
    private UserTransactionalDataStoreMapper userTransactionalDataStoreMapper;
    private UserTransactionalDataStoreService userTransactionalDataStoreService;
    private SalaryAccountResponseToTransactionalDraftDataMapper salaryAccountResponseToTransactionalDraftDataMapper;
    private UserTransactionalDraftDataStoreService userTransactionalDraftDataStoreService;

    private UserTransactionalDataIngester userTransactionalDataIngester;


    private FeignException feignException() {
        return FeignException.errorStatus("random", Response.builder().headers(new HashMap<>()).status(500).build());
    }

    @BeforeEach
    void setUp() {
        userStatesStoreService = mock(UserStatesStoreService.class);
        offersStateMetric = mock(OffersStateMetric.class);
        userTransactionalDataStoreMapper = mock(UserTransactionalDataStoreMapper.class);
        userTransactionalDataStoreService = mock(UserTransactionalDataStoreService.class);
        salaryAccountResponseToTransactionalDraftDataMapper = mock(SalaryAccountResponseToTransactionalDraftDataMapper.class);
        userTransactionalDraftDataStoreService = mock(UserTransactionalDraftDataStoreService.class);

        userTransactionalDataIngester = new UserTransactionalDataIngesterImpl(userStatesStoreService, offersStateMetric,
                userTransactionalDataStoreMapper, userTransactionalDataStoreService, salaryAccountResponseToTransactionalDraftDataMapper, userTransactionalDraftDataStoreService);
    }

    @Test
    void handleQuerySalaryAccountResponseFromDS_whenNoAccountInternalId_updateMissingSalaryAccount(@Random QuerySalaryAccountResponse querySalaryAccountResponse) {
        // given
        final var responseFromDsDate = OffsetDateTime.now();
        final var sentForClassificationCounter = 7;
        querySalaryAccountResponse.setAccountInternalId(null);
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder()
                .responseFromDataSolution(responseFromDsDate)
                .sentForClassificationCounter(sentForClassificationCounter)
                .build());
        ArgumentCaptor<UserStatesStore> argument = ArgumentCaptor.forClass(UserStatesStore.class);

        when(userStatesStoreService.findById(querySalaryAccountResponse.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));
        // when
        Mono<Void> result = userTransactionalDataIngester.handleQuerySalaryAccountResponseFromDS(querySalaryAccountResponse);

        // then
        assertAll(
                () -> StepVerifier.create(result).expectNextCount(0).verifyComplete(),
                () -> verify(userStatesStoreService).findById(querySalaryAccountResponse.getUserUUID()),
                () -> verify(userStatesStoreService).save(argument.capture()),
                () -> assertEquals(Status.MISSING_SALARY_ACCOUNT, argument.getValue().getTransactionalDataStateDetails().getState()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getAccountInternalId()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getSalaryAccountAdded()),
                () -> assertEquals(sentForClassificationCounter, argument.getValue().getTransactionalDataStateDetails().getSentForClassificationCounter()),
                () -> assertEquals(responseFromDsDate, argument.getValue().getTransactionalDataStateDetails().getResponseFromDataSolution()),
                () -> verifyNoInteractions(offersStateMetric)
        );
    }

    @Test
    void handleQuerySalaryAccountResponseFromDS_whenNoAccountInternalIdAndNoRequestAndResponseFromDS_updateMissingSalaryAccount(@Random QuerySalaryAccountResponse querySalaryAccountResponse) {
        // given
        final var sentForClassificationCounter = 7;
        querySalaryAccountResponse.setAccountInternalId(null);
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(querySalaryAccountResponse.getUserUUID());
        userStatesStore.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder()
                .responseFromDataSolution(null)
                .requestFromDataSolution(null)
                .sentForClassificationCounter(sentForClassificationCounter)
                .build());
        ArgumentCaptor<UserStatesStore> argument = ArgumentCaptor.forClass(UserStatesStore.class);

        when(userStatesStoreService.findById(querySalaryAccountResponse.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));
        doNothing().when(offersStateMetric).addTransactionalDataFromDSTimer(querySalaryAccountResponse.getUserUUID(), null);
        // when
        Mono<Void> result = userTransactionalDataIngester.handleQuerySalaryAccountResponseFromDS(querySalaryAccountResponse);

        // then
        assertAll(
                () -> StepVerifier.create(result).expectNextCount(0).verifyComplete(),
                () -> verify(userStatesStoreService).findById(querySalaryAccountResponse.getUserUUID()),
                () -> verify(userStatesStoreService).save(argument.capture()),
                () -> verify(offersStateMetric).addTransactionalDataFromDSTimer(querySalaryAccountResponse.getUserUUID(), null),
                () -> assertEquals(Status.MISSING_SALARY_ACCOUNT, argument.getValue().getTransactionalDataStateDetails().getState()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getAccountInternalId()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getSalaryAccountAdded()),
                () -> assertEquals(sentForClassificationCounter, argument.getValue().getTransactionalDataStateDetails().getSentForClassificationCounter()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getResponseFromDataSolution()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getRequestFromDataSolution())
        );
    }

    @Test
    void handleQuerySalaryAccountResponseFromDS_whenNoAccountInternalIdAndNoResponseFromDS_updateMissingSalaryAccount(@Random QuerySalaryAccountResponse querySalaryAccountResponse) {
        // given
        final var requestFromDsDate = OffsetDateTime.now().minusMinutes(10);
        final var sentForClassificationCounter = 7;
        querySalaryAccountResponse.setAccountInternalId(null);
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(querySalaryAccountResponse.getUserUUID());
        userStatesStore.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder()
                .responseFromDataSolution(null)
                .requestFromDataSolution(requestFromDsDate)
                .sentForClassificationCounter(sentForClassificationCounter)
                .build());
        ArgumentCaptor<UserStatesStore> argument = ArgumentCaptor.forClass(UserStatesStore.class);

        when(userStatesStoreService.findById(querySalaryAccountResponse.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));
        doNothing().when(offersStateMetric).addTransactionalDataFromDSTimer(querySalaryAccountResponse.getUserUUID(), requestFromDsDate);
        // when
        Mono<Void> result = userTransactionalDataIngester.handleQuerySalaryAccountResponseFromDS(querySalaryAccountResponse);

        // then
        assertAll(
                () -> StepVerifier.create(result).expectNextCount(0).verifyComplete(),
                () -> verify(userStatesStoreService).findById(querySalaryAccountResponse.getUserUUID()),
                () -> verify(userStatesStoreService).save(argument.capture()),
                () -> verify(offersStateMetric).addTransactionalDataFromDSTimer(querySalaryAccountResponse.getUserUUID(), requestFromDsDate),
                () -> assertEquals(Status.MISSING_SALARY_ACCOUNT, argument.getValue().getTransactionalDataStateDetails().getState()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getAccountInternalId()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getSalaryAccountAdded()),
                () -> assertEquals(sentForClassificationCounter, argument.getValue().getTransactionalDataStateDetails().getSentForClassificationCounter()),
                () -> assertThat(argument.getValue().getTransactionalDataStateDetails().getResponseFromDataSolution(), greaterThan(requestFromDsDate)),
                () -> assertEquals(requestFromDsDate, argument.getValue().getTransactionalDataStateDetails().getRequestFromDataSolution())
        );
    }

    @Test
    void handleQuerySalaryAccountResponseFromDS_whenAccountInternalIdExistsAndTransactionOutdated_updateOutdatedSalaryAccount(@Random QuerySalaryAccountResponse querySalaryAccountResponse) {
        // given
        final var requestFromDsDate = OffsetDateTime.now().minusMinutes(10);
        querySalaryAccountResponse.setAccountInternalId("a");
        querySalaryAccountResponse.setTransactions(List.of());
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(querySalaryAccountResponse.getUserUUID());
        userStatesStore.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder()
                .responseFromDataSolution(null)
                .requestFromDataSolution(requestFromDsDate)
                .build());
        ArgumentCaptor<UserStatesStore> argument = ArgumentCaptor.forClass(UserStatesStore.class);

        when(userStatesStoreService.findById(querySalaryAccountResponse.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));
        doNothing().when(offersStateMetric).addTransactionalDataFromDSTimer(querySalaryAccountResponse.getUserUUID(), requestFromDsDate);
        // when
        Mono<Void> result = userTransactionalDataIngester.handleQuerySalaryAccountResponseFromDS(querySalaryAccountResponse);

        // then
        assertAll(
                () -> StepVerifier.create(result).expectNextCount(0).verifyComplete(),
                () -> verify(userStatesStoreService).findById(querySalaryAccountResponse.getUserUUID()),
                () -> verify(userStatesStoreService).save(argument.capture()),
                () -> verify(offersStateMetric).addTransactionalDataFromDSTimer(querySalaryAccountResponse.getUserUUID(), requestFromDsDate),
                () -> assertEquals(Status.OUTDATED_SALARY_ACCOUNT, argument.getValue().getTransactionalDataStateDetails().getState()),
                () -> assertEquals("a", argument.getValue().getTransactionalDataStateDetails().getAccountInternalId()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getSalaryAccountAdded()),
                () -> assertThat(argument.getValue().getTransactionalDataStateDetails().getResponseFromDataSolution(), greaterThan(requestFromDsDate)),
                () -> assertEquals(requestFromDsDate, argument.getValue().getTransactionalDataStateDetails().getRequestFromDataSolution())
        );
    }

    @Test
    void handleQuerySalaryAccountResponseFromDS_whenAccountInternalIdExistsAndTransactionNotOutdated_savesTransactionalDraftData(@Random QuerySalaryAccountResponse querySalaryAccountResponse) {
        // given
        final var requestFromDsDate = OffsetDateTime.now().minusMinutes(10);
        querySalaryAccountResponse.setAccountInternalId("a");
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(querySalaryAccountResponse.getUserUUID());
        userStatesStore.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder()
                .responseFromDataSolution(null)
                .sentForClassificationCounter(null)
                .requestFromDataSolution(requestFromDsDate)
                .build());
        final var userTransactionalDraftDataStore = new UserTransactionalDraftDataStore();
        ArgumentCaptor<UserStatesStore> argument = ArgumentCaptor.forClass(UserStatesStore.class);

        when(userStatesStoreService.findById(querySalaryAccountResponse.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(userStatesStoreService.save(any())).thenReturn(Mono.just(userStatesStore));
        doNothing().when(offersStateMetric).addTransactionalDataFromDSTimer(querySalaryAccountResponse.getUserUUID(), requestFromDsDate);
        when(salaryAccountResponseToTransactionalDraftDataMapper.map(querySalaryAccountResponse)).thenReturn(userTransactionalDraftDataStore);
        when(userTransactionalDraftDataStoreService.save(userTransactionalDraftDataStore)).thenReturn(Mono.just(userTransactionalDraftDataStore));

        // when
        Mono<Void> result = userTransactionalDataIngester.handleQuerySalaryAccountResponseFromDS(querySalaryAccountResponse);

        // then
        assertAll(
                () -> StepVerifier.create(result).expectNextCount(0).verifyComplete(),
                () -> verify(userStatesStoreService).findById(querySalaryAccountResponse.getUserUUID()),
                () -> verify(userStatesStoreService).save(argument.capture()),
                () -> verify(offersStateMetric).addTransactionalDataFromDSTimer(querySalaryAccountResponse.getUserUUID(), requestFromDsDate),
                () -> verify(salaryAccountResponseToTransactionalDraftDataMapper).map(querySalaryAccountResponse),
                () -> verify(userTransactionalDraftDataStoreService).save(userTransactionalDraftDataStore),
                () -> assertEquals(Status.WAITING_TO_SEND_FOR_CLASSIFICATION, argument.getValue().getTransactionalDataStateDetails().getState()),
                () -> assertEquals("a", argument.getValue().getTransactionalDataStateDetails().getAccountInternalId()),
                () -> assertNotNull(argument.getValue().getTransactionalDataStateDetails().getResponseFromDataSolution()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getSentForClassification()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getSentForClassificationCounter()),
                () -> assertTrue(argument.getValue().getTransactionalDataStateDetails().getUserVerifiedByBankAccount()),
                () -> assertThat(argument.getValue().getTransactionalDataStateDetails().getResponseFromDataSolution(), greaterThan(requestFromDsDate)),
                () -> assertEquals(requestFromDsDate, argument.getValue().getTransactionalDataStateDetails().getRequestFromDataSolution())
        );
    }

    @Test
    void handleDacResponse_whenNoSalaryDetected_updateMissingSalaryAccount(@Random DacAccountSnapshot dacAccountSnapshot) {
        // given
        final var responseFromDsDate = OffsetDateTime.now();
        final var sentForClassificationCounter = 7;
        dacAccountSnapshot.setCustomDACData(CustomDACData.newBuilder().setHasSalary(false).build());
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder()
                .responseFromDataSolution(responseFromDsDate)
                .sentForClassification(responseFromDsDate.plusSeconds(3))
                .sentForClassificationCounter(sentForClassificationCounter)
                .build());
        ArgumentCaptor<UserStatesStore> argument = ArgumentCaptor.forClass(UserStatesStore.class);

        when(userStatesStoreService.findById(dacAccountSnapshot.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        doNothing().when(offersStateMetric).addTransactionalDataFromDACTimer(dacAccountSnapshot.getUserUUID(), userStatesStore.getTransactionalDataStateDetails().getSentForClassification());
        when(userStatesStoreService.save(any(UserStatesStore.class))).thenReturn(Mono.just(userStatesStore));
        // when
        Mono<Void> result = userTransactionalDataIngester.handleDacResponse(dacAccountSnapshot);

        // then
        assertAll(
                () -> StepVerifier.create(result).expectNextCount(0).verifyComplete(),
                () -> verify(userStatesStoreService).findById(dacAccountSnapshot.getUserUUID()),
                () -> verify(userStatesStoreService).save(argument.capture()),
                () -> verify(offersStateMetric).addTransactionalDataFromDACTimer(dacAccountSnapshot.getUserUUID(), userStatesStore.getTransactionalDataStateDetails().getSentForClassification()),
                () -> assertEquals(Status.MISSING_SALARY_ACCOUNT, argument.getValue().getTransactionalDataStateDetails().getState()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getAccountInternalId()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getSalaryAccountAdded()),
                () -> assertEquals(sentForClassificationCounter, argument.getValue().getTransactionalDataStateDetails().getSentForClassificationCounter()),
                () -> assertEquals(responseFromDsDate, argument.getValue().getTransactionalDataStateDetails().getResponseFromDataSolution()),
                () -> verifyNoInteractions(userTransactionalDataStoreMapper),
                () -> verifyNoMoreInteractions(offersStateMetric),
                () -> verifyNoInteractions(userTransactionalDataStoreService)
        );
    }

    @Test
    void handleDacResponse_whenSalaryDetected_updateToSuccess(@Random DacAccountSnapshot dacAccountSnapshot) {
        // given
        final var responseFromDsDate = OffsetDateTime.now();
        dacAccountSnapshot.setCustomDACData(CustomDACData.newBuilder().setHasSalary(true).build());
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(dacAccountSnapshot.getUserUUID());
        userStatesStore.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder()
                .responseFromDataSolution(responseFromDsDate)
                .sentForClassificationCounter(7)
                .build());
        final var transactionalDataStore = new UserTransactionalDataStore();
        ArgumentCaptor<UserStatesStore> argument = ArgumentCaptor.forClass(UserStatesStore.class);

        when(userTransactionalDataStoreMapper.toTransactionalDataStore(dacAccountSnapshot)).thenReturn(transactionalDataStore);
        when(userTransactionalDataStoreService.save(transactionalDataStore)).thenReturn(Mono.just(transactionalDataStore));
        when(userStatesStoreService.findById(dacAccountSnapshot.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        doNothing().when(offersStateMetric).addTransactionalDataFromDACTimer(dacAccountSnapshot.getUserUUID(), responseFromDsDate);
        when(userStatesStoreService.save(userStatesStore)).thenReturn(Mono.just(userStatesStore));
        // when
        Mono<Void> result = userTransactionalDataIngester.handleDacResponse(dacAccountSnapshot);

        // then
        assertAll(
                () -> StepVerifier.create(result).expectNextCount(0).verifyComplete(),
                () -> verify(userTransactionalDataStoreMapper).toTransactionalDataStore(dacAccountSnapshot),
                () -> verify(userTransactionalDataStoreService).save(transactionalDataStore),
                () -> verify(userStatesStoreService).findById(dacAccountSnapshot.getUserUUID()),
                () -> verify(offersStateMetric).addTransactionalDataFromDACTimer(dacAccountSnapshot.getUserUUID(), responseFromDsDate),
                () -> verify(userStatesStoreService).save(argument.capture()),
                () -> assertEquals(Status.SUCCESS, argument.getValue().getTransactionalDataStateDetails().getState()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getAccountInternalId()),
                () -> assertTrue(argument.getValue().getTransactionalDataStateDetails().getSalaryAccountAdded()),
                () -> assertNull(argument.getValue().getTransactionalDataStateDetails().getSentForClassificationCounter()),
                () -> assertNotNull(argument.getValue().getTransactionalDataStateDetails().getResponseDateTime()),
                () -> assertTrue(argument.getValue().getTransactionalDataStateDetails().getUserVerifiedByBankAccount()),
                () -> assertEquals(responseFromDsDate, argument.getValue().getTransactionalDataStateDetails().getResponseFromDataSolution())
        );
    }
}
