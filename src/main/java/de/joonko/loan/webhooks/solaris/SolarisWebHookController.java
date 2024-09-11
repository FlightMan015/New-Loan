package de.joonko.loan.webhooks.solaris;

import de.joonko.loan.common.utils.CommonUtils;
import de.joonko.loan.customer.support.CustomerSupportService;
import de.joonko.loan.data.support.DataSupportService;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.exception.GenericExceptionHandler;
import de.joonko.loan.identification.model.IdentificationLink;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.identification.service.IdentificationStatusService;
import de.joonko.loan.metric.kyc.SolarisMetric;
import de.joonko.loan.partner.solaris.SolarisAcceptOfferResponseStore;
import de.joonko.loan.partner.solaris.SolarisSignedDocService;
import de.joonko.loan.partner.solaris.SolarisStoreService;
import de.joonko.loan.partner.solaris.model.SolarisSignedDocTrail;
import de.joonko.loan.webhooks.solaris.enums.WebhookIdentificationStatus;
import de.joonko.loan.webhooks.solaris.model.SolarisIdNowWebhookRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@GenericExceptionHandler
public class SolarisWebHookController {

    private final IdentificationStatusService identificationStatusService;
    private final IdentificationLinkService identificationLinkService;
    private final LoanDemandStoreService loanDemandStoreService;
    private final LoanOfferStoreService loanOfferStoreService;
    private final CustomerSupportService customerSupportService;
    private final SolarisStoreService solarisStoreService;
    private final SolarisSignedDocService solarisSignedDocService;
    private final DataSupportService dataSupportService;
    private final SolarisMetric metric;

    @PostMapping("/loan/id-now/joonkosolaris/identification-notification")
    public Mono<ResponseEntity> handleIdNowSolarisWebHookNotification(@RequestBody SolarisIdNowWebhookRequest solarisIdNowWebhookRequest) {

        log.info("Received message, {} {}", solarisIdNowWebhookRequest);
        try {
            Optional<SolarisAcceptOfferResponseStore> solarisOffer = solarisStoreService
                    .getAcceptOfferResponseStoreByIdentificationId(solarisIdNowWebhookRequest.getIdentificationId())
                    .stream().findFirst();

            if (solarisOffer.isPresent()) {
                String applicationId = solarisOffer.get().getApplicationId();
                setLogContext(applicationId);
                identificationStatusService.saveSolarisWebhookNotification(solarisIdNowWebhookRequest, applicationId);
                IdentificationLink identificationLink = identificationLinkService.getByExternalIdentId(solarisIdNowWebhookRequest.getIdentificationId());

                LoanOfferStore acceptedOffer = loanOfferStoreService.findByLoanOfferId(identificationLink.getOfferId());
                String dacId = loanDemandStoreService.getDacId(applicationId);
                customerSupportService.pushKycStatus(acceptedOffer.getApplicationId(), acceptedOffer.getOffer(), solarisIdNowWebhookRequest.getUrl(), solarisIdNowWebhookRequest.getStatus().name(), acceptedOffer.getLoanProviderReferenceNumber(), "");
                if (solarisIdNowWebhookRequest.getStatus().name().equalsIgnoreCase(WebhookIdentificationStatus.successful.name())) {
                    solarisSignedDocService.save(SolarisSignedDocTrail.builder().applicationId(applicationId).emailSent(Boolean.FALSE).build());
                }
                metric.incrementKycCounter(solarisIdNowWebhookRequest.getStatus(), acceptedOffer.getOffer().getLoanProvider().getName());
                dataSupportService.pushKycStatus(dacId, acceptedOffer.getApplicationId(), acceptedOffer.getLoanOfferId(), solarisIdNowWebhookRequest.getUrl(), solarisIdNowWebhookRequest.getStatus().name(), acceptedOffer.getLoanProviderReferenceNumber(), "");
            }
        } catch (Exception e) {
            log.error("Error while saving webhookNotification data {} ", e);
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
