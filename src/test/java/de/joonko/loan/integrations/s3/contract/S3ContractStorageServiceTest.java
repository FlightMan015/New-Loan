package de.joonko.loan.integrations.s3.contract;

import com.amazonaws.services.s3.model.*;

import de.joonko.loan.config.s3.S3ConfigProperties;
import de.joonko.loan.contract.ContractStorageService;
import de.joonko.loan.contract.model.DocumentDetails;
import de.joonko.loan.contract.model.PresignedDocumentDetails;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.integrations.s3.S3Client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class S3ContractStorageServiceTest {

    private ContractStorageService contractStorageService;

    private S3Client s3Client;
    private S3ConfigProperties s3Config;

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);
        s3Config = mock(S3ConfigProperties.class);
        contractStorageService = new S3ContractStorageService(s3Client, s3Config);

        when(s3Config.getContractBucket()).thenReturn("b2b-loan-contract-dev");
        when(s3Config.getContractPresignedUrlDurationInDays()).thenReturn(1L);
    }

    @Test
    void storeContracts() {
        // given
        final var documents = Documents.builder()
                .documents(List.of(
                        Document.builder().documentId("agreement").content(new byte[]{}).build(),
                        Document.builder().documentId("schedule").content(new byte[]{}).build()))
                .build();
        when(s3Client.storeObject(eq(s3Config.getContractBucket()), anyString(), any(InputStream.class), any(ObjectMetadata.class))).thenReturn(Mono.just(new PutObjectResult()));

        // when
        final var storedContracts = contractStorageService.storeContracts(documents, "61984bd9-dc30-4d5e-8961-fc1801b01f49", "h38f92h38f90", "8dh392f");

        // then
        StepVerifier.create(storedContracts)
                .consumeNextWith(l -> assertEquals(2, l.size()))
                .verifyComplete();
    }

    @Test
    void getContracts() {
        final var documentDetails = List.of(DocumentDetails.builder()
                .key("a")
                .name("b")
                .build());
        final var s3Object = new S3Object();
        final var contract = "Some random stream".getBytes(StandardCharsets.UTF_8);
        s3Object.setObjectContent(new ByteArrayInputStream(contract));

        when(s3Client.getContract(s3Config.getContractBucket(), "a")).thenReturn(Mono.just(s3Object));

        final var contracts = contractStorageService.getContracts(documentDetails);


        StepVerifier.create(contracts)
                .consumeNextWith(doc -> assertEquals(contract.length, doc.getDocuments().stream().findFirst().get().getContent().length))
                .verifyComplete();
    }

    @SneakyThrows
    @Test
    void presignContracts() {
        // given
        final var documentDetails = List.of(
                DocumentDetails.builder().key("key1").name("agreement").build(),
                DocumentDetails.builder().key("key2").name("schedule").build());
        when(s3Client.presignGetUrlObject(any(GeneratePresignedUrlRequest.class)))
                .thenReturn(Mono.just(new URL("https", "s3.eu-central-1.amazonaws.com", 8080, "/test.pdf")))
                .thenReturn(Mono.just(new URL("https", "s3.eu-central-1.amazonaws.com", 8080, "/test2.pdf")));


        // when
        final var presignedContracts = contractStorageService.preSignContracts(documentDetails, "61984bd9-dc30-4d5e-8961-fc1801b01f49");

        // then
        StepVerifier.create(presignedContracts).consumeNextWith(l -> assertAll(
                () -> assertEquals(2, l.size()),
                () -> assertTrue(l.stream()
                        .map(PresignedDocumentDetails::getUrl)
                        .collect(Collectors.toList())
                        .containsAll(List.of("https://s3.eu-central-1.amazonaws.com:8080/test.pdf", "https://s3.eu-central-1.amazonaws.com:8080/test2.pdf"))))
        ).verifyComplete();
    }

    @Test
    void deleteContracts() {
        // given
        final var contractKeys = List.of("key1", "key2");
        when(s3Client.deleteObjects(s3Config.getContractBucket(), contractKeys)).thenReturn(Mono.just(new DeleteObjectsResult(List.of())));

        // when
        final var deletedContracts = contractStorageService.deleteContracts(contractKeys, "61984bd9-dc30-4d5e-8961-fc1801b01f49");

        // then
        StepVerifier.create(deletedContracts)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void doNotCallStorageToDeleteContractsWhenNoKeys() {
        // given
        final List<String> contractKeys = List.of();

        // when
        final var deletedContracts = contractStorageService.deleteContracts(contractKeys, "61984bd9-dc30-4d5e-8961-fc1801b01f49");

        // then
        StepVerifier.create(deletedContracts)
                .expectNextCount(0)
                .verifyComplete();
    }
}
