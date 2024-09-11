package de.joonko.loan.metric.kyc;

import de.joonko.loan.identification.model.IdentificationProvider;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

import static de.joonko.loan.metric.kyc.KycMetric.KYC_FAILED;
import static de.joonko.loan.metric.kyc.KycMetric.KYC_SUCCESS;

@AllArgsConstructor
@Component
public class IdNowMetric {

    private final KycMetric kycMetric;

    public void incrementKycCounter(String kycStatus, String kycUser) {
        if (isIntermediateStatus(kycStatus)) {
            return;
        }
        boolean successful = isSuccessfulStatus(kycStatus);
        kycMetric.incrementKycCounter(successful?KYC_SUCCESS: KYC_FAILED, kycUser, IdentificationProvider.ID_NOW);
    }

    private boolean isSuccessfulStatus(String kycStatus) {
        return kycStatus.equals("SUCCESS") || kycStatus.equals("SUCCESS_DATA_CHANGED");
    }

    private boolean isIntermediateStatus(String kycStatus) {
        return kycStatus.equals("REVIEW_PENDING");
    }
}
