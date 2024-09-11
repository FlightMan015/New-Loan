package de.joonko.loan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "mongo.ssl")
@Data
@Validated
public class MongoSslConfig {

    private String isSslEnabled;

    private String trustStorePath;
    private String trustStorePassword;
}
