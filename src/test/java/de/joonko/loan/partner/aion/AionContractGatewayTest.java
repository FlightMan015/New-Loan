package de.joonko.loan.partner.aion;

import de.joonko.loan.partner.aion.model.AionAuthToken;
import de.joonko.loan.partner.aion.testdata.AionContractGatewayTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AionContractGatewayTest {

    private AionContractGateway contractGateway;

    private AionClient aionClient;
    private AionStoreService aionStoreService;

    private static AionContractGatewayTestData testData;

    private static final String APPLICATION_ID = "739562953023";
    private static final String PROCESS_ID = "95c72259-4209-4e47-8205-98e651ce46d6";

    @BeforeEach
    void setUp() {
        testData = new AionContractGatewayTestData();

        aionClient = mock(AionClient.class);
        aionStoreService = mock(AionStoreService.class);

        contractGateway = new AionContractGateway(aionClient, aionStoreService);
    }

    @Test
    void getAllDocuments() {
        // given
        var offerChoiceResponse = testData.getOfferChoiceResponse();
        var authToken = AionAuthToken.builder().build();
        when(aionStoreService.findByApplicationId(APPLICATION_ID)).thenReturn(Mono.just(testData.getCreditApplicationResponseStore(PROCESS_ID)));
        when(aionClient.getToken(APPLICATION_ID)).thenReturn(Mono.just(authToken));
        when(aionClient.getOfferStatus(authToken, PROCESS_ID)).thenReturn(Mono.just(offerChoiceResponse));

        // when
        var monoDocuments = contractGateway.getDocuments(APPLICATION_ID);

        // then
        assertAll(
                () -> StepVerifier.create(monoDocuments).expectNextCount(1).verifyComplete(),
                () -> verify(aionStoreService).findByApplicationId(APPLICATION_ID),
                () -> verify(aionClient).getToken(APPLICATION_ID),
                () -> verify(aionClient).getOfferStatus(any(AionAuthToken.class), eq(PROCESS_ID))
        );
    }

    @Test
    void getErrorWhenMissingCreditApplicationStore() {
        // given
        when(aionStoreService.findByApplicationId(APPLICATION_ID)).thenReturn(Mono.just(Optional.empty()));

        // when
        var monoDocuments = contractGateway.getDocuments(APPLICATION_ID);

        // then
        assertAll(
                () -> StepVerifier.create(monoDocuments).verifyError(),
                () -> verify(aionStoreService).findByApplicationId(APPLICATION_ID),
                () -> verifyNoInteractions(aionClient)
        );
    }

    @Test
    void getErrorWhenFailedToGetToken() {
        // given
        when(aionStoreService.findByApplicationId(APPLICATION_ID)).thenReturn(Mono.just(testData.getCreditApplicationResponseStore(PROCESS_ID)));
        when(aionClient.getToken(APPLICATION_ID)).thenReturn(Mono.error(RuntimeException::new));

        // when
        var monoDocuments = contractGateway.getDocuments(APPLICATION_ID);

        // then
        assertAll(
                () -> StepVerifier.create(monoDocuments).verifyError(),
                () -> verify(aionStoreService).findByApplicationId(APPLICATION_ID),
                () -> verify(aionClient).getToken(APPLICATION_ID),
                () -> verifyNoMoreInteractions(aionClient)
        );
    }

    @Test
    void getErrorWhenFailedGettingOfferStatus() {
        // given
        var authToken = AionAuthToken.builder().build();
        when(aionStoreService.findByApplicationId(APPLICATION_ID)).thenReturn(Mono.just(testData.getCreditApplicationResponseStore(PROCESS_ID)));
        when(aionClient.getToken(APPLICATION_ID)).thenReturn(Mono.just(authToken));
        when(aionClient.getOfferStatus(authToken, PROCESS_ID)).thenReturn(Mono.error(RuntimeException::new));

        // when
        var monoDocuments = contractGateway.getDocuments(APPLICATION_ID);

        // then
        assertAll(
                () -> StepVerifier.create(monoDocuments).verifyError(),
                () -> verify(aionStoreService).findByApplicationId(APPLICATION_ID),
                () -> verify(aionClient).getToken(APPLICATION_ID),
                () -> verify(aionClient).getOfferStatus(any(AionAuthToken.class), eq(PROCESS_ID))
        );
    }
}
