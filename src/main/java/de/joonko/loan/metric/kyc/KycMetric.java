package de.joonko.loan.metric.kyc;

import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.metric.Metric;
import de.joonko.loan.metric.model.CounterMetricLabel;
import de.joonko.loan.metric.model.TagMetricLabel;

import org.springframework.stereotype.Component;

import java.util.Map;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class KycMetric {
    public static final String KYC_INIT="initiated";
    public static final String KYC_SUCCESS="success";
    public static final String KYC_FAILED="failure";

    private final Metric metric;

    public void incrementKycCounter(String status, String kycUser, IdentificationProvider kycProvider) {

        Map<String, String> tags = Map.of(
                TagMetricLabel.STATUS.getName(), status,
                TagMetricLabel.OFFER_PROVIDER.getName(), kycUser,
                TagMetricLabel.KYC_PROVIDER.getName(), kycProvider.name()
        );
        metric.incrementCounter(CounterMetricLabel.KYC, tags);
    }
}
