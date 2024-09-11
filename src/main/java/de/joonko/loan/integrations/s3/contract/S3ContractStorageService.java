package de.joonko.loan.integrations.s3.contract;

import com.amazonaws.services.s3.model.*;

import de.joonko.loan.config.s3.S3ConfigProperties;
import de.joonko.loan.contract.ContractStorageService;
import de.joonko.loan.contract.model.DocumentDetails;
import de.joonko.loan.contract.model.PresignedDocumentDetails;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.integrations.s3.S3Client;
import de.joonko.loan.integrations.s3.contract.model.MimeType;
import de.joonko.loan.util.DateUtil;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ContractStorageService implements ContractStorageService {

    private final S3Client s3Client;
    private final S3ConfigProperties s3Config;

    private static final MimeType MIME_TYPE = MimeType.PDF;

    @Override
    public Mono<List<DocumentDetails>> storeContracts(final @NotNull Documents documents, final @NotNull String userUuid, final @NotNull String applicationId, final @NotNull String offerId) {
        return Flux.fromIterable(documents.getDocuments())
                .flatMap(document -> storeContract(document, userUuid, applicationId, offerId))
                .collectList();
    }

    @Override
    public Mono<Documents> getContracts(List<DocumentDetails> documentDetails) {
        return Flux.fromIterable(documentDetails)
                .flatMap(documentDetail -> s3Client.getContract(s3Config.getContractBucket(), documentDetail.getKey())
                        .flatMap(contract -> Mono.just(readFileFromS3Object(contract, documentDetail.getName())))
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collectList()
                .map(documents -> Documents.builder().documents(documents).build());
    }

    @Override
    public Mono<List<DocumentDetails>> moveDocumentsForKeys(final List<DocumentDetails> oldDocuments, final @NotNull String userUuid, final @NotNull String applicationId, final @NotNull String offerId) {
        return Flux.fromIterable(oldDocuments)
                .flatMap(doc -> {
                    final var newKey = buildKey(userUuid, applicationId, offerId, doc.getName(), MIME_TYPE.getFileExtension());
                    CopyObjectRequest copyObjRequest = new CopyObjectRequest(s3Config.getContractBucket(), doc.getKey(), s3Config.getContractBucket(), newKey);

                    return s3Client.moveObject(copyObjRequest)
                            .map(any -> DocumentDetails.builder()
                                    .key(newKey)
                                    .name(doc.getName())
                                    .build());
                }).collectList();

    }

    private Mono<DocumentDetails> storeContract(Document document, String userUuid, String applicationId, String offerId) {
        final var key = buildKey(userUuid, applicationId, offerId, document.getDocumentId(), MIME_TYPE.getFileExtension());

        final var metadata = new ObjectMetadata();
        metadata.setContentType(MIME_TYPE.getLabel());
        metadata.setContentLength(document.getContent().length);
        metadata.setContentDisposition(buildContentDisposition(document.getDocumentId(), MIME_TYPE.getFileExtension()));

        return Mono.just(s3Config.getContractBucket())
                .doOnNext(bucket -> log.info("Uploading contract: {} for userUUid: {}", document.getDocumentId(), userUuid))
                .flatMap(bucket -> {
                    try (final var is = new ByteArrayInputStream(document.getContent())) {
                        return s3Client.storeObject(bucket, key, is, metadata);
                    } catch (IOException e) {
                        return Mono.error(new RuntimeException(e));
                    }
                })
                .map(any -> DocumentDetails.builder().key(key).name(document.getDocumentId()).build());
    }

    @Override
    public Mono<List<PresignedDocumentDetails>> preSignContracts(final @NotNull List<DocumentDetails> documentDetails, final String userUuid) {
        final var expirationDate = DateUtil.toDate(LocalDate.now().plusDays(s3Config.getContractPresignedUrlDurationInDays()));

        return Flux.fromIterable(documentDetails)
                .doOnNext(docDetails -> log.info("Presigning GET contract url: {} for userUUid: {} with expirationDate: {}", docDetails.getName(), userUuid, expirationDate))
                .flatMap(docDetails -> preSignContract(docDetails, expirationDate))
                .collectList();
    }

    private Mono<PresignedDocumentDetails> preSignContract(final DocumentDetails documentDetails, final Date expirationDate) {
        return Mono.just(documentDetails)
                .map(docDetails -> {
                    ResponseHeaderOverrides overrides = new ResponseHeaderOverrides();
                    overrides.setContentDisposition(buildContentDisposition(documentDetails.getName(), MIME_TYPE.getFileExtension()));

                    return new GeneratePresignedUrlRequest(s3Config.getContractBucket(), documentDetails.getKey())
                            .withExpiration(expirationDate)
                            .withResponseHeaders(overrides);
                })
                .flatMap(s3Client::presignGetUrlObject)
                .map(url -> PresignedDocumentDetails.builder()
                        .url(url.toString())
                        .name(documentDetails.getName()).build());
    }

    @Override
    public Mono<Void> deleteContracts(final @NotNull List<String> contractsKeys, final String userUuid) {
        return Mono.just(contractsKeys)
                .filter(keys -> !keys.isEmpty())
                .doOnNext(keys -> log.info("Deleting {} contracts for userUuid: {}", keys.size(), userUuid))
                .flatMap(keys -> s3Client.deleteObjects(s3Config.getContractBucket(), keys))
                .then();
    }

    private static String buildContentDisposition(String fileName, String fileExtension) {
        return String.format("attachment; filename=%s.%s", fileName, fileExtension);
    }

    private static String buildKey(String userUuid, String applicationId, String offerId, String contractName, String contractFileExtension) {
        return String.format("%s/%s/%s/%s.%s", userUuid, applicationId, offerId, contractName, contractFileExtension);
    }

    private Optional<Document> readFileFromS3Object(final S3Object s3Object, final String fileName) {
        try (InputStream contract = s3Object.getObjectContent()) {
            return Optional.of(Document.builder()
                    .documentId(fileName)
                    .content(contract.readAllBytes())
                    .build());
        } catch (final IOException ex) {
            log.error("Failed fetching file with key: {} from S3 bucket: {}, error message: {}, error cause: {}", s3Object.getKey(), s3Object.getBucketName(), ex.getMessage(), ex.getCause());
        }
        return Optional.empty();
    }
}

