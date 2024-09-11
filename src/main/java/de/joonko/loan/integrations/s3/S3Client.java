package de.joonko.loan.integrations.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Client {

    private final AmazonS3Client amazonS3Client;

    public Mono<PutObjectResult> storeObject(final @NotNull String bucketName, final @NotNull String objectKey, final @NotNull InputStream inputStream, final @NotNull ObjectMetadata objectMetadata) {
        return Mono.fromCallable(() -> amazonS3Client.putObject(bucketName, objectKey, inputStream, objectMetadata))
                .doOnNext(request -> log.info("Uploaded object: {} to the S3 bucket: {}", objectKey, bucketName))
                .doOnError(error -> log.error("Failed uploading object to S3 bucket: {}", bucketName, error))
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<S3Object> getContract(final @NotNull String bucketName, final @NotNull String objectKey) {
        return Mono.fromCallable(() -> amazonS3Client.getObject(bucketName, objectKey))
                .doOnNext(request -> log.info("Fetched object: {} from the S3 bucket: {}", objectKey, bucketName))
                .doOnError(error -> log.error("Failed fetching object from S3 bucket: {}", bucketName, error))
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<DeleteObjectsResult> moveObject(final @NotNull CopyObjectRequest copyObjectRequest) {
        return Mono.fromCallable(() -> amazonS3Client.copyObject(copyObjectRequest))
                .flatMap(obj -> deleteObjects(copyObjectRequest.getSourceBucketName(), List.of(copyObjectRequest.getSourceKey())))
                .doOnNext(tuple -> log.info("Moved object: for key {} to key {} for S3 bucket: {}", copyObjectRequest.getSourceKey(), copyObjectRequest.getDestinationKey(), copyObjectRequest.getSourceBucketName()))
                .doOnError(error -> log.error("Failed moving object for key {} to key {} for S3 bucket: {}, error: {}", copyObjectRequest.getSourceKey(), copyObjectRequest.getDestinationKey(), copyObjectRequest.getSourceBucketName(), error))
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<URL> presignGetUrlObject(final @NotNull GeneratePresignedUrlRequest request) {
        return Mono.fromCallable(() -> amazonS3Client.generatePresignedUrl(request))
                .doOnNext(any -> log.info("Presigned GET url for object: {}, S3 bucket: {}", request.getKey(), request.getBucketName()))
                .doOnError(error -> log.error("Failed presigning object from S3 bucket: {}", request.getBucketName(), error))
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<DeleteObjectsResult> deleteObjects(final @NotNull String bucketName, final @NotNull @NotEmpty List<String> objectKeys) {
        final var deleteObjectRequest = new DeleteObjectsRequest(bucketName)
                .withKeys(objectKeys.toArray(String[]::new));

        return Mono.fromCallable(() -> amazonS3Client.deleteObjects(deleteObjectRequest))
                .doOnNext(request -> log.info("Deleted {} objects from S3 bucket: {}", request.getDeletedObjects().size(), bucketName))
                .doOnError(error -> log.error("Failed deleting objects from S3 bucket: {}", bucketName, error))
                .subscribeOn(Schedulers.elastic());
    }
}
