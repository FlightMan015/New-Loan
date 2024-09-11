package de.joonko.loan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "customer.support")
@Data
@Validated
public class CustomerSupportConfig {

    @NotBlank
    private String host;

    @NotBlank
    private String userEndpoint;

    @NotBlank
    private String eventEndpoint;

    @NotBlank
    private String eventOfferEndpoint;

    @NotBlank
    private String eventApplicationAuditEndpoint;

    @NotBlank
    private String eventOfferAcceptedEndpoint;

    @NotBlank
    private String eventKycStatus;

    @NotBlank
    private String dashBoardUrl;

    @NotBlank
    private String dashBoardOffersEndpoint;

    @NotBlank
    private String dashBoardApplicationAuditEndpoint;
}
