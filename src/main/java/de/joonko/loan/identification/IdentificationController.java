package de.joonko.loan.identification;

import de.joonko.loan.exception.GenericExceptionHandler;
import de.joonko.loan.identification.model.GetIdentStatusResponse;
import de.joonko.loan.identification.model.GetKycUrlResponse;
import de.joonko.loan.identification.model.GetOfferContractsResponse;
import de.joonko.loan.identification.model.InitiateIdentificationRequest;
import de.joonko.loan.identification.model.StartIdentResponse;
import de.joonko.loan.identification.service.IdentificationService;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
@GenericExceptionHandler
@Validated
public class IdentificationController {

    private final IdentificationService identificationService;

    @PostMapping(value = "api/v1/loan/identification", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<StartIdentResponse> initiateIdentification(
            Principal principal,
            @Valid @RequestBody InitiateIdentificationRequest initiateIdentificationRequest,
            @RequestParam(name = "internal-use", defaultValue = "false") String internalUse) {
        log.info("Starting identification");
        return identificationService.createIdentification(principal.getName(), initiateIdentificationRequest.getLoanOfferId(), initiateIdentificationRequest.getApplicationId());
    }


    @PostMapping(value = "api/v1/loan/identification/start", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<StartIdentResponse> initiateIdentificationStart(
            Principal principal,
            @Valid @RequestBody InitiateIdentificationRequest initiateIdentificationRequest) {
        log.info(String.format("Starting identification v2 for offer - %s", initiateIdentificationRequest.getLoanOfferId()));
        return identificationService.createIdentificationV2(principal.getName(), initiateIdentificationRequest.getLoanOfferId(), initiateIdentificationRequest.getApplicationId());
    }

    @GetMapping(value = "api/v1/loan/identification/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<GetIdentStatusResponse> getIdentificationStatus(@RequestParam String externalIdentId) {
        log.info("Getting Identification Status for externalIdentId {} ", externalIdentId);
        return identificationService.getIdentificationStatus(externalIdentId);
    }

    @GetMapping(value = "api/v1/loan/identification/url", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<GetKycUrlResponse> getIdentificationUrl(@RequestParam String externalIdentId) {
        log.info("Getting Identification URL for externalIdentId {} ", externalIdentId);
        return identificationService.getKycUrl(externalIdentId);
    }

    @GetMapping(value = "api/v1/loan/offer/{offerId}/contract", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<GetOfferContractsResponse> getUserOfferContracts(final @NotNull Principal principal, @PathVariable String offerId) {
        log.info("Accepting offer and getting the contracts {} ", offerId);

        return identificationService.acceptAndGetContracts(offerId, principal.getName());
    }

    @GetMapping(value = "/loan/internal/offer/{offerId}/contract", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<GetOfferContractsResponse> getOfferContracts(@PathVariable String offerId) {
        log.info("Applying and getting user contracts by internal user for offerId {} ", offerId);

        return identificationService.acceptAndCreateIdentificationByInternalUser(offerId);
    }

}







