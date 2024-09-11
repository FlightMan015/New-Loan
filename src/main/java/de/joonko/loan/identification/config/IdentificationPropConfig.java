package de.joonko.loan.identification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "idnow")
@Data
@Validated
public class IdentificationPropConfig {

    @NotNull
    private String host;
    @NotNull
    private String identificationHost;
    @NotNull
    private Boolean autoidentification;

    @NotNull
    private String auxmoneyAccountId;
    @NotNull
    private String auxmoneyApiKey;
    @NotNull
    private String consorsAccountId;
    @NotNull
    private String consorsApiKey;
    @NotNull
    private String swkAccountId;
    @NotNull
    private String swkApiKey;
    @NotNull
    private String creditPlusAccountId;
    @NotNull
    private String creditPlusApiKey;
    @NotNull
    private String aionAccountId;
    @NotNull
    private String aionApiKey;
}
