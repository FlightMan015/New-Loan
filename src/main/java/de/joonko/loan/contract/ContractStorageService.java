package de.joonko.loan.contract;

import de.joonko.loan.contract.model.DocumentDetails;
import de.joonko.loan.contract.model.PresignedDocumentDetails;
import de.joonko.loan.identification.model.Documents;

import reactor.core.publisher.Mono;

import java.util.List;

import javax.validation.constraints.NotNull;

public interface ContractStorageService {

    Mono<List<DocumentDetails>> storeContracts(Documents documents, String userUuid, String applicationId, String offerId);

    Mono<Documents> getContracts(List<DocumentDetails> documentDetails);

    Mono<List<DocumentDetails>> moveDocumentsForKeys(List<DocumentDetails> oldDocuments, @NotNull String userUuid, @NotNull String applicationId, @NotNull String offerId);

    Mono<List<PresignedDocumentDetails>> preSignContracts(List<DocumentDetails> documentDetails, String userUuid);

    Mono<Void> deleteContracts(List<String> contractKeys, String userUuid);
}
