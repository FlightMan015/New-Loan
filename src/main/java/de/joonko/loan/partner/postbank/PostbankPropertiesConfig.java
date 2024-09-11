package de.joonko.loan.partner.postbank;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.function.Function;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "postbank")
@Data
@Validated
public class PostbankPropertiesConfig {

    @NotBlank
    private String host;
    @NotBlank
    private String companyId;
    @NotBlank
    private String password;
    @NotBlank
    private String loanDemandEndpoint;

    @NotNull
    private Integer minLoanAmount;

    @NotNull
    private Integer maxLoanAmount;

    //    @Value("${postank.offerResponse.retry.maxAttempts}")
    private Integer offerResponseRetryMaxAttempts;

    //    @Value("${postbank.offerResponse.retry.maxAttempts}")
    private Integer offerResponseRetryMaxDelay;

    public Function<UriBuilder, URI> getLoanDemandUri() {
        return uriBuilder ->
                uriBuilder.path(loanDemandEndpoint)
                        .build();
    }

    @Value("${TWEAKED_IBAN_IN_LOAN_DEMAND_REQUEST:#{NULL}}")
    private String tweakedIBAN;
}
