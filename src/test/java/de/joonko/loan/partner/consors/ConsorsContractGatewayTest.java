package de.joonko.loan.partner.consors;

import de.joonko.loan.common.partner.consors.auth.JwtToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

class ConsorsContractGatewayTest {

    private ConsorsContractGateway consorsContractGateway;

    private ConsorsStoreService consorsStoreService;
    private ConsorsClient consorsClient;

    @BeforeEach
    void setUp() {
        consorsStoreService = mock(ConsorsStoreService.class);
        consorsClient = mock(ConsorsClient.class);

        consorsContractGateway = new ConsorsContractGateway(consorsStoreService, consorsClient);
    }

    @Test
    void getContract() {
        // given
        final var applicationId = "382j9f823f";
        final var downloadUrl = "/subscription/3hf9238fj293/documents?version=5.0";
        final var jwtToken = new JwtToken("jwtToken");
        when(consorsStoreService.getDownloadSubscriptionDocumentLinkForApplicationId(applicationId)).thenReturn(downloadUrl);
        when(consorsClient.getToken(applicationId)).thenReturn(Mono.just(jwtToken));
        when(consorsClient.getContract(jwtToken, downloadUrl, applicationId)).thenReturn(Mono.just(new byte[]{0}));

        // when
        var actualContract = consorsContractGateway.getContractForLoanApplicationId(applicationId);

        // then
        assertAll(
                () -> StepVerifier.create(actualContract).expectNextCount(1).verifyComplete(),
                () -> verify(consorsStoreService).getDownloadSubscriptionDocumentLinkForApplicationId(applicationId),
                () -> verify(consorsClient).getToken(applicationId),
                () -> verify(consorsClient).getContract(jwtToken, downloadUrl, applicationId)
        );
    }
}
