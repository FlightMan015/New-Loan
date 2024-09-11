package de.joonko.loan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "auxmoney")
@Data
@Validated
public class AuxmoneyConfig {
    @NotBlank
    private String host;
    @NotBlank
    private String urlkey;
    @NotBlank
    private String offersEndpoint;
    @NotBlank
    private String acceptOfferEndpoint;
}
