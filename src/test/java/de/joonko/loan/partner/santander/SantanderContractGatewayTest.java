package de.joonko.loan.partner.santander;

import de.joonko.loan.identification.model.webid.useractionresponse.CreateUserActionResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SantanderContractGatewayTest {

    private SantanderContractGateway santanderContractGateway;

    private SantanderClientApi santanderClientApi;

    private static final String SCB_ANTRAG_ID = "6983753";
    private static final String LOAN_OFFER_ID = "198652";

    @BeforeEach
    void setUp() {
        santanderClientApi = mock(SantanderClientApi.class);
        santanderContractGateway = new SantanderContractGateway(santanderClientApi);
    }

    @Test
    void doNotGetContractWhenFailedCreatingContractEntry() {
        // given
        CreateUserActionResponse createUserActionResponse = getValidCreateUserActionResponse();
        when(santanderClientApi.createContractEntry(anyString(), anyString())).thenReturn(Mono.error(new RuntimeException()));

        // when
        var contract = santanderContractGateway.fetchContract(SCB_ANTRAG_ID, LOAN_OFFER_ID, createUserActionResponse, false);

        // then
        StepVerifier.create(contract)
                .verifyError();
    }

    @Test
    void getContractWhenContractEntryCreated() {
        // given
        CreateUserActionResponse createUserActionResponse = getValidCreateUserActionResponse();
        when(santanderClientApi.createContractEntry(anyString(), anyString())).thenReturn(Mono.just(ResponseEntity.ok().build()));
        when(santanderClientApi.getContract(anyString(), anyBoolean())).thenReturn(new byte[]{});

        // when
        var contract = santanderContractGateway.fetchContract(SCB_ANTRAG_ID, LOAN_OFFER_ID, createUserActionResponse, false);

        // then
        StepVerifier.create(contract)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void throwErrorWhenFailedGettingContract() {
        // given
        CreateUserActionResponse createUserActionResponse = getValidCreateUserActionResponse();
        when(santanderClientApi.createContractEntry(anyString(), anyString())).thenReturn(Mono.just(ResponseEntity.ok().build()));
        when(santanderClientApi.getContract(anyString(), anyBoolean())).thenThrow(RuntimeException.class);

        // when
        var contract = santanderContractGateway.fetchContract(SCB_ANTRAG_ID, LOAN_OFFER_ID, createUserActionResponse, false);

        // then
        StepVerifier.create(contract)
                .verifyError();
    }

    private CreateUserActionResponse getValidCreateUserActionResponse() {
        return CreateUserActionResponse.builder()
                .actionId("actionId")
                .transactionId("transactionId")
                .build();
    }
}
