package de.joonko.loan.user.service;

import de.joonko.loan.contract.ContractStorageService;
import de.joonko.loan.contract.model.DocumentDetails;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanDemandRequestService;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.IdentificationLink;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStoreService;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStoreService;
import de.joonko.loan.partner.consors.ConsorsStoreService;
import de.joonko.loan.partner.santander.SantanderStoreService;
import de.joonko.loan.partner.swk.SwkStoreService;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;
import de.joonko.loan.webhooks.idnow.service.IdentificationWebHookStoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDeletionServiceImplTest {

    private UserDeletionService userDeletionService;

    private LoanDemandStoreService loanDemandStoreService;
    private LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private ConsorsStoreService consorsStoreService;
    private IdentificationLinkService identificationLinkService;
    private IdentificationWebHookStoreService identificationWebHookStoreService;
    private SantanderStoreService santanderStoreService;
    private SwkStoreService swkStoreService;
    private UserStatesStoreService userStatesStoreService;
    private UserAdditionalInfoService userAdditionalInfoService;
    private UserPersonalInfoService userPersonalInfoService;
    private UserTransactionalDataStoreService userTransactionalDataStoreService;
    private UserTransactionalDraftDataStoreService userTransactionalDraftDataStoreService;
    private LoanOfferStoreService loanOfferStoreService;
    private LoanDemandRequestService loanDemandRequestService;
    private ContractStorageService contractStorageService;

    @BeforeEach
    void setUp() {
        loanDemandStoreService = mock(LoanDemandStoreService.class);
        loanApplicationAuditTrailService = mock(LoanApplicationAuditTrailService.class);
        consorsStoreService = mock(ConsorsStoreService.class);
        identificationLinkService = mock(IdentificationLinkService.class);
        identificationWebHookStoreService = mock(IdentificationWebHookStoreService.class);
        santanderStoreService = mock(SantanderStoreService.class);
        swkStoreService = mock(SwkStoreService.class);
        userStatesStoreService = mock(UserStatesStoreService.class);
        userAdditionalInfoService = mock(UserAdditionalInfoService.class);
        userPersonalInfoService = mock(UserPersonalInfoService.class);
        userTransactionalDataStoreService = mock(UserTransactionalDataStoreService.class);
        userTransactionalDraftDataStoreService = mock(UserTransactionalDraftDataStoreService.class);
        loanOfferStoreService = mock(LoanOfferStoreService.class);
        loanDemandRequestService = mock(LoanDemandRequestService.class);
        contractStorageService = mock(ContractStorageService.class);

        userDeletionService = new UserDeletionServiceImpl(loanDemandStoreService, loanApplicationAuditTrailService, consorsStoreService,
                identificationLinkService, identificationWebHookStoreService, santanderStoreService, swkStoreService,
                userStatesStoreService, userAdditionalInfoService, userPersonalInfoService, userTransactionalDataStoreService,
                userTransactionalDraftDataStoreService, loanOfferStoreService, loanDemandRequestService, contractStorageService);
    }

    @Test
    void delete_user_data() {
        // given
        final var userId = "f7bd6b9b-510d-4138-8a8d-688b0b7f9f1b";
        final var applicationId1 = "123";
        final var applicationId2 = "345";
        final var loanDemands = List.of(
                LoanDemandStore.builder().applicationId(applicationId1).build(),
                LoanDemandStore.builder().applicationId(applicationId2).build()
        );
        when(loanDemandStoreService.findByUserId(userId)).thenReturn(Flux.fromIterable(loanDemands));
        when(loanDemandStoreService.deleteById(anyString())).thenReturn(Mono.just(LoanDemandStore.builder().build()));
        when(consorsStoreService.deletePersonalizedCalculationByApplicationId(anyString())).thenReturn(Flux.fromIterable(List.of()));
        when(santanderStoreService.deleteByApplicationId(anyString())).thenReturn(Flux.fromIterable(List.of()));
        when(loanApplicationAuditTrailService.deleteByApplicationId(anyString())).thenReturn(Flux.fromIterable(List.of()));
        when(identificationLinkService.deleteByApplicationId(anyString())).thenReturn(Flux.fromIterable(List.of(
                IdentificationLink.builder().externalIdentId("78362935").build(),
                IdentificationLink.builder().externalIdentId("379693").build()
        )));
        when(identificationWebHookStoreService.deleteByTransactionNumber(anyString())).thenReturn(Mono.just(1L));
        when(swkStoreService.deleteSwkOffer(anyString())).thenReturn(Flux.fromIterable(List.of()));
        when(swkStoreService.deleteSwkCreditApplication(anyString())).thenReturn(Flux.fromIterable(List.of()));
        when(userStatesStoreService.deleteByUserId(userId)).thenReturn(Mono.just(new UserStatesStore()));
        when(userAdditionalInfoService.deleteById(userId)).thenReturn(Mono.just(new UserAdditionalInformationStore()));
        when(userPersonalInfoService.deleteById(userId)).thenReturn(Mono.just(new UserPersonalInformationStore()));
        when(userTransactionalDataStoreService.deleteById(userId)).thenReturn(Mono.just(new UserTransactionalDataStore()));
        when(userTransactionalDraftDataStoreService.deleteById(userId)).thenReturn(Mono.just(new UserTransactionalDraftDataStore()));
        when(loanOfferStoreService.deleteAllByUserId(userId)).thenReturn(Flux.fromIterable(List.of()));
        when(loanDemandRequestService.deleteAllByUserId(userId)).thenReturn(Flux.fromIterable(List.of()));
        when(loanOfferStoreService.findLoanOfferStoreByUserUUIDAndContractsExists(userId)).thenReturn(Mono.just(List.of(
                LoanOfferStore.builder().contracts(List.of(
                        DocumentDetails.builder().name("agreement").key("key1").build(),
                        DocumentDetails.builder().name("schedule").key("key2").build()
                )).build(),
                LoanOfferStore.builder().contracts(List.of(
                        DocumentDetails.builder().name("agreement").key("key3").build(),
                        DocumentDetails.builder().name("schedule").key("key4").build()
                )).build()
        )));
        when(contractStorageService.deleteContracts(List.of("key1", "key2", "key3", "key4"), userId)).thenReturn(Mono.empty());

        // when
        var deleteUserDataMono = userDeletionService.deleteUser(userId);

        // then
        assertAll(
                () -> StepVerifier.create(deleteUserDataMono).expectNextCount(0).verifyComplete(),
                () -> verify(loanDemandStoreService).findByUserId(userId),
                () -> verify(loanDemandStoreService, times(2)).deleteById(anyString()),
                () -> verify(loanApplicationAuditTrailService, times(2)).deleteByApplicationId(anyString()),
                () -> verify(consorsStoreService, times(2)).deletePersonalizedCalculationByApplicationId(anyString()),
                () -> verify(identificationLinkService, times(2)).deleteByApplicationId(anyString()),
                () -> verify(identificationWebHookStoreService, times(4)).deleteByTransactionNumber(anyString()),
                () -> verify(santanderStoreService, times(2)).deleteByApplicationId(anyString()),
                () -> verify(swkStoreService, times(2)).deleteSwkOffer(anyString()),
                () -> verify(swkStoreService, times(2)).deleteSwkCreditApplication(anyString()),
                () -> verify(userStatesStoreService).deleteByUserId(userId),
                () -> verify(userAdditionalInfoService).deleteById(userId),
                () -> verify(userPersonalInfoService).deleteById(userId),
                () -> verify(userTransactionalDataStoreService).deleteById(userId),
                () -> verify(userTransactionalDraftDataStoreService).deleteById(userId),
                () -> verify(loanOfferStoreService).deleteAllByUserId(userId),
                () -> verify(loanDemandRequestService).deleteAllByUserId(userId),
                () -> verify(contractStorageService).deleteContracts(List.of("key1", "key2", "key3", "key4"), userId)
        );
    }
}