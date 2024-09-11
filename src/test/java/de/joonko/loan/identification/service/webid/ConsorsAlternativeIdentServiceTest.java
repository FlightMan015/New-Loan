package de.joonko.loan.identification.service.webid;

import de.joonko.loan.contract.ContractStorageService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.CreateIdentResponse;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.partner.consors.ConsorsContractGateway;
import de.joonko.loan.partner.consors.ConsorsStoreService;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStoreService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConsorsAlternativeIdentServiceTest {

    private ConsorsAlternativeIdentService identService;

    private ConsorsStoreService consorsStoreService;
    private IdentificationLinkService identificationLinkService;
    private LoanOfferStoreService loanOfferStoreService;
    private ConsorsContractGateway consorsContractGateway;

    @BeforeEach
    void setUp() {
        consorsStoreService = mock(ConsorsStoreService.class);
        identificationLinkService = mock(IdentificationLinkService.class);
        loanOfferStoreService = mock(LoanOfferStoreService.class);
        consorsContractGateway = mock(ConsorsContractGateway.class);
        identService = new ConsorsAlternativeIdentService(consorsStoreService, identificationLinkService, loanOfferStoreService, consorsContractGateway);
    }

    @Test
    void createIdent() {
        // given
        final var applicationId = "a";
        final var kycLink = "kycLink";
        final var identRequest = CreateIdentRequest.builder()
                .applicationId(applicationId)
                .loanOfferId("b")
                .build();

        // when
        when(consorsStoreService.getKYCLinkForApplicationId(applicationId)).thenReturn(Mono.just("kycLink"));
        when(consorsContractGateway.getContractForLoanApplicationId(applicationId)).thenReturn(Mono.just(new byte[]{}));

        Mono<CreateIdentResponse> identResponse = identService.createIdent(identRequest);
        // then
        assertAll(
                () -> StepVerifier.create(identResponse).expectNextMatches(res ->
                        kycLink.equals(res.getKycUrl()) &&
                                IdentificationProvider.WEB_ID.equals(res.getKycProvider()) &&
                                res.getDocuments().getDocuments().size() == 1
                ).verifyComplete(),
                () -> verify(consorsStoreService).getKYCLinkForApplicationId(applicationId),
                () -> verify(consorsContractGateway).getContractForLoanApplicationId(applicationId),
                () -> verify(identificationLinkService).add(identRequest.getApplicationId(), identRequest.getLoanOfferId(), identRequest.getLoanProvider(), IdentificationProvider.WEB_ID, identRequest.getApplicationId(), kycLink)
        );
    }
}