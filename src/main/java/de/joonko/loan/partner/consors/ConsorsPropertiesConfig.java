package de.joonko.loan.partner.consors;

import lombok.Builder;
import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

@Configuration
@ConfigurationProperties(prefix = "consors")
@Data
@Validated
public class ConsorsPropertiesConfig {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String host;
    @NotBlank
    private String tokenEndpoint;
    @NotBlank
    private String productEndpoint;
    @NotBlank
    private String version;
    @NotBlank
    private String ratanet;
    @NotNull
    private List<Integer> greenProfileDocIgnoreList;
    @NotBlank
    private String dacEmail;
    @NotBlank
    private String partnerId;
    @NotNull
    private Integer minCreditAmount;
    @Min(value = 18)
    @Max(value = 100)
    @NotNull
    private Integer acceptedApplicantMinAge;
    @Min(value = 18)
    @Max(value = 100)
    @NotNull
    private Integer acceptedApplicantMaxAge;
    @NotNull
    private Integer acceptedApplicantMinIncome;
    @NotNull
    private Integer acceptedApplicantMinProbationInMonths;

    @Min(value = 0)
    @NotNull
    private Integer acceptedApplicantMinLoanAmount;

    @Min(value = 0)
    @NotNull
    private Integer acceptedApplicantMaxLoanAmount;

    @Builder.Default
    @NotNull
    private Boolean webidEnabled = false;

    @Bean
    public FinancialCalculationsFilter getFinancialCalculationsFilter() {
        return new FinancialCalculationsFilter(minCreditAmount);
    }

    public Function<UriBuilder, URI> getTokenUri() {
        return uriBuilder -> uriBuilder.path(tokenEndpoint)
                .queryParam("version", version)
                .build();
    }

    public Function<UriBuilder, URI> buildUriFromUrlLink(String finalizeSubscriptionUrl) {
        var uriComponents = UriComponentsBuilder.fromUriString(finalizeSubscriptionUrl).build();

        return uriBuilder -> uriBuilder.path(ratanet)
                .path(uriComponents.getPath())
                .query(uriComponents.getQuery())
                .build();
    }

    public Function<UriBuilder, URI> buildProductUri() {
        return uriBuilder -> uriBuilder
                .path(ratanet)
                .path(productEndpoint)
                .queryParam("version", version)
                .build();
    }
}
