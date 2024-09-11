package de.joonko.loan.webhooks.postbank;

import de.joonko.loan.acceptoffer.api.OfferRequestMapper;
import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.contract.ContractStorageService;
import de.joonko.loan.db.service.LoanDemandRequestService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.offer.domain.OfferStatusUpdateService;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStore;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStoreService;
import de.joonko.loan.webhooks.postbank.model.CreditResultWithContracts;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static de.joonko.loan.util.Base64Encoder.decodeFromBase64;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostbankWebhookService {

    private final PostbankLoanDemandStoreService postbankLoanDemandStoreService;

    private final LoanOfferStoreService loanOfferStoreService;

    private final OfferStatusUpdateService offerStatusUpdateService;

    private final OfferRequestMapper offerRequestMapper;

    private final CreditResultMapper creditResultMapper;

    private final ContractStorageService contractStorageService;

    private final LoanDemandRequestService loanDemandRequestService;

    public Mono<PostbankLoanDemandStore> savePostbankOfferResponse(final CreditResultWithContracts creditResultWithContracts) {
        return storeAndAddContracts(creditResultWithContracts)
                .flatMap(postbankLoanDemandStore ->
                        mapPostbankStatusAndUpdateLoanOffer(creditResultWithContracts)
                                .map(any -> postbankLoanDemandStore)
                                .switchIfEmpty(Mono.defer(() -> Mono.just(postbankLoanDemandStore)))
                );
    }

    private Mono<PostbankLoanDemandStore> storeAndAddContracts(final CreditResultWithContracts creditResultWithContracts) {
        return Mono.just(creditResultMapper.toCreditResult(creditResultWithContracts))
                .flatMap(creditResult -> {
                    if (creditResultWithContracts.hasContracts()) {
                        return loanDemandRequestService.findLoanDemandRequest(creditResultWithContracts.getPartnerContractNumber())
                                .flatMap(application -> {
                                    final List<Document> documents = new ArrayList<>();
                                    if (nonNull(creditResultWithContracts.getContract())) {
                                        documents.add(Document.builder()
                                                .content(decodeFromBase64(creditResultWithContracts.getContract()))
                                                .documentId("Main_Contract")
                                                .build());
                                    }
                                    if (nonNull(creditResultWithContracts.getContractShort())) {
                                        documents.add(Document.builder()
                                                .content(decodeFromBase64(creditResultWithContracts.getContractShort()))
                                                .documentId("Short_Contract")
                                                .build());
                                    }

                                    return contractStorageService.storeContracts(
                                            Documents.builder()
                                                    .documents(documents)
                                                    .build(),
                                            application.getUserUUID(),
                                            application.getApplicationId(),
                                            creditResultWithContracts.getContractNumber().toString()
                                    );
                                })
                                .map(docs -> creditResult.toBuilder().savedContracts(docs).build());
                    }
                    return Mono.just(creditResult);
                })
                .flatMap(creditResult -> postbankLoanDemandStoreService.addOffersResponse(creditResultWithContracts.getPartnerContractNumber(), creditResult));
    }

    private Mono<LoanOfferStore> mapPostbankStatusAndUpdateLoanOffer(final CreditResultWithContracts creditResultWithContracts) {
        return Mono.just(creditResultWithContracts)
                .doOnNext(creditResult -> log.info("POSTBANK: Successfully received the webhook with status - {} for applicationId - {}", creditResult.getContractState(), creditResult.getPartnerContractNumber()))
                .flatMap(any -> {
                    switch (creditResultWithContracts.getContractState()) {
                        case ONLINE_GENEHMIGT_24:
                            return Mono.empty();
                        case DIGITALE_SIGNATUR_EINGEGANGEN_80:
                            return loanOfferStoreService.getSingleLoanOfferForLoanProviderReferenceNumber(creditResultWithContracts.getContractNumber().toString())
                                    .flatMap(store -> offerStatusUpdateService.updateKycStatus(store, LoanApplicationStatus.SUCCESS));
                        case ONLINE_GENEHMIGT_UND_AUSBEZAHLT_99:
                            return loanOfferStoreService.getSingleLoanOfferForLoanProviderReferenceNumber(creditResultWithContracts.getContractNumber().toString())
                                    .map(offerRequestMapper::fromRequest)
                                    .flatMap(request -> offerStatusUpdateService.updateOfferStatus(request, LoanApplicationStatus.PAID_OUT));
                        case ONLINE_ABGELEHNT_93:
                        case MANUELL_ABGEWIESEN_94:
                            return loanOfferStoreService.getSingleLoanOfferForLoanProviderReferenceNumber(creditResultWithContracts.getContractNumber().toString())
                                    .map(offerRequestMapper::fromRequest)
                                    .flatMap(request -> offerStatusUpdateService.updateOfferStatus(request, LoanApplicationStatus.REJECTED));
                        case IM_SYSTEM_GESPEICHERT_10:
                        case MANUELLE_BEARBEITUNG_20:
                        case UNTERLAGEN_EINGEGANGEN_25:
                        case ALTERNATIV_ANGEBOT_27:
                        case UNTERLAGEN_NACHGEFORDERT_30:
                        case NACHGEFORDERTE_UNTERLAGEN_EINGEGANGEN_35:
                        case ABLOESEBESTAETIGUNG_ANGEFORDERT_40:
                        case ANGEFORDERTE_ABLOESEBESTAETIGUNG_EINGEGANGEN_45:
                        case ALTERNATIV_ANGEBOT_ERFOLGREICH_2724:
                        case ALTERNATIV_ANGEBOT_GESCHEITERT_2793:
                        case HOCHGELADENE_DOKUMENTE_532:
                            return loanOfferStoreService.getSingleLoanOfferForLoanProviderReferenceNumber(creditResultWithContracts.getContractNumber().toString())
                                    .map(offerRequestMapper::fromRequest)
                                    .flatMap(request -> offerStatusUpdateService.updateOfferStatus(request, LoanApplicationStatus.PENDING));
                        default:
                            log.warn("POSTBANK: Received unexpected offer status from Postbank, contractNumber - {}, applicationId - {}, status - {}", creditResultWithContracts.getContractNumber(), creditResultWithContracts.getPartnerContractNumber(), creditResultWithContracts.getContractState());
                            return Mono.empty();
                    }
                });
    }
}
