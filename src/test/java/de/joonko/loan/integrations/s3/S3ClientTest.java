package de.joonko.loan.integrations.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class S3ClientTest {

    private S3Client s3Client;

    private AmazonS3Client amazonS3Client;
    private static final String BUCKET_NAME = "b2b-loan-contract-dev";
    private static final String KEY = "userUuid/applicationId/offerId/agreement.pdf";

    @BeforeEach
    void setUp() {
        amazonS3Client = mock(AmazonS3Client.class);
        s3Client = new S3Client(amazonS3Client);
    }

    @Test
    void uploadObject() {
        // given
        final var content = new ByteArrayInputStream(new byte[]{});
        final var metadata = new ObjectMetadata();
        when(amazonS3Client.putObject(BUCKET_NAME, KEY, content, metadata)).thenReturn(new PutObjectResult());

        // when
        final var storedObject = s3Client.storeObject(BUCKET_NAME, KEY, content, metadata);

        // then
        StepVerifier.create(storedObject).expectNextCount(1).verifyComplete();
    }

    @SneakyThrows
    @Test
    void presignGetUrl() {
        // given
        final var expirationDate = new Date();
        final var presignRequest = new GeneratePresignedUrlRequest(BUCKET_NAME, KEY)
                .withExpiration(expirationDate);
        when(amazonS3Client.generatePresignedUrl(presignRequest)).thenReturn(new URL("https", "s3.eu-central-1.amazonaws.com", 8080, "test.pdf"));

        // when
        final var presignGetUrlObject = s3Client.presignGetUrlObject(presignRequest);

        // then
        StepVerifier.create(presignGetUrlObject).expectNextCount(1).verifyComplete();
    }

    @Test
    void deleteObject() {
        // given
        when(amazonS3Client.deleteObjects(any(DeleteObjectsRequest.class))).thenReturn(new DeleteObjectsResult(List.of(new DeleteObjectsResult.DeletedObject())));

        // when
        final var deleteObjects = s3Client.deleteObjects(BUCKET_NAME, List.of(KEY));

        // then
        StepVerifier.create(deleteObjects).expectNextCount(1).verifyComplete();
    }
}
