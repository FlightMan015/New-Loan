package de.joonko.loan.webhooks.webid;

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
import de.joonko.loan.metric.kyc.WebIdMetric;
import de.joonko.loan.webhooks.webid.model.KycStatus;
import de.joonko.loan.webhooks.webid.model.request.Ident;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
@GenericExceptionHandler
public class WebidWebHookController {

    private final IdentificationStatusService identificationStatusService;
    private final LoanDemandStoreService loanDemandStoreService;
    private final LoanOfferStoreService loanOfferStoreService;
    private final IdentificationLinkService identificationLinkService;
    private final DataSolutionCommunicationManager dataSupportService;

    private final WebIdMetric metric;

    @PostMapping("/loan/webid/webhook-notification")
    public Mono<ResponseEntity> handleWebIdWebHookIdentNotification(@RequestBody Ident ident) {
        String applicationId = ident.getTransactionId();

        setLogContext(applicationId);
        log.info("Received message with transactionId, {}, notification type {} and with success {}", ident.getTransactionId(), ident.getResponseType(), ident.isSuccess());
        try {
            identificationStatusService.saveWebIdIdentWebhookNotification(ident);
            String notificationStatus = ident.isSuccess() ? KycStatus.success.name() : KycStatus.failure.name();

            IdentificationLink identificationLink = identificationLinkService.getByExternalIdentId(applicationId);
            String acceptedOfferId = identificationLink.getOfferId();

            LoanOfferStore acceptedOffer = loanOfferStoreService.findByLoanOfferId(acceptedOfferId);
            log.info("user {} {} successful in kyc for offer {}", acceptedOffer.getUserUUID(),
                    ident.success ? "was" : "was not",
                    acceptedOffer.getLoanOfferId());
            String existingStatus = acceptedOffer.getKycStatus();
            if (!StringUtils.containsIgnoreCase(existingStatus, KycStatus.failure.name())) {
                acceptedOffer.setKycStatus(Strings.toUpperCase(notificationStatus));
            }
            loanOfferStoreService.save(acceptedOffer);

            metric.incrementKycCounter(ident.isSuccess(), acceptedOffer.getOffer().getLoanProvider().getName());
            dataSupportService.updateLoanOffers(acceptedOffer.getUserUUID(), applicationId, acceptedOffer.getLoanOfferId(), OfferUpdateType.KYC_UPDATE);

        } catch (Exception e) {
            log.error("Error while saving webid ident webhookNotification data ", e);
            return Mono.just(new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
        return Mono.justOrEmpty(ResponseEntity.ok().build());
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
