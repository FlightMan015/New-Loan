package de.joonko.loan.metric.kyc;

import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.webhooks.solaris.enums.WebhookIdentificationStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static de.joonko.loan.metric.kyc.KycMetric.KYC_FAILED;
import static de.joonko.loan.metric.kyc.KycMetric.KYC_SUCCESS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SolarisMetricTest {
    private SolarisMetric solarisMetric;

    private KycMetric kycMetric;

    private static final String APPLICATION_ID = "5f561054e3fa3d3d1a7ef3f4";

    @BeforeEach
    void setUp() {
        kycMetric = mock(KycMetric.class);
        solarisMetric = new SolarisMetric(kycMetric);
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void incrementKycCounter(WebhookIdentificationStatus kycStatus, int calledNTimes, String successful) {
        // given
        String kycUser = "Deutsche Finanz Sozietät Privatkredit";

        // when
        solarisMetric.incrementKycCounter(kycStatus, kycUser);

        // then
        verify(kycMetric, times(calledNTimes)).incrementKycCounter(successful, kycUser, IdentificationProvider.SOLARIS);
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(
                        WebhookIdentificationStatus.successful, 1, KYC_SUCCESS
                ),
                Arguments.of(
                        WebhookIdentificationStatus.created, 0, KYC_SUCCESS
                ),
                Arguments.of(
                        WebhookIdentificationStatus.pending, 0, KYC_SUCCESS
                ),
                Arguments.of(
                        WebhookIdentificationStatus.pending_successful, 0, KYC_SUCCESS
                ),
                Arguments.of(
                        WebhookIdentificationStatus.failed, 1, KYC_FAILED
                )
        );
    }
}
