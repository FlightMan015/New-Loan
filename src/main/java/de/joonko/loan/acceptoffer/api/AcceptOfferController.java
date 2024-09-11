package de.joonko.loan.acceptoffer.api;

import de.joonko.loan.acceptoffer.domain.AcceptOfferService;
import de.joonko.loan.common.utils.CommonUtils;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.exception.GenericExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@Slf4j
@RequiredArgsConstructor
@GenericExceptionHandler
public class AcceptOfferController {
    private final AcceptOfferService offerService;
    private final LoanDemandStoreService loanDemandStoreService;

    @PostMapping(value = "api/v1/loan/accept-offer", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AcceptOfferResponse> acceptOffer(Principal principal,
                                                 @Valid @RequestBody AcceptOfferRequest acceptOfferRequest) {
        String userUUID = principal.getName();
        return offerService.acceptOffer(acceptOfferRequest, userUUID);
    }

    @PutMapping(value = "api/v1/loan/accept-offer/save-consent", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Boolean> acceptOfferSolarisConsent(@Valid @RequestBody SaveConsentRequest saveConsentRequest) {
        String applicationId = saveConsentRequest.getLoanApplicationId();
        String dacId = loanDemandStoreService.getDacId(applicationId);
        CommonUtils.loadLogContext(applicationId, dacId);
        log.info("Saving consent {} ", saveConsentRequest.getTermsAccepted());
        return Mono.justOrEmpty(offerService.saveConsent(saveConsentRequest));
    }
}
