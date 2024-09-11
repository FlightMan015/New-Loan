package de.joonko.loan.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "encryption")
@Data
@Validated
public class EncryptionConfig {

    @Value("${encryption.security.encryptionKey}")
    private String securityEncryptionKey;

    @Value("${encryption.anonymization.encryptionKey}")
    private String anonymizationEncryptionKey;

    @Value("${encryption.anonymization.iv}")
    private String anonymizationIv;
}
