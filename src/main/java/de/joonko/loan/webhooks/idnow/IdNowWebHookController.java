package de.joonko.loan.webhooks.idnow;

import de.joonko.loan.common.utils.CommonUtils;
import de.joonko.loan.data.support.DataSolutionCommunicationManager;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.exception.GenericExceptionHandler;
import de.joonko.loan.identification.model.IdentificationLink;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.identification.service.IdentificationStatusService;
import de.joonko.loan.integrations.model.OfferUpdateType;
import de.joonko.loan.metric.kyc.IdNowMetric;
import de.joonko.loan.webhooks.idnow.model.Identification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
@GenericExceptionHandler
public class IdNowWebHookController {

    private final IdentificationStatusService identificationStatusService;
    private final LoanDemandStoreService loanDemandStoreService;
    private final LoanOfferStoreService loanOfferStoreService;
    private final DataSolutionCommunicationManager dataSupportService;
    private final IdentificationLinkService identificationLinkService;
    private final IdNowMetric metric;

    @PostMapping("/loan/id-now/identification-notification")
    public Mono<ResponseEntity> handleIdNowWebHookNotification(@RequestBody Identification identification) {
        log.info("Identification process: {}", identification.getIdentificationProcess());
        try {

            IdentificationLink identificationLink = identificationLinkService.getByExternalIdentId(identification.getIdentificationProcess().getTransactionNumber());
            setLogContext(identificationLink.getApplicationId());

            identificationStatusService.saveWebhookNotification(identification);

            String kycStatus = identification.getIdentificationProcess().getResult();
            String applicationId = identificationLink.getApplicationId();
            String acceptedOfferId = identificationLink.getOfferId();
            LoanOfferStore acceptedOffer = loanOfferStoreService.findByLoanOfferId(acceptedOfferId);
            log.info("{} identification result for user {} for offer {} ",
                    kycStatus, acceptedOffer.getUserUUID(), acceptedOffer.getLoanOfferId());
            metric.incrementKycCounter(kycStatus, acceptedOffer.getOffer().getLoanProvider().getName());
            acceptedOffer.setKycStatus(kycStatus);
            loanOfferStoreService.save(acceptedOffer);
            dataSupportService.updateLoanOffers(acceptedOffer.getUserUUID(), applicationId, acceptedOffer.getLoanOfferId(), OfferUpdateType.KYC_UPDATE);
            return Mono.justOrEmpty(ResponseEntity.ok().build());

        } catch (Exception e) {
            log.error("Error while saving webhookNotification data", e);
            return Mono.just(new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private void setLogContext(String applicationId) {
        try {
            String dacId = loanDemandStoreService.getDacId(applicationId);
            CommonUtils.loadLogContext(applicationId, dacId);
        } catch (Exception exc) {
            log.info("Not able to set the Thread context in identification webhook : " + exc.getMessage());
        }
    }

}
