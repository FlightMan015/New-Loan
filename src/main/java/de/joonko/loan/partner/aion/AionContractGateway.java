package de.joonko.loan.partner.aion;

import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.partner.aion.model.CreditApplicationResponseStore;
import de.joonko.loan.partner.aion.model.offerchoice.FileDetails;
import de.joonko.loan.partner.aion.model.offerchoice.OfferChoiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Slf4j
@Service
public class AionContractGateway {

    private final AionClient aionClient;
    private final AionStoreService aionStoreService;

    public Mono<Documents> getDocuments(final @NotNull String applicationId) {
        return Mono.from(getProcessId(applicationId))
                .zipWhen(token -> aionClient.getToken(applicationId))
                .flatMap(tuple -> aionClient.getOfferStatus(tuple.getT2(), tuple.getT1()))
                .map(OfferChoiceResponse::getDraftAgreement)
                .map(this::mapToDocuments)
                .map(documents -> Documents.builder().documents(documents).build())
                .doOnError(throwable -> log.error("Failed getting bank documents for applicationId: {}", applicationId, throwable));
    }

    private List<Document> mapToDocuments(List<FileDetails> fileDetailsList) {
        return fileDetailsList.stream()
                .map(fileDetails -> Document.builder()
                        .documentId(fileDetails.getFileId())
                        .content(decode(fileDetails.getFileContent()))
                        .build())
                .collect(toList());
    }

    public static byte[] decode(String base64Str) {
        return Base64.getDecoder().decode(base64Str.getBytes(StandardCharsets.UTF_8));
    }

    private Mono<String> getProcessId(String applicationId) {
        return Mono.from(aionStoreService.findByApplicationId(applicationId))
                .map(Optional::get)
                .map(CreditApplicationResponseStore::getProcessId)
                .doOnNext(processId -> log.info("Got processId: {}, applicationId: {}", processId, applicationId))
                .doOnError(e -> log.error("Failed getting processId for applicationId: {}", applicationId, e));
    }
}
