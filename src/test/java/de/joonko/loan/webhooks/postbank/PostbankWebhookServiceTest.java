package de.joonko.loan.webhooks.postbank;

import de.joonko.loan.acceptoffer.api.OfferRequestMapper;
import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.contract.ContractStorageService;
import de.joonko.loan.contract.model.DocumentDetails;
import de.joonko.loan.db.service.LoanDemandRequestService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.domain.OfferStatusUpdateService;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStore;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStoreService;
import de.joonko.loan.webhooks.postbank.model.ContractState;
import de.joonko.loan.webhooks.postbank.model.CreditResult;
import de.joonko.loan.webhooks.postbank.model.CreditResultWithContracts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.joonko.loan.util.Base64Encoder.encodeToBase64AsString;
import static de.joonko.loan.webhooks.postbank.model.ContractState.DIGITALE_SIGNATUR_EINGEGANGEN_80;
import static de.joonko.loan.webhooks.postbank.model.ContractState.ONLINE_GENEHMIGT_24;
import static de.joonko.loan.webhooks.postbank.model.ContractState.ONLINE_GENEHMIGT_UND_AUSBEZAHLT_99;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(RandomBeansExtension.class)
class PostbankWebhookServiceTest {

    private PostbankWebhookService postbankWebhookService;

    private PostbankLoanDemandStoreService postbankLoanDemandStoreService;

    private LoanOfferStoreService loanOfferStoreService;

    private OfferStatusUpdateService offerStatusUpdateService;

    private OfferRequestMapper offerRequestMapper;

    private CreditResultMapper creditResultMapper;

    private ContractStorageService contractStorageService;

    private LoanDemandRequestService loanDemandRequestService;

    @BeforeEach
    void setUp() {
        postbankLoanDemandStoreService = mock(PostbankLoanDemandStoreService.class);
        loanOfferStoreService = mock(LoanOfferStoreService.class);
        offerStatusUpdateService = mock(OfferStatusUpdateService.class);
        offerRequestMapper = mock(OfferRequestMapper.class);
        creditResultMapper = mock(CreditResultMapper.class);
        contractStorageService = mock(ContractStorageService.class);
        loanDemandRequestService = mock(LoanDemandRequestService.class);
        postbankWebhookService = new PostbankWebhookService(postbankLoanDemandStoreService, loanOfferStoreService, offerStatusUpdateService, offerRequestMapper, creditResultMapper, contractStorageService, loanDemandRequestService);
    }

    @Test
    void testSuccessfulLoanOfferCase(@Random CreditResultWithContracts creditResultWithContracts) {
        // given
        final var now = LocalDateTime.now();
        creditResultWithContracts.setContractState(ONLINE_GENEHMIGT_24);
        final var mainContract = "This is the main contract";
        final var shortContract = "This is the short contract";
        creditResultWithContracts.setContract(encodeToBase64AsString(mainContract.getBytes()));
        creditResultWithContracts.setContractShort(encodeToBase64AsString(shortContract.getBytes()));
        final var creditResult = CreditResult.builder().build();
        final var loanDemand = LoanDemandRequest.builder()
                .userUUID(randomUUID().toString())
                .applicationId(randomUUID().toString())
                .build();
        final var documentDetails = List.of(
                DocumentDetails.builder()
                        .name("Main_Contract")
                        .key("a")
                        .build(),
                DocumentDetails.builder()
                        .name("Short_Contract")
                        .key("b")
                        .build()

        );

        final var argumentCaptorForStoreContracts = ArgumentCaptor.forClass(Documents.class);
        final var argumentCaptorForCreditResult = ArgumentCaptor.forClass(CreditResult.class);


        // when
        when(creditResultMapper.toCreditResult(creditResultWithContracts)).thenReturn(creditResult);
        when(loanDemandRequestService.findLoanDemandRequest(creditResultWithContracts.getPartnerContractNumber())).thenReturn(Mono.just(loanDemand));
        when(contractStorageService.storeContracts(any(Documents.class), eq(loanDemand.getUserUUID()), eq(loanDemand.getApplicationId()), eq(creditResultWithContracts.getContractNumber().toString()))).thenReturn(Mono.just(documentDetails));
        when(postbankLoanDemandStoreService.addOffersResponse(eq(creditResultWithContracts.getPartnerContractNumber()), any(CreditResult.class))).thenReturn(Mono.just(PostbankLoanDemandStore.builder().build()));

        final var result = postbankWebhookService.savePostbankOfferResponse(creditResultWithContracts);

        assertAll(
                () -> StepVerifier.create(result).expectNextCount(1).verifyComplete(),
                () -> verify(creditResultMapper).toCreditResult(creditResultWithContracts),
                () -> verify(loanDemandRequestService).findLoanDemandRequest(creditResultWithContracts.getPartnerContractNumber()),
                () -> verify(contractStorageService).storeContracts(argumentCaptorForStoreContracts.capture(), eq(loanDemand.getUserUUID()), eq(loanDemand.getApplicationId()), eq(creditResultWithContracts.getContractNumber().toString())),
                () -> assertEquals(Set.of(mainContract, shortContract), argumentCaptorForStoreContracts.getValue().getDocuments().stream().map(Document::getContent).map(String::new).collect(toSet())),
                () -> verify(postbankLoanDemandStoreService).addOffersResponse(eq(creditResultWithContracts.getPartnerContractNumber()), argumentCaptorForCreditResult.capture()),
                () -> assertEquals(documentDetails, argumentCaptorForCreditResult.getValue().getSavedContracts())
        );
    }

    @Test
    void testSuccessfullyPassedKYCCase(@Random CreditResultWithContracts creditResultWithContracts) {
        // given
        final var creditResult = CreditResult.builder().build();

        creditResultWithContracts.setContractState(DIGITALE_SIGNATUR_EINGEGANGEN_80);
        creditResultWithContracts.setContract(null);
        creditResultWithContracts.setContractShort(null);
        final var loanOffer = LoanOfferStore.builder().build();

        // when
        when(creditResultMapper.toCreditResult(creditResultWithContracts)).thenReturn(creditResult);
        when(postbankLoanDemandStoreService.addOffersResponse(creditResultWithContracts.getPartnerContractNumber(), creditResult)).thenReturn(Mono.just(PostbankLoanDemandStore.builder().build()));
        when(loanOfferStoreService.getSingleLoanOfferForLoanProviderReferenceNumber(creditResultWithContracts.getContractNumber().toString())).thenReturn(Mono.just(loanOffer));
        when(offerStatusUpdateService.updateKycStatus(loanOffer, LoanApplicationStatus.SUCCESS)).thenReturn(Mono.just(loanOffer));

        final var result = postbankWebhookService.savePostbankOfferResponse(creditResultWithContracts);

        assertAll(
                () -> StepVerifier.create(result).expectNextCount(1).verifyComplete(),
                () -> verify(creditResultMapper).toCreditResult(creditResultWithContracts),
                () -> verify(postbankLoanDemandStoreService).addOffersResponse(creditResultWithContracts.getPartnerContractNumber(), creditResult),
                () -> verify(loanOfferStoreService).getSingleLoanOfferForLoanProviderReferenceNumber(creditResultWithContracts.getContractNumber().toString()),
                () -> verify(offerStatusUpdateService).updateKycStatus(loanOffer, LoanApplicationStatus.SUCCESS)
        );
    }


    @Test
    void testSuccessfullyPaidOutLoanCase(@Random CreditResultWithContracts creditResultWithContracts) {
        // given
        final var creditResult = CreditResult.builder().build();
        creditResultWithContracts.setContractState(ONLINE_GENEHMIGT_UND_AUSBEZAHLT_99);
        creditResultWithContracts.setContract(null);
        creditResultWithContracts.setContractShort(null);
        final var loanOffer = LoanOfferStore.builder().build();
        final var offerRequest = OfferRequest.builder().build();

        // when
        when(creditResultMapper.toCreditResult(creditResultWithContracts)).thenReturn(creditResult);
        when(postbankLoanDemandStoreService.addOffersResponse(creditResultWithContracts.getPartnerContractNumber(), creditResult)).thenReturn(Mono.just(PostbankLoanDemandStore.builder().build()));
        when(loanOfferStoreService.getSingleLoanOfferForLoanProviderReferenceNumber(creditResultWithContracts.getContractNumber().toString())).thenReturn(Mono.just(loanOffer));
        when(offerRequestMapper.fromRequest(loanOffer)).thenReturn(offerRequest);
        when(offerStatusUpdateService.updateOfferStatus(offerRequest, LoanApplicationStatus.PAID_OUT)).thenReturn(Mono.just(loanOffer));

        final var result = postbankWebhookService.savePostbankOfferResponse(creditResultWithContracts);

        assertAll(
                () -> StepVerifier.create(result).expectNextCount(1).verifyComplete(),
                () -> verify(creditResultMapper).toCreditResult(creditResultWithContracts),
                () -> verify(postbankLoanDemandStoreService).addOffersResponse(creditResultWithContracts.getPartnerContractNumber(), creditResult),
                () -> verify(loanOfferStoreService).getSingleLoanOfferForLoanProviderReferenceNumber(creditResultWithContracts.getContractNumber().toString()),
                () -> verify(offerRequestMapper).fromRequest(loanOffer),
                () -> verify(offerStatusUpdateService).updateOfferStatus(offerRequest, LoanApplicationStatus.PAID_OUT)
        );
    }

    @Test
    void doNotUpdateStatusWhenReceivedOffersFromTheBanks() {
        // given
        final var creditResult = CreditResult.builder().build();
        final var creditResultWithContracts = CreditResultWithContracts.builder()
                .contractNumber(1)
                .partnerContractNumber("a")
                .contractState(ONLINE_GENEHMIGT_24)
                .build();

        // when
        when(creditResultMapper.toCreditResult(creditResultWithContracts)).thenReturn(creditResult);
        when(postbankLoanDemandStoreService.addOffersResponse(creditResultWithContracts.getPartnerContractNumber(), creditResult)).thenReturn(Mono.just(PostbankLoanDemandStore.builder().build()));

        final var result = postbankWebhookService.savePostbankOfferResponse(creditResultWithContracts);

        assertAll(
                () -> StepVerifier.create(result).expectNextCount(1).verifyComplete(),
                () -> verify(postbankLoanDemandStoreService).addOffersResponse(creditResultWithContracts.getPartnerContractNumber(), creditResult),
                () -> verifyNoInteractions(loanOfferStoreService),
                () -> verifyNoInteractions(offerRequestMapper),
                () -> verifyNoInteractions(offerStatusUpdateService)
        );
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void storePostbankStatus(ContractState postbankStatus, LoanApplicationStatus expectedLoanStatus, @Random CreditResultWithContracts creditResultWithContracts) {
        // given
        final var creditResult = CreditResult.builder().build();
        creditResultWithContracts.setContractState(postbankStatus);
        creditResultWithContracts.setContract(null);
        creditResultWithContracts.setContractShort(null);
        final var loanOffer = LoanOfferStore.builder().build();
        final var offerRequest = OfferRequest.builder().build();

        // when
        when(creditResultMapper.toCreditResult(creditResultWithContracts)).thenReturn(creditResult);
        when(postbankLoanDemandStoreService.addOffersResponse(creditResultWithContracts.getPartnerContractNumber(), creditResult)).thenReturn(Mono.just(PostbankLoanDemandStore.builder().build()));
        when(loanOfferStoreService.getSingleLoanOfferForLoanProviderReferenceNumber(creditResultWithContracts.getContractNumber().toString())).thenReturn(Mono.just(loanOffer));
        when(offerRequestMapper.fromRequest(loanOffer)).thenReturn(offerRequest);
        when(offerStatusUpdateService.updateOfferStatus(offerRequest, expectedLoanStatus)).thenReturn(Mono.just(loanOffer));

        final var result = postbankWebhookService.savePostbankOfferResponse(creditResultWithContracts);

        assertAll(
                () -> StepVerifier.create(result).expectNextCount(1).verifyComplete(),
                () -> verify(creditResultMapper).toCreditResult(creditResultWithContracts),
                () -> verify(postbankLoanDemandStoreService).addOffersResponse(creditResultWithContracts.getPartnerContractNumber(), creditResult),
                () -> verify(loanOfferStoreService).getSingleLoanOfferForLoanProviderReferenceNumber(creditResultWithContracts.getContractNumber().toString()),
                () -> verify(offerRequestMapper).fromRequest(loanOffer),
                () -> verify(offerStatusUpdateService).updateOfferStatus(offerRequest, expectedLoanStatus)

        );
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(ContractState.IM_SYSTEM_GESPEICHERT_10, LoanApplicationStatus.PENDING),
                Arguments.of(ContractState.MANUELLE_BEARBEITUNG_20, LoanApplicationStatus.PENDING),
                Arguments.of(ContractState.UNTERLAGEN_EINGEGANGEN_25, LoanApplicationStatus.PENDING),
                Arguments.of(ContractState.ALTERNATIV_ANGEBOT_27, LoanApplicationStatus.PENDING),
                Arguments.of(ContractState.UNTERLAGEN_NACHGEFORDERT_30, LoanApplicationStatus.PENDING),
                Arguments.of(ContractState.NACHGEFORDERTE_UNTERLAGEN_EINGEGANGEN_35, LoanApplicationStatus.PENDING),
                Arguments.of(ContractState.ABLOESEBESTAETIGUNG_ANGEFORDERT_40, LoanApplicationStatus.PENDING),
                Arguments.of(ContractState.ANGEFORDERTE_ABLOESEBESTAETIGUNG_EINGEGANGEN_45, LoanApplicationStatus.PENDING),
                Arguments.of(ContractState.ONLINE_ABGELEHNT_93, LoanApplicationStatus.REJECTED),
                Arguments.of(ContractState.ONLINE_GENEHMIGT_UND_AUSBEZAHLT_99, LoanApplicationStatus.PAID_OUT),
                Arguments.of(ContractState.MANUELL_ABGEWIESEN_94, LoanApplicationStatus.REJECTED),
                Arguments.of(ContractState.ALTERNATIV_ANGEBOT_ERFOLGREICH_2724, LoanApplicationStatus.PENDING),
                Arguments.of(ContractState.ALTERNATIV_ANGEBOT_GESCHEITERT_2793, LoanApplicationStatus.PENDING),
                Arguments.of(ContractState.HOCHGELADENE_DOKUMENTE_532, LoanApplicationStatus.PENDING)
        );
    }
}