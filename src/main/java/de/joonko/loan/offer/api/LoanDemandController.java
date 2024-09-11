package de.joonko.loan.offer.api;

import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.exception.GenericExceptionHandler;
import de.joonko.loan.identification.service.IdentificationStatusService;
import de.joonko.loan.integrations.configuration.GetOffersConfigurations;
import de.joonko.loan.integrations.domain.OfferRequestProcessor;
import de.joonko.loan.offer.OfferDemandRequest;
import de.joonko.loan.offer.api.model.UserJourneyStateResponse;
import de.joonko.loan.user.service.UserAdditionalInfoService;
import de.joonko.loan.user.service.UserStatesService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static de.joonko.loan.util.HttpUtil.extractInetAddressFromRequest;

@RestController
@Slf4j
@RequiredArgsConstructor
@GenericExceptionHandler
public class LoanDemandController {


    private final LoanDemandStoreService loanDemandStoreService;
    private final IdentificationStatusService identificationStatusService;
    private final OfferRequestProcessor offerRequestProcessor;
    private final GetOffersConfigurations getOffersConfigurations;
    private final UserStatesService userStatesService;
    private final UserAdditionalInfoService userAdditionalInfoService;

    @PostMapping(value = "internal/anonymization/migrate", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean migratePersonalData(@RequestParam(name = "internal-use", defaultValue = "false") String internalUse) {
        if ("true".equals(internalUse)) {
            loanDemandStoreService.gpdrMigration();
            identificationStatusService.gpdrMigration();
        }
        return true;
    }

    @GetMapping(value = "api/v1/loan/offers", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> getOffers(Principal principal,
                                          @RequestParam(value = "amount", required = false) Integer amount,
                                          @RequestParam(value = "purpose", required = false, defaultValue = "other") String purpose,
                                          @RequestParam(value = "onlyBonify", required = false, defaultValue = "false") Boolean onlyBonify,
                                          final ServerHttpRequest request) {
        if (amount == null) {
            amount = getOffersConfigurations.getDefaultAskedLoanAmount();
        }

        final var offerDemandRequest = OfferDemandRequest.builder()
                .requestedLoanAmount(amount)
                .userUUID(principal.getName())
                .requestedLoanPurpose(purpose)
                .onlyBonify(onlyBonify)
                .inetAddress(extractInetAddressFromRequest(request))
                .build();

        return offerRequestProcessor.getOffers(offerDemandRequest).map(ResponseEntity::ok);
    }

    @GetMapping(value = "api/v1/loan/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<UserJourneyStateResponse>> getLatestUserJourneyState(final Principal principal) {
        return userStatesService.getLatestUserJourneyState(principal.getName()).map(ResponseEntity::ok);
    }

}
