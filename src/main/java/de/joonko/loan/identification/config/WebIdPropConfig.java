package de.joonko.loan.identification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "webid")
@Data
@Validated
public class WebIdPropConfig {
    @NotNull
    private String host;
    @NotNull
    private String basicAuthUsername;
    @NotNull
    private String basicAuthPassword;
    @NotNull
    private String frontendHost;
    @NotNull
    private String santanderMd;
}
