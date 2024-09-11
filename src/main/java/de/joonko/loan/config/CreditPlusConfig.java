package de.joonko.loan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "creditplus")
@Data
@Validated
public class CreditPlusConfig {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String host;
    @NotBlank
    private String service;

    private int productType;//28 ==> PROD , 67 ==>TEST

    @NotBlank
    private String custom3;
}
