package de.joonko.loan.partner.solaris;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "solaris")
@Data
@Validated
public class SolarisPropertiesConfig {

    @NotBlank
    private String host;

    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;

    @NotBlank
    private String tokenEndpoint;

    @NotBlank
    private String personsEndpoint;

    @NotBlank
    private String creditEndpoint;

    @NotBlank
    private String loanEndpoint;

    @NotBlank
    private String accountSnapshotEndpoint;

    @NotBlank
    private String accountSnapshotUpdateEndpoint;

    @NotBlank
    private String signingEndPoint;

    @NotNull
    private Boolean tweakSnapshot;
}
