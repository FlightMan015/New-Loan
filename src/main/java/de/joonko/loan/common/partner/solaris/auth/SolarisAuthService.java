package de.joonko.loan.common.partner.solaris.auth;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.exception.SolarisAuthException;
import de.joonko.loan.partner.solaris.SolarisPropertiesConfig;
import de.joonko.loan.partner.solaris.model.SolarisGetAccessTokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolarisAuthService {
    private final SolarisPropertiesConfig solarisPropertiesConfig;

    @Qualifier("solarisWebClient")
    private final WebClient solarisWebClient;

    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;

    public Mono<AccessToken> getToken(String id) {
        log.info("Getting Access Token from Solaris");
        SolarisGetAccessTokenRequest solarisGetAccessTokenRequest = new SolarisGetAccessTokenRequest();

        solarisGetAccessTokenRequest.setGrantType("client_credentials");
        solarisGetAccessTokenRequest.setClientId(solarisPropertiesConfig.getClientId());
        solarisGetAccessTokenRequest.setClientSecret(solarisPropertiesConfig.getClientSecret());

        return solarisWebClient.post()
                .uri(getTokenUri())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(solarisGetAccessTokenRequest)
                .retrieve()
                .bodyToMono(AccessToken.class)
                .doOnError(e -> loanApplicationAuditTrailService.saveApplicationError(id, "Error While getting Token " + e.getMessage(), Bank.DEUTSCHE_FINANZ_SOZIETÄT.name()))
                .onErrorMap(e -> new SolarisAuthException("Error While getting Token", e));
    }

    private Function<UriBuilder, URI> getTokenUri() {
        return uriBuilder -> uriBuilder.path(solarisPropertiesConfig.getTokenEndpoint())
                .build();
    }
}
