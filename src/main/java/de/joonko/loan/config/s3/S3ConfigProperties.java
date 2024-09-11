package de.joonko.loan.config.s3;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Configuration
@Data
@Validated
public class S3ConfigProperties {

    @Value("${aws.s3.contract.bucket}")
    @NotBlank
    private String contractBucket;

    @Value("${aws.s3.contract.presigned-url-duration-in-days}")
    @NotBlank
    private Long contractPresignedUrlDurationInDays;
}
