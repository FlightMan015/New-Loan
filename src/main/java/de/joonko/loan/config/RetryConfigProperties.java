package de.joonko.loan.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "retry")
@Data
@Validated
public class RetryConfigProperties {
    @NotNull
    private Integer fixedBackoffSeconds;
    @NotNull
    private Integer maxRetry;
}
