package de.joonko.loan.metric.kyc;

import de.joonko.loan.identification.model.IdentificationProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WebIdMetricTest {
    private WebIdMetric webIdMetric;

    private KycMetric kycMetric;

    private static final String APPLICATION_ID = "5f561054e3fa3d3d1a7ef3f4";

    @BeforeEach
    void setUp() {
        kycMetric = mock(KycMetric.class);
        webIdMetric = new WebIdMetric(kycMetric);
    }

    @Test
    void incrementKycCounter() {
        // given
        String kycUser = "SANTANDER";

        // when
        webIdMetric.incrementKycCounter(true, kycUser);

        // then
        verify(kycMetric).incrementKycCounter(KycMetric.KYC_SUCCESS, kycUser, IdentificationProvider.WEB_ID);
    }
}
