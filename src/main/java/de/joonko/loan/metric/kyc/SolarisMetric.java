package de.joonko.loan.metric.kyc;

import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.webhooks.solaris.enums.WebhookIdentificationStatus;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

import static de.joonko.loan.metric.kyc.KycMetric.KYC_FAILED;
import static de.joonko.loan.metric.kyc.KycMetric.KYC_SUCCESS;

@AllArgsConstructor
@Component
public class SolarisMetric {

    private final KycMetric kycMetric;

    public void incrementKycCounter(WebhookIdentificationStatus kycStatus, String kycUser) {
        if (isIntermediateStatus(kycStatus)) {
            return;
        }
        boolean successful = isSuccessfulStatus(kycStatus);
        kycMetric.incrementKycCounter(successful?KYC_SUCCESS: KYC_FAILED, kycUser, IdentificationProvider.SOLARIS);
    }

    private boolean isSuccessfulStatus(WebhookIdentificationStatus kycStatus) {
        return kycStatus == WebhookIdentificationStatus.successful;
    }

    private boolean isIntermediateStatus(WebhookIdentificationStatus kycStatus) {
        return kycStatus == WebhookIdentificationStatus.created || kycStatus == WebhookIdentificationStatus.pending || kycStatus == WebhookIdentificationStatus.pending_successful;
    }
}
