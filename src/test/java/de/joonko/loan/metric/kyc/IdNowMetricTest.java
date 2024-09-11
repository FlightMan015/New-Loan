package de.joonko.loan.metric.kyc;

import de.joonko.loan.identification.model.IdentificationProvider;

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

class IdNowMetricTest {

    private IdNowMetric idNowMetric;

    private KycMetric kycMetric;

    private static final String APPLICATION_ID = "5f561054e3fa3d3d1a7ef3f4";

    @BeforeEach
    void setUp() {
        kycMetric = mock(KycMetric.class);
        idNowMetric = new IdNowMetric(kycMetric);
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void incrementKycCounter(String kycStatus, int calledNTimes, String successful) {
        // given
        String kycUser = "Consors Finanz";

        // when
        idNowMetric.incrementKycCounter(kycStatus, kycUser);

        // then
        verify(kycMetric, times(calledNTimes)).incrementKycCounter(successful, kycUser, IdentificationProvider.ID_NOW);
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(
                        "SUCCESS", 1, KYC_SUCCESS
                ),
                Arguments.of(
                        "SUCCESS_DATA_CHANGED", 1, KYC_SUCCESS
                ),
                Arguments.of(
                        "REVIEW_PENDING", 0, KYC_FAILED
                ),
                Arguments.of(
                        "CANCELED", 1, KYC_FAILED
                )
        );
    }
}
