package de.joonko.loan.idnow;

import de.joonko.loan.identification.config.AccountActiveConfig;
import de.joonko.loan.identification.config.IDNowAccountSetupConfig;
import de.joonko.loan.identification.model.idnow.DocumentDefinition;
import de.joonko.loan.identification.model.idnow.IdNowAccount;
import de.joonko.loan.identification.service.idnow.IdNowAccountMapper;
import de.joonko.loan.identification.service.idnow.IdNowClientApi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Set;

import static de.joonko.loan.idnow.IdNowAccountTestData.*;
import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdNowAccountSetupConfigTest {

    private IdNowClientApi idNowClientApi;
    private AccountActiveConfig accountActiveConfig;
    private IdNowAccountMapper idNowAccountMapper;

    private IDNowAccountSetupConfig accountSetupConfig;

    @BeforeEach
    void setUp() {
        idNowClientApi = mock(IdNowClientApi.class);
        accountActiveConfig = mock(AccountActiveConfig.class);
        idNowAccountMapper = mock(IdNowAccountMapper.class);
        accountSetupConfig = new IDNowAccountSetupConfig(idNowClientApi, accountActiveConfig, idNowAccountMapper);
    }

    @Test
    void doNotUploadDocumentDefinitionIfAlreadyExist() {
        // given
        when(accountActiveConfig.getActiveIdNowAccounts()).thenReturn(Set.of(IdNowAccount.AION, IdNowAccount.CONSORS));

        when(idNowClientApi.getJwtToken(any(IdNowAccount.class))).thenReturn(getIdNowJwtToken());
        when(idNowClientApi.getDocumentDefinitions(eq(IdNowAccount.CONSORS), anyString())).thenReturn(Mono.just(getArrayOfSingleDocumentDefinition()));
        when(idNowClientApi.getDocumentDefinitions(eq(IdNowAccount.AION), anyString())).thenReturn(Mono.just(getArrayOfMultipleDocumentDefinitions()));

        when(idNowAccountMapper.getDocumentDefinitions(IdNowAccount.CONSORS)).thenReturn(getSingleDocumentDefinition());
        when(idNowAccountMapper.getDocumentDefinitions(IdNowAccount.AION)).thenReturn(getMultipleDocumentDefinitions());

        // when
        accountSetupConfig.syncDocumentDefinitions();

        // then
        assertAll(
                () -> verify(idNowClientApi, times(2)).getJwtToken(any(IdNowAccount.class)),
                () -> verify(idNowClientApi, times(2)).getDocumentDefinitions(any(IdNowAccount.class), anyString()),
                () -> verifyNoMoreInteractions(idNowClientApi)
        );
    }

    @Test
    void uploadDocumentDefinitionIfMissing() {
        // given
        when(accountActiveConfig.getActiveIdNowAccounts()).thenReturn(Set.of(IdNowAccount.CONSORS, IdNowAccount.AION));

        when(idNowClientApi.getJwtToken(any(IdNowAccount.class))).thenReturn(getIdNowJwtToken());
        when(idNowClientApi.getDocumentDefinitions(eq(IdNowAccount.CONSORS), anyString())).thenReturn(Mono.just(getArrayOfSingleDocumentDefinition()));
        when(idNowClientApi.getDocumentDefinitions(eq(IdNowAccount.AION), anyString())).thenReturn(Mono.just(new DocumentDefinition[]{}));
        when(idNowClientApi.createDocumentDefinition(eq(IdNowAccount.AION), anyString(), any(DocumentDefinition.class))).thenReturn(Mono.empty());

        when(idNowAccountMapper.getDocumentDefinitions(IdNowAccount.CONSORS)).thenReturn(getSingleDocumentDefinition());
        when(idNowAccountMapper.getDocumentDefinitions(IdNowAccount.AION)).thenReturn(getMultipleDocumentDefinitions());


        // when
        accountSetupConfig.syncDocumentDefinitions();

        // then
        assertAll(
                () -> verify(idNowClientApi, times(2)).getJwtToken(any(IdNowAccount.class)),
                () -> verify(idNowClientApi, times(2)).getDocumentDefinitions(any(IdNowAccount.class), anyString()),
                () -> verify(idNowClientApi, times(2)).createDocumentDefinition(any(IdNowAccount.class), anyString(), any(DocumentDefinition.class))
        );
    }

    @Test
    void doNotUploadWhenNoAccountsActive() {
        // given
        when(accountActiveConfig.getActiveIdNowAccounts()).thenReturn(emptySet());

        // when
        accountSetupConfig.syncDocumentDefinitions();

        // then
        verifyNoInteractions(idNowClientApi);
    }
}
