package de.joonko.loan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;


@Configuration
@ConfigurationProperties(prefix = "fts")
@Data
@Validated
public class FtsConfig
{
    @NotBlank
    private String endPointUrl;

    @NotBlank
    private String apiKey;

}
