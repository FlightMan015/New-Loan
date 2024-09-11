package de.joonko.loan.integrations.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.PutObjectResult;
import de.joonko.loan.config.s3.S3ConfigProperties;
import de.joonko.loan.contract.ContractStorageService;
import de.joonko.loan.contract.model.DocumentDetails;
import de.joonko.loan.contract.model.PresignedDocumentDetails;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.integrations.s3.contract.S3ContractStorageService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.test.StepVerifier;

import java.net.URL;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("integration")
@SpringBootTest
class S3ContractStorageServiceIT {

    @Autowired
    private ContractStorageService s3ContractStorageService;

    private S3Client s3Client;
    private AmazonS3Client amazonS3Client;
    private S3ConfigProperties s3Config;
    private static final String BUCKET_NAME = "b2b-loan-contract-dev";
    private static final String KEY = "userUuid/applicationId/offerId/agreement.pdf";

    @BeforeEach
    void setUp() {
        // given
        amazonS3Client = mock(AmazonS3Client.class);
        s3Client = new S3Client(amazonS3Client);
        s3Config = new S3ConfigProperties();
        s3Config.setContractBucket(BUCKET_NAME);
        s3Config.setContractPresignedUrlDurationInDays(1L);

        when(amazonS3Client.putObject(anyString(), anyString(), any(), any())).thenReturn(new PutObjectResult());
        when(amazonS3Client.deleteObjects(any(DeleteObjectsRequest.class))).thenReturn(new DeleteObjectsResult(List.of(new DeleteObjectsResult.DeletedObject())));

        ReflectionTestUtils.setField(s3ContractStorageService, "s3Client", s3Client);
        ReflectionTestUtils.setField(s3ContractStorageService, "s3Config", s3Config);
    }

    @Test
    void storeObject() {
        // given
        final var userUuid = UUID.randomUUID().toString();
        final var documents = Documents.builder()
                .documents(List.of(
                        Document.builder().documentId("agreement").content(new byte[]{}).build(),
                        Document.builder().documentId("schedule").content(new byte[]{}).build()))
                .build();

        // when
        final var storedContracts = s3ContractStorageService.storeContracts(documents, userUuid, "h38f92h38f90", "8dh392f");

        // then
        StepVerifier.create(storedContracts)
                .consumeNextWith(l -> assertEquals(2, l.size()))
                .verifyComplete();
    }

    @SneakyThrows
    @Test
    void presignContracts() {
        // given
        when(amazonS3Client.generatePresignedUrl(any())).thenReturn(new URL("https", "s3.eu-central-1.amazonaws.com", 8080, "response-content-disposition/test.pdf"));

        final var userUuid = UUID.randomUUID().toString();
        final var storedContract = s3ContractStorageService.storeContracts(Documents.builder()
                .documents(List.of(
                        Document.builder().documentId("agreement").content(new byte[]{}).build(),
                        Document.builder().documentId("schedule").content(new byte[]{}).build())
                )
                .build(), userUuid, "h29f8j249f8h43", "2j38f93hf93f");
        StepVerifier.create(storedContract).expectNextCount(1).verifyComplete();

        final var documentDetails = List.of(
                DocumentDetails.builder().key(userUuid + "/h29f8j249f8h43/2j38f93hf93f/agreement.pdf").name("agreement").build(),
                DocumentDetails.builder().key(userUuid + "/h29f8j249f8h43/2j38f93hf93f/schedule.pdf").name("schedule").build());

        // when
        final var presignedContracts = s3ContractStorageService.preSignContracts(documentDetails, userUuid);

        // then
        StepVerifier.create(presignedContracts).consumeNextWith(l -> assertAll(
                () -> assertEquals(2, l.size()),
                () -> assertTrue(l.stream().map(PresignedDocumentDetails::getUrl).anyMatch(s -> s.contains("response-content-disposition")))
        )).verifyComplete();
    }

    @Test
    void deleteContracts() {
        // given
        final var userUuid = UUID.randomUUID().toString();
        final var storedContract = s3ContractStorageService.storeContracts(Documents.builder()
                .documents(List.of(
                        Document.builder().documentId("agreement").content(new byte[]{}).build(),
                        Document.builder().documentId("schedule").content(new byte[]{}).build())
                )
                .build(), userUuid, "h29f8j249f8h43", "2j38f93hf93f");
        StepVerifier.create(storedContract).expectNextCount(1).verifyComplete();

        final var contractKeys = List.of(
                userUuid + "/h29f8j249f8h43/2j38f93hf93f/agreement.pdf",
                userUuid + "/h29f8j249f8h43/2j38f93hf93f/schedule.pdf");

        // when
        final var deletedContracts = s3ContractStorageService.deleteContracts(contractKeys, userUuid);

        // then
        StepVerifier.create(deletedContracts)
                .expectNextCount(0)
                .verifyComplete();
    }
}

