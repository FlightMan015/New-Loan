package de.joonko.loan.identification.service;

import de.joonko.loan.acceptoffer.domain.AcceptOfferService;
import de.joonko.loan.contract.ContractStorageService;
import de.joonko.loan.contract.model.DocumentDetails;
import de.joonko.loan.contract.model.PresignedDocumentDetails;
import de.joonko.loan.db.repositories.LoanOfferStoreRepository;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.exception.ExternalIdentIdNotFoundException;
import de.joonko.loan.identification.exception.IdentificationFailureException;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.CreateIdentResponse;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.identification.model.IdentificationLink;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.service.idnow.ConsorsIdentService;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper.UserPersonalInformationMapper;
import de.joonko.loan.user.service.UserAdditionalInformationService;
import de.joonko.loan.user.service.UserAdditionalInformationStore;
import de.joonko.loan.user.service.UserPersonalInfoService;
import de.joonko.loan.user.service.UserPersonalInformationRepository;
import de.joonko.loan.user.service.UserPersonalInformationStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;

import java.util.List;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(RandomBeansExtension.class)
class IdentificationServiceTest {
    private static IdentificationService identificationService;

    @Random
    private LoanOfferStore loanOfferStore;

    @Random
    private UserPersonalInformationStore userPersonalInformationStore;

    @Random
    private UserAdditionalInformationStore userAdditionalInformationStore;

    @Random
    private CreateIdentRequest createIdentRequest;


    private IdentServiceFactory identServiceFactory;
    private IdentificationAuditService identificationAuditService;
    private LoanOfferStoreService loanOfferStoreService;
    private LoanDemandStoreService loanDemandStoreService;
    private IdentificationLinkService identificationLinkService;
    private LoanOfferStoreRepository loanOfferStoreRepository;
    private UserPersonalInformationRepository userPersonalInformationRepository;
    private LoanOfferStoreRepository offerStoreRepository;

    private static ConsorsIdentService consorsIdentService;
    private UserPersonalInformationMapper userPersonalInformationMapper;

    private AcceptOfferService acceptOfferService;
    private UserPersonalInfoService userPersonalInfoService;
    private UserAdditionalInformationService userAdditionalInformationService;
    private ContractStorageService contractStorageService;

    private static final String EXTERNAL_IDENT_ID = "12345";

    @BeforeEach
    void beforeEach() {
        consorsIdentService = mock(ConsorsIdentService.class);

        identServiceFactory = mock(IdentServiceFactory.class);
        identificationAuditService = mock(IdentificationAuditService.class);
        loanOfferStoreService = mock(LoanOfferStoreService.class);
        loanDemandStoreService = mock(LoanDemandStoreService.class);
        identificationLinkService = mock(IdentificationLinkService.class);
        loanOfferStoreRepository = mock(LoanOfferStoreRepository.class);
        userPersonalInformationRepository = mock(UserPersonalInformationRepository.class);
        offerStoreRepository = mock(LoanOfferStoreRepository.class);
        userPersonalInformationMapper = mock(UserPersonalInformationMapper.class);
        acceptOfferService = mock(AcceptOfferService.class);
        userPersonalInfoService = mock(UserPersonalInfoService.class);
        userAdditionalInformationService = mock(UserAdditionalInformationService.class);
        contractStorageService = mock(ContractStorageService.class);


        identificationService = new IdentificationService(identServiceFactory, identificationAuditService,
                loanOfferStoreService, loanDemandStoreService, identificationLinkService,
                loanOfferStoreRepository, userPersonalInformationRepository, offerStoreRepository, userPersonalInformationMapper,
                acceptOfferService, userPersonalInfoService, userAdditionalInformationService, contractStorageService);
    }

    @Test
    void createIdentificationByUserSuccessCase() {
        final var userId = "userId";
        final var loanOfferId = "offerId";
        final var applicationId = "applicationId";
        loanOfferStore = loanOfferStore.toBuilder()
                .loanOfferId(loanOfferId)
                .userUUID(userId)
                .applicationId(applicationId)
                .isAccepted(true)
                .contracts(List.of())
                .build();
        createIdentRequest = createIdentRequest.toBuilder()
                .loanOfferId(loanOfferId)
                .build();
        final var createIdentResponse = CreateIdentResponse.builder()
                .kycUrl("a")
                .kycProvider(IdentificationProvider.ID_NOW)
                .documents(Documents.builder()
                        .documents(getDocumentsFromProviders())
                        .build())
                .build();

        final var documentDetails = getDocuments();

        when(loanOfferStoreService.findById(loanOfferId)).thenReturn(Mono.just(loanOfferStore));
        when(userPersonalInfoService.findById(userId)).thenReturn(Mono.just(userPersonalInformationStore));
        when(userAdditionalInformationService.findById(userId)).thenReturn(Mono.just(of(userAdditionalInformationStore)));
        when(userPersonalInformationMapper.from(userPersonalInformationStore, loanOfferStore, of(userAdditionalInformationStore))).thenReturn(createIdentRequest);
        when(identServiceFactory.getIdentService(anyString())).thenReturn(consorsIdentService);
        when(consorsIdentService.createIdent(createIdentRequest)).thenReturn(Mono.just(createIdentResponse));
        when(contractStorageService.storeContracts(createIdentResponse.getDocuments(), userId, applicationId, loanOfferId)).thenReturn(Mono.just(documentDetails));
        when(loanOfferStoreService.saveOffer(loanOfferStore)).thenReturn(Mono.just(loanOfferStore));
        when(consorsIdentService.getProvider()).thenReturn(IdentificationProvider.ID_NOW);

        ArgumentCaptor<LoanOfferStore> argument = ArgumentCaptor.forClass(LoanOfferStore.class);

        final var response = identificationService.createIdentification(userId, loanOfferId, applicationId);

        assertAll(
                () -> StepVerifier.create(response)
                        .expectSubscription()
                        .expectNextCount(1)
                        .verifyComplete(),
                () -> verify(loanOfferStoreService, times(2)).findById(loanOfferId),
                () -> verify(userPersonalInfoService).findById(userId),
                () -> verify(userAdditionalInformationService).findById(userId),
                () -> verify(userPersonalInformationMapper).from(userPersonalInformationStore, loanOfferStore, of(userAdditionalInformationStore)),
                () -> verify(identServiceFactory, times(2)).getIdentService(anyString()),
                () -> verify(consorsIdentService).createIdent(createIdentRequest),
                () -> verify(identificationAuditService).kycLinkCreated(createIdentResponse, createIdentRequest),
                () -> verify(loanOfferStoreService, times(3)).saveOffer(argument.capture()),
                () -> assertEquals(documentDetails, argument.getValue().getContracts()),
                () -> assertEquals(createIdentResponse.getKycUrl(), argument.getValue().getKycUrl()),
                () -> verify(consorsIdentService, times(2)).getProvider(),
                () -> verify(identificationAuditService).kycInitiated(IdentificationProvider.ID_NOW.name(), applicationId),
                () -> assertEquals("INITIATED", argument.getValue().getKycStatus()),
                () -> assertEquals("INITIATED", argument.getValue().getOfferStatus()),
                () -> assertEquals(IdentificationProvider.ID_NOW, argument.getValue().getKycProvider())
        );
    }


    @Test
    void createIdentificationByUserContractsAlreadyAvailableCase() {
        final var userId = "userId";
        final var loanOfferId = "offerId";
        final var applicationId = "applicationId";

        final var documentDetails = getDocuments();
        loanOfferStore = loanOfferStore.toBuilder()
                .loanOfferId(loanOfferId)
                .userUUID(userId)
                .applicationId(applicationId)
                .isAccepted(true)
                .kycUrl("kycURL")
                .kycProvider(IdentificationProvider.ID_NOW)
                .contracts(documentDetails)
                .build();

        when(loanOfferStoreService.findById(loanOfferId)).thenReturn(Mono.just(loanOfferStore));

        final var response = identificationService.createIdentification(userId, loanOfferId, applicationId);

        assertAll(
                () -> StepVerifier.create(response)
                        .expectSubscription()
                        .expectNextCount(1)
                        .verifyComplete(),
                () -> verify(loanOfferStoreService).findById(loanOfferId),
                () -> verifyNoMoreInteractions(loanOfferStoreService),
                () -> verifyNoInteractions(userPersonalInfoService, userAdditionalInformationService, userPersonalInformationMapper, identServiceFactory, consorsIdentService)
        );
    }

    @Test
    void createIdentificationByUserWrongOffer() {
        final var userId = "userId";
        final var loanOfferId = "offerId";
        final var applicationId = "applicationId";

        when(loanOfferStoreService.findById(loanOfferId)).thenReturn(Mono.just(LoanOfferStore.builder().build()));
        final var response = identificationService.createIdentification(userId, loanOfferId, applicationId);

        StepVerifier.create(response)
                .expectSubscription()
                .expectError(IllegalStateException.class)
                .verify();
    }


    @Test
    void createIdentificationByInternalUserSuccessCase() {
        final var userId = "userId";
        final var loanOfferId = "offerId";
        final var applicationId = "applicationId";
        loanOfferStore = loanOfferStore.toBuilder()
                .loanOfferId(loanOfferId)
                .userUUID(userId)
                .applicationId(applicationId)
                .isAccepted(false)
                .kycStatus(null)
                .contracts(null)
                .build();
        createIdentRequest = createIdentRequest.toBuilder()
                .loanOfferId(loanOfferId)
                .build();
        final var createIdentResponse = CreateIdentResponse.builder()
                .kycUrl("a")
                .kycProvider(IdentificationProvider.ID_NOW)
                .documents(Documents.builder()
                        .documents(getDocumentsFromProviders())
                        .build())
                .build();

        final var documentDetails = getDocuments();

        final var presignedDocs = getPreesignedDocuments();

        when(acceptOfferService.acceptOfferByInternalUser(loanOfferId)).thenReturn(Mono.just(loanOfferStore));
        when(loanOfferStoreService.findById(loanOfferId)).thenReturn(Mono.just(loanOfferStore));
        when(userPersonalInfoService.findById(userId)).thenReturn(Mono.just(userPersonalInformationStore));
        when(userAdditionalInformationService.findById(userId)).thenReturn(Mono.just(of(userAdditionalInformationStore)));
        when(userPersonalInformationMapper.from(userPersonalInformationStore, loanOfferStore, of(userAdditionalInformationStore))).thenReturn(createIdentRequest);
        when(identServiceFactory.getIdentService(anyString())).thenReturn(consorsIdentService);
        when(consorsIdentService.createIdent(createIdentRequest)).thenReturn(Mono.just(createIdentResponse));
        when(contractStorageService.storeContracts(createIdentResponse.getDocuments(), userId, applicationId, loanOfferId)).thenReturn(Mono.just(documentDetails));
        when(loanOfferStoreService.saveOffer(loanOfferStore)).thenReturn(Mono.just(loanOfferStore));
        when(consorsIdentService.getProvider()).thenReturn(IdentificationProvider.ID_NOW);
        when(contractStorageService.preSignContracts(documentDetails, userId)).thenReturn(Mono.just(presignedDocs));

        ArgumentCaptor<LoanOfferStore> argument = ArgumentCaptor.forClass(LoanOfferStore.class);

        final var response = identificationService.acceptAndCreateIdentificationByInternalUser(loanOfferId);

        assertAll(
                () -> StepVerifier.create(response)
                        .expectSubscription()
                        .expectNextMatches(getOfferContractsResponse ->
                                getOfferContractsResponse.getKycURL().equals(createIdentResponse.getKycUrl()) &&
                                        getOfferContractsResponse.getContracts().stream().collect(toSet()).equals(presignedDocs.stream().collect(toSet()))
                        )
                        .verifyComplete(),
                () -> verify(acceptOfferService).acceptOfferByInternalUser(loanOfferId),
                () -> verify(loanOfferStoreService).findById(loanOfferId),
                () -> verify(userPersonalInfoService).findById(userId),
                () -> verify(userAdditionalInformationService).findById(userId),
                () -> verify(userPersonalInformationMapper).from(userPersonalInformationStore, loanOfferStore, of(userAdditionalInformationStore)),
                () -> verify(identServiceFactory, times(2)).getIdentService(anyString()),
                () -> verify(consorsIdentService).createIdent(createIdentRequest),
                () -> verify(identificationAuditService).kycLinkCreated(createIdentResponse, createIdentRequest),
                () -> verify(loanOfferStoreService, times(3)).saveOffer(argument.capture()),
                () -> assertEquals(documentDetails, argument.getValue().getContracts()),
                () -> assertEquals(createIdentResponse.getKycUrl(), argument.getValue().getKycUrl()),
                () -> verify(consorsIdentService, times(2)).getProvider(),
                () -> verify(identificationAuditService).kycInitiated(IdentificationProvider.ID_NOW.name(), applicationId),
                () -> assertEquals("INITIATED", argument.getValue().getKycStatus()),
                () -> assertEquals("INITIATED", argument.getValue().getOfferStatus()),
                () -> assertEquals(IdentificationProvider.ID_NOW, argument.getValue().getKycProvider()),
                () -> verify(contractStorageService).preSignContracts(documentDetails, userId)
        );
    }

    private List<Document> getDocumentsFromProviders() {
        return List.of(
                Document.builder()
                        .documentId("contract")
                        .content("Contract".getBytes())
                        .build(),
                Document.builder()
                        .documentId("schedule")
                        .content("Schedule".getBytes())
                        .build()
        );
    }

    private List<PresignedDocumentDetails> getPreesignedDocuments() {
        return List.of(
                PresignedDocumentDetails.builder()
                        .name("first")
                        .url("First")
                        .build(),
                PresignedDocumentDetails.builder()
                        .name("second")
                        .url("Second")
                        .build()
        );
    }

    @Test
    void createIdentificationByInternalUserIdentificationAlreadyPassedCase() {
        final var userId = "userId";
        final var loanOfferId = "offerId";
        final var applicationId = "applicationId";

        final var documentDetails = getDocuments();

        loanOfferStore = loanOfferStore.toBuilder()
                .loanOfferId(loanOfferId)
                .userUUID(userId)
                .applicationId(applicationId)
                .isAccepted(true)
                .kycStatus("INITIATED")
                .kycUrl("a")
                .contracts(documentDetails)
                .build();

        final var presignedDocs = getPreesignedDocuments();

        when(acceptOfferService.acceptOfferByInternalUser(loanOfferId)).thenReturn(Mono.just(loanOfferStore));
        when(contractStorageService.preSignContracts(documentDetails, userId)).thenReturn(Mono.just(presignedDocs));

        final var response = identificationService.acceptAndCreateIdentificationByInternalUser(loanOfferId);

        assertAll(
                () -> StepVerifier.create(response)
                        .expectSubscription()
                        .expectNextMatches(getOfferContractsResponse ->
                                getOfferContractsResponse.getKycURL().equals(loanOfferStore.getKycUrl()) &&
                                        getOfferContractsResponse.getContracts().stream().collect(toSet()).equals(presignedDocs.stream().collect(toSet()))
                        )
                        .verifyComplete(),
                () -> verify(acceptOfferService).acceptOfferByInternalUser(loanOfferId),
                () -> verify(contractStorageService).preSignContracts(documentDetails, userId)
        );
    }

    private List<DocumentDetails> getDocuments() {
        return List.of(
                DocumentDetails.builder()
                        .key("contract")
                        .name("Contract")
                        .build(),
                DocumentDetails.builder()
                        .key("schedule")
                        .name("Schedule")
                        .build()
        );
    }

    @Test
    void throwExceptionWhenExternalIdentIdDoesNotExist() {
        // given
        when(identificationLinkService.getByExternalIdentId(EXTERNAL_IDENT_ID)).thenThrow(new ExternalIdentIdNotFoundException(""));

        // when
        Executable response = () -> identificationService.getIdentificationStatus(EXTERNAL_IDENT_ID);

        // then
        assertThrows(ExternalIdentIdNotFoundException.class, response);
    }

    @Test
    void throwExceptionWhenNotAuthorizedAfterGettingIdentStatus() {
        // given
        IdentificationLink identificationLink = IdentificationLink.builder()
                .loanProvider("Consors Finanz")
                .build();
        when(identificationLinkService.getByExternalIdentId(EXTERNAL_IDENT_ID)).thenReturn(identificationLink);
        when(identServiceFactory.getIdentService(anyString())).thenReturn(consorsIdentService);
        when(consorsIdentService.getIdentStatus(EXTERNAL_IDENT_ID)).thenThrow(new IdentificationFailureException("Identification error"));

        // when
        Executable response = () -> identificationService.getIdentificationStatus(EXTERNAL_IDENT_ID);

        // then
        assertThrows(IdentificationFailureException.class, response);
    }

    @Test
    void getCancelStatusWhenNotFoundAfterGettingIdentStatus() {
        // given
        IdentificationLink identificationLink = IdentificationLink.builder()
                .loanProvider("Consors Finanz")
                .offerId("123")
                .build();
        when(identificationLinkService.getByExternalIdentId(EXTERNAL_IDENT_ID)).thenReturn(identificationLink);
        when(identServiceFactory.getIdentService(anyString())).thenReturn(consorsIdentService);
        when(consorsIdentService.getIdentStatus(EXTERNAL_IDENT_ID)).thenReturn(Mono.just("CANCELLED"));
        when(consorsIdentService.getProvider()).thenReturn(IdentificationProvider.ID_NOW);
        when(offerStoreRepository.findById(anyString())).thenReturn(of(LoanOfferStore.builder().userUUID("userId").build()));
        UserPersonalInformationStore userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setFirstName("firstName");
        when(userPersonalInformationRepository.findById(anyString())).thenReturn(of(userPersonalInformationStore));
        when(loanOfferStoreService.findByLoanOfferId(anyString())).thenReturn(LoanOfferStore.builder().build());


        // when
        var monoGetIdentStatusResp = identificationService.getIdentificationStatus(EXTERNAL_IDENT_ID);

        // then
        StepVerifier.create(monoGetIdentStatusResp)
                .consumeNextWith(consume -> assertAll(
                        () -> assertEquals("CANCELLED", consume.getStatus()),
                        () -> assertEquals("firstName", consume.getFirstName()),
                        () -> assertEquals("Consors Finanz", consume.getLoanProvider()),
                        () -> assertEquals(IdentificationProvider.ID_NOW, consume.getKycProvider())
                ))
                .verifyComplete();
    }

    @Test
    void getValidIdentStatus() {
        // given
        IdentificationLink identificationLink = IdentificationLink.builder()
                .loanProvider("Consors Finanz")
                .offerId("123")
                .build();
        when(identificationLinkService.getByExternalIdentId(EXTERNAL_IDENT_ID)).thenReturn(identificationLink);
        when(identServiceFactory.getIdentService(anyString())).thenReturn(consorsIdentService);
        when(consorsIdentService.getIdentStatus(EXTERNAL_IDENT_ID)).thenReturn(Mono.just("FRAUD_SUSPICION_PENDING"));
        when(consorsIdentService.getProvider()).thenReturn(IdentificationProvider.ID_NOW);
        when(offerStoreRepository.findById(anyString())).thenReturn(of(LoanOfferStore.builder().userUUID("userId").build()));
        UserPersonalInformationStore userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setFirstName("firstName");
        when(userPersonalInformationRepository.findById(anyString())).thenReturn(of(userPersonalInformationStore));
        when(loanOfferStoreService.findByLoanOfferId(anyString())).thenReturn(LoanOfferStore.builder().build());

        // when
        var monoGetIdentStatusResp = identificationService.getIdentificationStatus(EXTERNAL_IDENT_ID);

        // then
        StepVerifier.create(monoGetIdentStatusResp)
                .consumeNextWith(consume -> assertAll(
                        () -> assertEquals("FRAUD_SUSPICION_PENDING", consume.getStatus()),
                        () -> assertEquals("firstName", consume.getFirstName()),
                        () -> assertEquals("Consors Finanz", consume.getLoanProvider()),
                        () -> assertEquals(IdentificationProvider.ID_NOW, consume.getKycProvider())
                ))
                .verifyComplete();
    }

    @Test
    void acceptAndGetContracts() {
        final var userId = "userId";
        final var loanOfferId = "offerId";
        final var applicationId = "applicationId";
        loanOfferStore = loanOfferStore.toBuilder()
                .loanOfferId(loanOfferId)
                .userUUID(userId)
                .applicationId(applicationId)
                .kycUrl(null)
                .isAccepted(true)
                .contracts(List.of())
                .build();
        createIdentRequest = createIdentRequest.toBuilder()
                .loanOfferId(loanOfferId)
                .build();
        final var createIdentResponse = CreateIdentResponse.builder()
                .kycUrl("a")
                .kycProvider(IdentificationProvider.ID_NOW)
                .documents(Documents.builder()
                        .documents(getDocumentsFromProviders())
                        .build())
                .build();

        final var documentDetails = getDocuments();
        final var presignedDocs = getPreesignedDocuments();

        when(acceptOfferService.acceptOfferByUser(loanOfferId, userId)).thenReturn(Mono.just(loanOfferStore));
        when(loanOfferStoreService.findById(loanOfferId)).thenReturn(Mono.just(loanOfferStore));
        when(userPersonalInfoService.findById(userId)).thenReturn(Mono.just(userPersonalInformationStore));
        when(userAdditionalInformationService.findById(userId)).thenReturn(Mono.just(of(userAdditionalInformationStore)));
        when(userPersonalInformationMapper.from(userPersonalInformationStore, loanOfferStore, of(userAdditionalInformationStore))).thenReturn(createIdentRequest);
        when(identServiceFactory.getIdentService(anyString())).thenReturn(consorsIdentService);
        when(consorsIdentService.createIdent(createIdentRequest)).thenReturn(Mono.just(createIdentResponse));
        when(contractStorageService.storeContracts(createIdentResponse.getDocuments(), userId, applicationId, loanOfferId)).thenReturn(Mono.just(documentDetails));
        when(loanOfferStoreService.saveOffer(loanOfferStore)).thenReturn(Mono.just(loanOfferStore));
        when(consorsIdentService.getProvider()).thenReturn(IdentificationProvider.ID_NOW);
        when(contractStorageService.preSignContracts(documentDetails, userId)).thenReturn(Mono.just(presignedDocs));

        ArgumentCaptor<LoanOfferStore> argument = ArgumentCaptor.forClass(LoanOfferStore.class);

        final var response = identificationService.acceptAndGetContracts(loanOfferId, userId);

        assertAll(
                () -> StepVerifier.create(response)
                        .expectSubscription()
                        .expectNextCount(1)
                        .verifyComplete(),
                () -> verify(acceptOfferService).acceptOfferByUser(loanOfferId, userId),
                () -> verify(loanOfferStoreService).findById(loanOfferId),
                () -> verify(userPersonalInfoService).findById(userId),
                () -> verify(userAdditionalInformationService).findById(userId),
                () -> verify(userPersonalInformationMapper).from(userPersonalInformationStore, loanOfferStore, of(userAdditionalInformationStore)),
                () -> verify(identServiceFactory).getIdentService(anyString()),
                () -> verify(consorsIdentService).createIdent(createIdentRequest),
                () -> verify(identificationAuditService).kycLinkCreated(createIdentResponse, createIdentRequest),
                () -> verify(loanOfferStoreService, times(2)).saveOffer(argument.capture()),
                () -> assertEquals(documentDetails, argument.getValue().getContracts()),
                () -> assertEquals(createIdentResponse.getKycUrl(), argument.getValue().getKycUrl()),
                () -> verify(contractStorageService).preSignContracts(documentDetails, userId)
        );
    }

    @Test
    void acceptAndGetContracts_whenAlreadyAccepted() {
        final var userId = "userId";
        final var loanOfferId = "offerId";
        final var applicationId = "applicationId";

        final var documentDetails = getDocuments();
        loanOfferStore = loanOfferStore.toBuilder()
                .loanOfferId(loanOfferId)
                .userUUID(userId)
                .applicationId(applicationId)
                .isAccepted(true)
                .kycUrl("url")
                .contracts(documentDetails)
                .build();

        final var presignedDocs = getPreesignedDocuments();

        when(acceptOfferService.acceptOfferByUser(loanOfferId, userId)).thenReturn(Mono.just(loanOfferStore));
        when(contractStorageService.preSignContracts(documentDetails, userId)).thenReturn(Mono.just(presignedDocs));

        ArgumentCaptor<LoanOfferStore> argument = ArgumentCaptor.forClass(LoanOfferStore.class);

        final var response = identificationService.acceptAndGetContracts(loanOfferId, userId);

        assertAll(
                () -> StepVerifier.create(response)
                        .expectSubscription()
                        .expectNextCount(1)
                        .verifyComplete(),
                () -> verify(acceptOfferService).acceptOfferByUser(loanOfferId, userId),
                () -> verify(contractStorageService).preSignContracts(documentDetails, userId)
        );
    }


    @Test
    void createIdentificationV2() {
        final var userId = "userId";
        final var loanOfferId = "offerId";
        final var applicationId = "applicationId";
        final var documentDetails = getDocuments();
        loanOfferStore = loanOfferStore.toBuilder()
                .loanOfferId(loanOfferId)
                .userUUID(userId)
                .applicationId(applicationId)
                .isAccepted(true)
                .kycUrl("url")
                .kycStatus(null)
                .kycProvider(null)
                .contracts(documentDetails)
                .build();

        when(loanOfferStoreService.findById(loanOfferId)).thenReturn(Mono.just(loanOfferStore));
        when(identServiceFactory.getIdentService(anyString())).thenReturn(consorsIdentService);
        when(loanOfferStoreService.saveOffer(loanOfferStore)).thenReturn(Mono.just(loanOfferStore));
        when(consorsIdentService.getProvider()).thenReturn(IdentificationProvider.ID_NOW);

        ArgumentCaptor<LoanOfferStore> argument = ArgumentCaptor.forClass(LoanOfferStore.class);
        final var response = identificationService.createIdentificationV2(userId, loanOfferId, applicationId);

        assertAll(
                () -> StepVerifier.create(response)
                        .expectSubscription()
                        .expectNextCount(1)
                        .verifyComplete(),
                () -> verify(loanOfferStoreService).findById(loanOfferId),
                () -> verify(identServiceFactory).getIdentService(anyString()),
                () -> verify(consorsIdentService, times(2)).getProvider(),
                () -> verify(identificationAuditService).kycInitiated(IdentificationProvider.ID_NOW.name(), applicationId),
                () -> verify(loanOfferStoreService).saveOffer(argument.capture()),
                () -> assertEquals("INITIATED", argument.getValue().getKycStatus()),
                () -> assertEquals("INITIATED", argument.getValue().getOfferStatus()),
                () -> assertEquals(IdentificationProvider.ID_NOW, argument.getValue().getKycProvider())
        );
    }

    @Test
    void createIdentificationV2_whenKycOnceInitiated() {
        final var userId = "userId";
        final var loanOfferId = "offerId";
        final var applicationId = "applicationId";
        final var documentDetails = getDocuments();
        loanOfferStore = loanOfferStore.toBuilder()
                .loanOfferId(loanOfferId)
                .userUUID(userId)
                .applicationId(applicationId)
                .isAccepted(true)
                .kycUrl("url")
                .kycProvider(IdentificationProvider.ID_NOW)
                .kycStatus("SUCCESS")
                .contracts(documentDetails)
                .build();

        when(loanOfferStoreService.findById(loanOfferId)).thenReturn(Mono.just(loanOfferStore));

        final var response = identificationService.createIdentificationV2(userId, loanOfferId, applicationId);

        assertAll(
                () -> StepVerifier.create(response)
                        .expectSubscription()
                        .expectNextMatches(res ->
                                res.getKycProvider().equals(loanOfferStore.getKycProvider()) &&
                                        res.getKycUrl().equals(loanOfferStore.getKycUrl())
                        )
                        .verifyComplete(),
                () -> verify(loanOfferStoreService).findById(loanOfferId),
                () -> verifyNoInteractions(identServiceFactory, consorsIdentService, identificationAuditService),
                () -> verifyNoMoreInteractions(loanOfferStoreService)
        );
    }

    @Test
    void createIdentificationV2_whenOfferNotBelongingToUser() {
        final var userId = "userId";
        final var loanOfferId = "offerId";
        final var applicationId = "applicationId";
        final var documentDetails = getDocuments();
        loanOfferStore = loanOfferStore.toBuilder()
                .loanOfferId(loanOfferId)
                .userUUID("anotherUserId")
                .applicationId("anotherApplicationId")
                .isAccepted(true)
                .contracts(documentDetails)
                .build();

        when(loanOfferStoreService.findById(loanOfferId)).thenReturn(Mono.just(loanOfferStore));

        final var response = identificationService.createIdentificationV2(userId, loanOfferId, applicationId);

        assertAll(
                () -> StepVerifier.create(response)
                        .expectSubscription()
                        .verifyError(),
                () -> verify(loanOfferStoreService).findById(loanOfferId),
                () -> verifyNoInteractions(identServiceFactory, consorsIdentService, identificationAuditService),
                () -> verifyNoMoreInteractions(loanOfferStoreService)
        );
    }

    @Test
    void createIdentificationV2_whenOfferAccepted() {
        final var userId = "userId";
        final var loanOfferId = "offerId";
        final var applicationId = "applicationId";
        final var documentDetails = getDocuments();
        loanOfferStore = loanOfferStore.toBuilder()
                .loanOfferId(loanOfferId)
                .userUUID(userId)
                .applicationId(applicationId)
                .isAccepted(false)
                .contracts(documentDetails)
                .build();

        when(loanOfferStoreService.findById(loanOfferId)).thenReturn(Mono.just(loanOfferStore));

        final var response = identificationService.createIdentificationV2(userId, loanOfferId, applicationId);

        assertAll(
                () -> StepVerifier.create(response)
                        .expectSubscription()
                        .verifyError(),
                () -> verify(loanOfferStoreService).findById(loanOfferId),
                () -> verifyNoInteractions(identServiceFactory, consorsIdentService, identificationAuditService),
                () -> verifyNoMoreInteractions(loanOfferStoreService)
        );
    }
}
