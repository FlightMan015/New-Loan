package de.joonko.loan.identification.service.idnow;

import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.identification.mapper.idnow.ConsorsCreateIdentRequestMapper;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.idnow.IdNowAccount;
import de.joonko.loan.identification.service.IdentificationAuditService;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.partner.consors.ConsorsContractGateway;
import de.joonko.loan.partner.consors.ConsorsStoreService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.joonko.loan.identification.service.idnow.testdata.IdNowIdentServiceTestData.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConsorsIDNowIdentServiceTest {

    private static IDNowIdentService idNowIdentService;

    private static IdNowClientApi idNowClientApi;
    private static ConsorsCreateIdentRequestMapper consorsCreateIdentRequestMapper;
    private static IdentificationPropConfig identificationPropConfig;
    private static ConsorsContractGateway consorsContractGateway;
    private static IdentificationAuditService identificationService;
    private static ConsorsStoreService consorsStoreService;
    private static IdentificationLinkService identificationLinkService;

    private static final String IDENT_ID = "123456";
    private static final String APPLICATION_ID = "37825690235";

    @BeforeAll
    static void beforeAll() {
        idNowClientApi = mock(IdNowClientApi.class);
        consorsCreateIdentRequestMapper = mock(ConsorsCreateIdentRequestMapper.class);
        identificationPropConfig = mock(IdentificationPropConfig.class);
        consorsContractGateway = mock(ConsorsContractGateway.class);
        identificationService = mock(IdentificationAuditService.class);
        consorsStoreService = mock(ConsorsStoreService.class);
        identificationLinkService = mock(IdentificationLinkService.class);

        idNowIdentService = new ConsorsIdentService(idNowClientApi, consorsCreateIdentRequestMapper, identificationPropConfig,
                consorsContractGateway, identificationService, consorsStoreService, identificationLinkService);

        when(identificationPropConfig.getConsorsAccountId()).thenReturn("FAKE_ACCOUNT_ID");
        when(identificationPropConfig.getConsorsApiKey()).thenReturn("FAKE_API_KEY");
    }

    @Test
    void getValidIdentStatus() {
        // given
        when(idNowClientApi.getJwtToken(IdNowAccount.CONSORS)).thenReturn(Mono.just(getIdNowJwtToken()));
        when(idNowClientApi.getIdent(eq(IdNowAccount.CONSORS), anyString(), anyString())).thenReturn(Mono.just(getIdentResponse()));

        // when
        var monoIdentStatus = idNowIdentService.getIdentStatus(IDENT_ID);

        // then
        StepVerifier.create(monoIdentStatus)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void get401WhenGettingIdentStatus() {
        // given
        when(idNowClientApi.getJwtToken(IdNowAccount.CONSORS)).thenReturn(getUnauthorizedExceptionFromWebClient());

        // when
        var monoIdentStatus = idNowIdentService.getIdentStatus(IDENT_ID);

        // then
        StepVerifier.create(monoIdentStatus)
                .verifyErrorMessage("401 Unauthorized exception");
    }

    @Test
    void getCancelledWhenIdentStatusNotFound() {
        // given
        when(idNowClientApi.getJwtToken(IdNowAccount.CONSORS)).thenReturn(Mono.just(getIdNowJwtToken()));
        when(idNowClientApi.getIdent(eq(IdNowAccount.CONSORS), anyString(), anyString())).thenReturn(getNotFoundExceptionFromWebClient());

        // when
        var monoIdentStatus = idNowIdentService.getIdentStatus(IDENT_ID);

        // then
        StepVerifier.create(monoIdentStatus)
                .expectNext("CANCELLED")
                .verifyComplete();
    }

    @Test
    void getDocuments() {
        // given
        var createIdentRequest = CreateIdentRequest.builder()
                .applicationId(APPLICATION_ID)
                .build();
        var documentContent = new byte[]{};
        when(consorsContractGateway.getContractForLoanApplicationId(APPLICATION_ID)).thenReturn(Mono.just(documentContent));

        // when
        var monoDocuments = idNowIdentService.getDocuments(createIdentRequest);

        // then
        StepVerifier.create(monoDocuments).consumeNextWith(actualDocuments -> assertAll(
                () -> assertEquals(1, actualDocuments.getDocuments().size()),
                () -> assertEquals("contract", actualDocuments.getDocuments().get(0).getDocumentId()),
                () -> assertEquals(documentContent, actualDocuments.getDocuments().get(0).getContent())
        )).verifyComplete();
    }
}
