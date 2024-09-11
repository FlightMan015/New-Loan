package de.joonko.loan.partner.aion;

import lombok.Builder;
import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.net.URI;
import java.util.function.Function;

@Configuration
@ConfigurationProperties(prefix = "aion")
@Data
@Validated
public class AionPropertiesConfig {

    @NotNull
    private Boolean enabled;

    @NotBlank
    private String host;
    @NotBlank
    private String brandId;
    @NotBlank
    private String customerId;
    @NotBlank
    private String gatewayKey;
    @NotBlank
    private String authEndpoint;
    @NotBlank
    private String authClientId;
    @NotBlank
    private String authAudience;
    @NotBlank
    private String authClientSecret;
    @NotBlank
    private String processEndpoint;

    @NotNull
    private BigDecimal minAverage3MSalary;

    @NotNull
    private Integer acceptedApplicantMaxLoanAmount;

    @NotNull
    private BigDecimal minLastSalary;

    @NotNull
    private Integer acceptedApplicantMinAge;

    @NotNull
    private Integer acceptedApplicantMaxAge;

    @NotNull
    private Integer minEmploymentMonths;

    @NotNull
    @Builder.Default
    private Integer acceptedBonimaScore = 90000;

    public Function<UriBuilder, URI> getTokenUri() {
        return uriBuilder ->
                uriBuilder.path(brandId)
                        .path(authEndpoint)
                        .build();
    }

    public Function<UriBuilder, URI> getProcessUri() {
        return uriBuilder ->
                uriBuilder.path(brandId)
                        .path(processEndpoint)
                        .build();
    }

    public Function<UriBuilder, URI> getOffersToBeatUri(final String processId) {
        return uriBuilder ->
                uriBuilder.path(brandId)
                        .path(processEndpoint)
                        .path("/")
                        .path(processId)
                        .build();
    }

    public Function<UriBuilder, URI> getOfferChoiceUri(final String processId) {
        return uriBuilder ->
                uriBuilder.path(brandId)
                        .path(processEndpoint)
                        .path("/")
                        .path(processId)
                        .build();
    }

    public Function<UriBuilder, URI> getOfferStatusUri(final String processId) {
        return uriBuilder ->
                uriBuilder.path(brandId)
                        .path(processEndpoint)
                        .path("/")
                        .path(processId)
                        .build();
    }
}
