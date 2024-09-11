package de.joonko.loan.identification.webid;

import de.joonko.loan.contract.ContractStorageService;
import de.joonko.loan.contract.model.DocumentDetails;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.identification.model.IdentificationLink;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.identification.service.webid.PostbankIdentService;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStore;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStoreService;
import de.joonko.loan.webhooks.postbank.model.ContractState;
import de.joonko.loan.webhooks.postbank.model.CreditResult;
import de.joonko.loan.webhooks.postbank.model.DebtorInformation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostbankIdentServiceTest {

    private PostbankIdentService identService;

    private PostbankLoanDemandStoreService postbankLoanDemandStoreService;
    private IdentificationLinkService identificationLinkService;
    private LoanOfferStoreService loanOfferStoreService;
    private ContractStorageService contractStorageService;


    @BeforeEach
    void setUp() {
        postbankLoanDemandStoreService = mock(PostbankLoanDemandStoreService.class);
        identificationLinkService = mock(IdentificationLinkService.class);
        loanOfferStoreService = mock(LoanOfferStoreService.class);
        contractStorageService = mock(ContractStorageService.class);
        identService = new PostbankIdentService(postbankLoanDemandStoreService, identificationLinkService, loanOfferStoreService, contractStorageService);
    }

    @Test
    void getCreateIdentWhenKycExists() {
        // given
        final var kycUrl = "https://test.webid-solutions.de/service/index/ti/123/cn/421/act/pass";
        final var applicationId = "dg27h39f83";
        final var loanOfferId = "h3f928f";
        final var createIdentRequest = CreateIdentRequest.builder()
                .applicationId(applicationId)
                .loanOfferId(loanOfferId)
                .loanProvider("POSTBANK")
                .build();
        final var documents = de.joonko.loan.identification.model.Documents.builder()
                .documents(List.of(
                        Document.builder()
                                .documentId("a")
                                .content("Soma random document".getBytes(StandardCharsets.UTF_8))
                                .build()
                ))
                .build();
        final var postBankStore = getPostbankLoanDemandStore(applicationId, kycUrl);
        when(postbankLoanDemandStoreService.findByApplicationId(applicationId)).thenReturn(Mono.just(postBankStore));
        when(contractStorageService.getContracts(postBankStore.getCreditResults().stream().map(CreditResult::getSavedContracts).flatMap(List::stream).collect(toList()))).thenReturn(Mono.just(documents));
        // when
        var actualCreateIdent = identService.createIdent(createIdentRequest);

        // then
        assertAll(
                () -> StepVerifier.create(actualCreateIdent)
                        .consumeNextWith(actual -> assertAll(
                                () -> assertEquals(kycUrl, actual.getKycUrl()),
                                () -> assertEquals(IdentificationProvider.WEB_ID, actual.getKycProvider()),
                                () -> assertEquals(documents, actual.getDocuments()),
                                () -> assertEquals(IdentificationProvider.WEB_ID, actual.getKycProvider())
                        )).verifyComplete(),
                () -> verify(postbankLoanDemandStoreService).findByApplicationId(applicationId),
                () -> verify(contractStorageService).getContracts(any(List.class)),
                () -> verify(identificationLinkService).add(applicationId, loanOfferId, "POSTBANK", IdentificationProvider.WEB_ID, applicationId, kycUrl)
        );
    }

    @Test
    void getCreateIdentWhenKycMissing() {
        // given
        final var applicationId = "dg27h39f83";
        final var createIdentRequest = CreateIdentRequest.builder()
                .applicationId(applicationId)
                .build();
        when(postbankLoanDemandStoreService.findByApplicationId(applicationId)).thenReturn(Mono.just(getPostbankLoanDemandStore(applicationId, null)));

        // when
        var actualCreateIdent = identService.createIdent(createIdentRequest);

        // then
        StepVerifier.create(actualCreateIdent).verifyError();
    }

    @Test
    void getCreateIdentWhenSuccessContractStateMissing() {
        // given
        final var applicationId = "dg27h39f83";
        final var createIdentRequest = CreateIdentRequest.builder()
                .applicationId(applicationId)
                .build();
        when(postbankLoanDemandStoreService.findByApplicationId(applicationId)).thenReturn(Mono.just(PostbankLoanDemandStore.builder()
                .creditResults(Set.of(CreditResult.builder()
                        .contractState(ContractState.MANUELL_ABGEWIESEN_94)
                        .build()))
                .build()));

        // when
        var actualCreateIdent = identService.createIdent(createIdentRequest);

        // then
        StepVerifier.create(actualCreateIdent).verifyError();
    }

    @Test
    void getIdentStatus() {
        // given
        final var externalIdentId = "3f8hj20f";
        final var loanOfferId = "3t23gf93";
        when(identificationLinkService.getIdentificationByExternalIdentId(externalIdentId)).thenReturn(Mono.just(IdentificationLink.builder()
                .offerId(loanOfferId)
                .build()));
        when(loanOfferStoreService.findById(loanOfferId)).thenReturn(Mono.just(LoanOfferStore.builder()
                .kycStatus("INITIATED")
                .build()));

        // when
        var actualIdentStatus = identService.getIdentStatus(externalIdentId);

        // then
        StepVerifier.create(actualIdentStatus)
                .consumeNextWith(actual -> assertAll(
                        () -> assertEquals("INITIATED", actual)
                )).verifyComplete();
    }

    public PostbankLoanDemandStore getPostbankLoanDemandStore(String applicationId, String kycUrl) {
        return PostbankLoanDemandStore.builder()
                .applicationId(applicationId)
                .creditResults(
                        Set.of(CreditResult.builder()
                                        .partnerContractNumber(applicationId)
                                        .contractState(ContractState.ONLINE_GENEHMIGT_24)
                                        .savedContracts(List.of(DocumentDetails.builder()
                                                .name("a")
                                                .key("b")
                                                .build()))
                                        .debtorInformation(DebtorInformation.builder()
                                                .digitaleSignaturUrl(kycUrl)
                                                .build())
                                        .build(),
                                CreditResult.builder()
                                        .partnerContractNumber(applicationId)
                                        .contractState(ContractState.UNTERLAGEN_EINGEGANGEN_25)
                                        .build()
                        )
                )
                .build();
    }
}
