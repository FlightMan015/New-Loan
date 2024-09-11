package de.joonko.loan.metric.kyc;

import de.joonko.loan.identification.model.IdentificationProvider;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

import static de.joonko.loan.metric.kyc.KycMetric.KYC_FAILED;
import static de.joonko.loan.metric.kyc.KycMetric.KYC_SUCCESS;

@AllArgsConstructor
@Component
public class WebIdMetric {

    private final KycMetric kycMetric;

    public void incrementKycCounter(boolean successful, String kycUser) {
        kycMetric.incrementKycCounter(successful?KYC_SUCCESS: KYC_FAILED, kycUser, IdentificationProvider.WEB_ID);
    }
}
