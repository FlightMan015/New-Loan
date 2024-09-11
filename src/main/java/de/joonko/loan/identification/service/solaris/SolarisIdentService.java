package de.joonko.loan.identification.service.solaris;

import de.joonko.loan.common.partner.solaris.auth.AccessToken;
import de.joonko.loan.common.partner.solaris.auth.SolarisAuthService;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.CreateIdentResponse;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.model.idnow.Result;
import de.joonko.loan.identification.model.solaris.SolarisGetIdentificationStatusResponse;
import de.joonko.loan.identification.service.IdentService;
import de.joonko.loan.identification.service.IdentificationAuditService;
import de.joonko.loan.partner.solaris.SolarisAcceptOfferResponseStore;
import de.joonko.loan.partner.solaris.SolarisPropertiesConfig;
import de.joonko.loan.partner.solaris.SolarisStoreService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SolarisIdentService implements IdentService {

    private final SolarisStoreService solarisStoreService;
    private final WebClient.Builder solarisWebClientBuilder;
    private final SolarisAuthService solarisAuthService;
    private final SolarisPropertiesConfig solarisPropertiesConfig;
    private final IdentificationAuditService identificationService;

    public SolarisIdentService(
            IdentificationAuditService identificationService,
            SolarisStoreService solarisStoreService,
            WebClient.Builder solarisWebClientBuilder,
            SolarisAuthService solarisAuthService,
            SolarisPropertiesConfig solarisPropertiesConfig) {
        this.solarisStoreService = solarisStoreService;
        this.solarisWebClientBuilder = solarisWebClientBuilder;
        this.solarisAuthService = solarisAuthService;
        this.solarisPropertiesConfig = solarisPropertiesConfig;
        this.identificationService = identificationService;
    }

    @Override
    public Mono<CreateIdentResponse> createIdent(CreateIdentRequest createIdentRequest) {
        String kycUrl = identificationService.getKycUrlForSolaris(createIdentRequest.getApplicationId());
        return Mono.just(new CreateIdentResponse(kycUrl, IdentificationProvider.SOLARIS, Documents.builder().build()));
    }

    @Override
    public Mono<String> getIdentStatus(String applicationId) {
        List<SolarisAcceptOfferResponseStore> solarisAcceptOffers = this.solarisStoreService.getAcceptOfferResponseStoreByIdentificationId(applicationId);
        Optional<SolarisAcceptOfferResponseStore> solarisAcceptOfferResponseStore = solarisAcceptOffers.stream().findFirst();

        if (solarisAcceptOfferResponseStore.isPresent()) {
            String loanApplicationId = solarisAcceptOfferResponseStore.get().getApplicationId();
            String identificationId = solarisAcceptOfferResponseStore.get().getIdentificationId();
            String personId = solarisAcceptOfferResponseStore.get().getPersonId();

            return solarisAuthService.getToken(loanApplicationId)
                    .flatMap(accessToken -> getSolarisIdentificationStatus(accessToken, identificationId, personId))
                    .map(this::toStatus);
        } else {
            return Mono.error(new RuntimeException("Can not find solaris accept offer " + applicationId));
        }
    }

    @Override
    public IdentificationProvider getProvider() {
        return IdentificationProvider.SOLARIS;
    }

    private Mono<SolarisGetIdentificationStatusResponse> getSolarisIdentificationStatus(AccessToken accessToken, String identificationId, String personId) {
        WebClient client = solarisWebClientBuilder.defaultHeaders(accessToken.bearer()).build();
        return client.get()
                .uri(uriBuilder -> buildUriForIdentificationstatus(uriBuilder, personId, identificationId))
                .retrieve()
                .bodyToMono(SolarisGetIdentificationStatusResponse.class);
    }

    private URI buildUriForIdentificationstatus(UriBuilder uriBuilder, String personId, String identificationId) {
        return uriBuilder.path(solarisPropertiesConfig.getPersonsEndpoint())
                .pathSegment(personId)
                .path("/identifications")
                .pathSegment(identificationId)
                .build();
    }

    private String toStatus(SolarisGetIdentificationStatusResponse solarisGetIdentificationStatusResponse) {
        String status = solarisGetIdentificationStatusResponse.getStatus();
        switch (status) {
            case "successful":
                return Result.SUCCESS.name();
            case "pending_successful":
            case "pending_failed":
            case "pending":
                return Result.REVIEW_PENDING.name();
            case "aborted":
            case "failed":
                return Result.ABORTED.name();
            case "canceled":
                return Result.CANCELED.name();
            default:
                throw new RuntimeException("Unknown Solaris identification status" + status);
        }
    }
}
