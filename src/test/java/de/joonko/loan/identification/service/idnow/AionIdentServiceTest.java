package de.joonko.loan.identification.service.idnow;

import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.identification.mapper.idnow.AionCreateIdentRequestMapper;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.model.idnow.IdNowAccount;
import de.joonko.loan.identification.service.IdentificationAuditService;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.partner.aion.AionContractGateway;
import de.joonko.loan.partner.aion.AionStoreService;
import de.joonko.loan.partner.aion.model.CreditApplicationResponseStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static de.joonko.loan.identification.service.idnow.testdata.IdNowIdentServiceTestData.getCreateIdentResponse;
import static de.joonko.loan.identification.service.idnow.testdata.IdNowIdentServiceTestData.getIdNowJwtToken;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

class AionIdentServiceTest {

    private IDNowIdentService aionIdentService;

    private IdNowClientApi idNowClientApi;
    private AionCreateIdentRequestMapper aionCreateIdentRequestMapper;
    private AionContractGateway aionContractGateway;
    private AionStoreService aionStoreService;
    protected IdentificationAuditService identificationAuditService;
    private IdentificationPropConfig identificationPropConfig;
    private IdentificationLinkService identificationLinkService;

    private static final String APPLICATION_ID = "76239586";
    private static final String REPRESENTATIVE_ID = "53f3354d-d04c-4a48-a68b-114bd8baba6e";

    @BeforeEach
    void setUp() {
        identificationAuditService = mock(IdentificationAuditService.class);
        identificationPropConfig = mock(IdentificationPropConfig.class);
        identificationLinkService = mock(IdentificationLinkService.class);
        idNowClientApi = mock(IdNowClientApi.class);
        aionContractGateway = mock(AionContractGateway.class);
        aionStoreService = mock(AionStoreService.class);
        aionCreateIdentRequestMapper = mock(AionCreateIdentRequestMapper.class);
        aionIdentService = new AionIdentService(idNowClientApi, identificationPropConfig, identificationAuditService, aionCreateIdentRequestMapper,
                identificationLinkService, aionContractGateway, aionStoreService);
    }

    @Test
    void getIdentRequest() {
        // given
        var createIdentRequest = CreateIdentRequest.builder()
                .applicationId(APPLICATION_ID)
                .build();
        when(aionStoreService.findByApplicationId(APPLICATION_ID)).thenReturn(Mono.just(Optional.of(CreditApplicationResponseStore.builder()
                .representativeId(REPRESENTATIVE_ID)
                .build())));
        when(aionCreateIdentRequestMapper.toIdNowCreateIdentRequest(createIdentRequest, REPRESENTATIVE_ID)).thenReturn(new de.joonko.loan.identification.model.idnow.CreateIdentRequest());


        // when
        var monoIdentRequest = aionIdentService.getIdentRequest(createIdentRequest);

        // then
        assertAll(
                () -> StepVerifier.create(monoIdentRequest).expectNextCount(1).verifyComplete(),
                () -> verify(aionStoreService).findByApplicationId(APPLICATION_ID),
                () -> verify(aionCreateIdentRequestMapper).toIdNowCreateIdentRequest(createIdentRequest, REPRESENTATIVE_ID)
        );
    }

    @Test
    void getErrorWhenRepresentativeIdMissing() {
        // given
        var createIdentRequest = CreateIdentRequest.builder()
                .applicationId(APPLICATION_ID)
                .build();
        when(aionStoreService.findByApplicationId(APPLICATION_ID)).thenReturn(Mono.just(Optional.of(CreditApplicationResponseStore.builder().build())));

        // when
        var monoIdentRequest = aionIdentService.getIdentRequest(createIdentRequest);

        // then
        assertAll(
                () -> StepVerifier.create(monoIdentRequest).verifyError(),
                () -> verify(aionStoreService).findByApplicationId(anyString()),
                () -> verifyNoInteractions(aionCreateIdentRequestMapper)
        );
    }

    @Test
    void getDocuments() {
        // given
        var createIdentRequest = CreateIdentRequest.builder()
                .applicationId(APPLICATION_ID)
                .build();
        when(aionContractGateway.getDocuments(APPLICATION_ID)).thenReturn(Mono.just(Documents.builder().build()));

        // when
        var monoDocuments = aionIdentService.getDocuments(createIdentRequest);


        // then
        assertAll(
                () -> StepVerifier.create(monoDocuments).expectNextCount(1).verifyComplete(),
                () -> verify(aionContractGateway).getDocuments(APPLICATION_ID)
        );
    }

    @Test
    void createIdent() {
        // given
        var createIdentRequest = CreateIdentRequest.builder()
                .applicationId(APPLICATION_ID)
                .build();
        when(idNowClientApi.getJwtToken(IdNowAccount.AION)).thenReturn(Mono.just(getIdNowJwtToken()));
        when(idNowClientApi.createIdent(eq(IdNowAccount.AION), anyString(), anyString(), any(de.joonko.loan.identification.model.idnow.CreateIdentRequest.class)))
                .thenReturn(Mono.just(getCreateIdentResponse()));
        when(aionStoreService.findByApplicationId(APPLICATION_ID)).thenReturn(Mono.just(Optional.of(CreditApplicationResponseStore.builder()
                .representativeId(REPRESENTATIVE_ID)
                .build())));
        when(aionCreateIdentRequestMapper.toIdNowCreateIdentRequest(createIdentRequest, REPRESENTATIVE_ID)).thenReturn(new de.joonko.loan.identification.model.idnow.CreateIdentRequest());
        when(aionContractGateway.getDocuments(anyString())).thenReturn(Mono.just(Documents.builder()
                .documents(List.of(
                        Document.builder()
                                .content(new byte[]{})
                                .documentId("agreement")
                                .build(),
                        Document.builder()
                                .content(new byte[]{})
                                .documentId("schedule")
                                .build()
                ))
                .build()));
        when(idNowClientApi.uploadDocument(eq(IdNowAccount.AION), anyString(), anyString(), any(Document.class))).thenReturn(Mono.empty());


        // when
        var monoCreateIdent = aionIdentService.createIdent(createIdentRequest);

        // then
        assertAll(
                () -> StepVerifier.create(monoCreateIdent).expectNextCount(1).verifyComplete(),
                () -> verify(idNowClientApi).getJwtToken(IdNowAccount.AION),
                () -> verify(idNowClientApi).createIdent(eq(IdNowAccount.AION), anyString(), anyString(), any(de.joonko.loan.identification.model.idnow.CreateIdentRequest.class)),
                () -> verify(identificationAuditService).identCreatedSuccess(anyString(), any(CreateIdentRequest.class), eq(IdentificationProvider.ID_NOW)),
                () -> verify(identificationLinkService).add(anyString(), any(), any(), eq(IdentificationProvider.ID_NOW), anyString(), anyString()),
                () -> verify(aionContractGateway).getDocuments(anyString()),
                () -> verify(idNowClientApi, times(2)).uploadDocument(eq(IdNowAccount.AION), anyString(), anyString(), any(Document.class))
        );
    }

    @Test
    void getErrorWhenFailedGettingCreateIdent() {
        // given
        var createIdentRequest = CreateIdentRequest.builder()
                .applicationId(APPLICATION_ID)
                .build();
        when(idNowClientApi.getJwtToken(IdNowAccount.AION)).thenReturn(Mono.just(getIdNowJwtToken()));
        when(idNowClientApi.createIdent(eq(IdNowAccount.AION), anyString(), anyString(), any(de.joonko.loan.identification.model.idnow.CreateIdentRequest.class)))
                .thenReturn(Mono.error(RuntimeException::new));
        when(aionStoreService.findByApplicationId(APPLICATION_ID)).thenReturn(Mono.just(Optional.of(CreditApplicationResponseStore.builder()
                .representativeId(REPRESENTATIVE_ID)
                .build())));
        when(aionCreateIdentRequestMapper.toIdNowCreateIdentRequest(createIdentRequest, REPRESENTATIVE_ID)).thenReturn(new de.joonko.loan.identification.model.idnow.CreateIdentRequest());

        // when
        var monoCreateIdent = aionIdentService.createIdent(createIdentRequest);

        // then
        assertAll(
                () -> StepVerifier.create(monoCreateIdent).verifyError(),
                () -> verify(idNowClientApi).getJwtToken(IdNowAccount.AION),
                () -> verify(idNowClientApi).createIdent(eq(IdNowAccount.AION), anyString(), anyString(), any(de.joonko.loan.identification.model.idnow.CreateIdentRequest.class)),
                () -> verify(identificationAuditService).identCreationFailure(any(RuntimeException.class), any(CreateIdentRequest.class), eq(IdentificationProvider.ID_NOW)),
                () -> verifyNoInteractions(aionContractGateway),
                () -> verifyNoMoreInteractions(idNowClientApi)
        );
    }

    @Test
    void getErrorWhenFailedGettingBankDocument() {
        // given
        var createIdentRequest = CreateIdentRequest.builder()
                .applicationId(APPLICATION_ID)
                .build();
        when(idNowClientApi.getJwtToken(IdNowAccount.AION)).thenReturn(Mono.just(getIdNowJwtToken()));
        when(idNowClientApi.createIdent(eq(IdNowAccount.AION), anyString(), anyString(), any(de.joonko.loan.identification.model.idnow.CreateIdentRequest.class)))
                .thenReturn(Mono.just(getCreateIdentResponse()));
        when(aionStoreService.findByApplicationId(APPLICATION_ID)).thenReturn(Mono.just(Optional.of(CreditApplicationResponseStore.builder()
                .representativeId(REPRESENTATIVE_ID)
                .build())));
        when(aionCreateIdentRequestMapper.toIdNowCreateIdentRequest(createIdentRequest, REPRESENTATIVE_ID)).thenReturn(new de.joonko.loan.identification.model.idnow.CreateIdentRequest());
        when(aionContractGateway.getDocuments(anyString())).thenReturn(Mono.error(RuntimeException::new));

        // when
        var monoCreateIdent = aionIdentService.createIdent(createIdentRequest);

        // then
        assertAll(
                () -> StepVerifier.create(monoCreateIdent).verifyError(),
                () -> verify(idNowClientApi).getJwtToken(IdNowAccount.AION),
                () -> verify(idNowClientApi).createIdent(eq(IdNowAccount.AION), anyString(), anyString(), any(de.joonko.loan.identification.model.idnow.CreateIdentRequest.class)),
                () -> verify(identificationAuditService).identCreatedSuccess(anyString(), any(CreateIdentRequest.class), eq(IdentificationProvider.ID_NOW)),
                () -> verify(identificationLinkService).add(anyString(), any(), any(), eq(IdentificationProvider.ID_NOW), anyString(), anyString()),
                () -> verify(aionContractGateway).getDocuments(anyString()),
                () -> verifyNoMoreInteractions(idNowClientApi),
                () -> verify(identificationAuditService).contractUploadFail(any(CreateIdentRequest.class), any(), eq(IdentificationProvider.ID_NOW))
        );
    }

    @Test
    void getErrorWhenFailedUploadingDocumentsToIdNow() {
        // given
        var createIdentRequest = CreateIdentRequest.builder()
                .applicationId(APPLICATION_ID)
                .build();
        when(idNowClientApi.getJwtToken(IdNowAccount.AION)).thenReturn(Mono.just(getIdNowJwtToken()));
        when(idNowClientApi.createIdent(eq(IdNowAccount.AION), anyString(), anyString(), any(de.joonko.loan.identification.model.idnow.CreateIdentRequest.class)))
                .thenReturn(Mono.just(getCreateIdentResponse()));
        when(aionStoreService.findByApplicationId(APPLICATION_ID)).thenReturn(Mono.just(Optional.of(CreditApplicationResponseStore.builder()
                .representativeId(REPRESENTATIVE_ID)
                .build())));
        when(aionCreateIdentRequestMapper.toIdNowCreateIdentRequest(createIdentRequest, REPRESENTATIVE_ID)).thenReturn(new de.joonko.loan.identification.model.idnow.CreateIdentRequest());
        when(aionContractGateway.getDocuments(anyString())).thenReturn(Mono.just(Documents.builder()
                .documents(List.of(
                        Document.builder()
                                .content(new byte[]{})
                                .documentId("agreement")
                                .build(),
                        Document.builder()
                                .content(new byte[]{})
                                .documentId("schedule")
                                .build()
                ))
                .build()));
        when(idNowClientApi.uploadDocument(eq(IdNowAccount.AION), anyString(), anyString(), any(Document.class)))
                .thenReturn(Mono.empty())
                .thenReturn(Mono.error(RuntimeException::new));


        // when
        var monoCreateIdent = aionIdentService.createIdent(createIdentRequest);

        // then
        assertAll(
                () -> StepVerifier.create(monoCreateIdent).verifyError(),
                () -> verify(idNowClientApi).getJwtToken(IdNowAccount.AION),
                () -> verify(idNowClientApi).createIdent(eq(IdNowAccount.AION), anyString(), anyString(), any(de.joonko.loan.identification.model.idnow.CreateIdentRequest.class)),
                () -> verify(identificationAuditService).identCreatedSuccess(anyString(), any(CreateIdentRequest.class), eq(IdentificationProvider.ID_NOW)),
                () -> verify(identificationLinkService).add(anyString(), any(), any(), eq(IdentificationProvider.ID_NOW), anyString(), anyString()),
                () -> verify(aionContractGateway).getDocuments(anyString()),
                () -> verify(idNowClientApi, times(2)).uploadDocument(eq(IdNowAccount.AION), anyString(), anyString(), any(Document.class)),
                () -> verify(identificationAuditService).contractUploadFail(any(CreateIdentRequest.class), any(), eq(IdentificationProvider.ID_NOW))
        );
    }
}
