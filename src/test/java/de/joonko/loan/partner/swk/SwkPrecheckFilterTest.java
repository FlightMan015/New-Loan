package de.joonko.loan.partner.swk;

import de.joonko.loan.config.SwkConfig;
import de.joonko.loan.offer.domain.EmploymentType;
import de.joonko.loan.offer.domain.Finance;
import de.joonko.loan.offer.domain.Income;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.PrecheckFilterTestData;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SwkPrecheckFilterTest {

    private static SwkPrecheckFilter filter;

    private static PrecheckFilterTestData testData;

    @BeforeAll
    static void beforeAll() {
        SwkConfig config = mock(SwkConfig.class);
        filter = new SwkPrecheckFilter(config);
        testData = new PrecheckFilterTestData();

        when(config.getAcceptedApplicantMinAge()).thenReturn(18);
        when(config.getAcceptedApplicantMaxAge()).thenReturn(69);
        when(config.getAcceptedApplicantMinIncome()).thenReturn(1300);
        when(config.getAcceptedApplicantMinProbationInMonths()).thenReturn(6);
        when(config.getAcceptedApplicantMaxGamblingAmountInLast90Days()).thenReturn(500);
        when(config.getAcceptedCashWithdrawalsOutOfTotalIncomeInLast90DaysRatio()).thenReturn(0.3);
        when(config.getAcceptedApplicantMinLoanAmount()).thenReturn(2000);
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void applicationIsPassingPreCheckForSwk(LoanDemand input, boolean expectedFiltered, String errorMsg) {
        // given
        // when
        boolean isFiltered = filter.test(input);

        // then
        assertEquals(expectedFiltered, isFiltered, errorMsg);
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(
                        testData.getBaseLoanDemand(5000),
                        true,
                        "should return true for passing all preChecks"),
                Arguments.of(
                        testData.getLoanDemandWithBirthDate(14),
                        false,
                        "should return false for birthDate below min limit"),
                Arguments.of(
                        testData.getLoanDemandWithBirthDate(75),
                        false,
                        "should return false for birthDate above max limit"),
                Arguments.of(
                        testData.getLoanDemandWithBirthDate(30),
                        true,
                        "should return true for birthDate within limit"),
                Arguments.of(
                        testData.getLoanDemandWithFinance(null),
                        false,
                        "should return false for personalDetails.finance=null"),
                Arguments.of(
                        testData.getLoanDemandWithFinance(new Finance(null, null, BigDecimal.TEN)),
                        false,
                        "should return false for personalDetails.finance.income=null"),
                Arguments.of(
                        testData.getLoanDemandWithFinance(new Finance(Income.builder().build(), null, BigDecimal.TEN)),
                        false,
                        "should return false for personalDetails.finance.income.netIncome=null"),
                Arguments.of(
                        testData.getLoanDemandWithNetIncome(BigDecimal.valueOf(1400)),
                        true,
                        "should return true for netIncome above min limit"),
                Arguments.of(
                        testData.getLoanDemandWithNetIncome(BigDecimal.valueOf(700)),
                        false,
                        "should return false for netIncome below min limit"),
                Arguments.of(
                        testData.getLoanDemandWithSwkPreventionTags(null),
                        true,
                        "should return true for customDACData.hasSwkPreventionTags=null as this tag is ignored"),
                Arguments.of(
                        testData.getLoanDemandWithSwkPreventionTags(false),
                        true,
                        "should return true for customDACData.hasSwkPreventionTags=false as this tag is ignored"),
                Arguments.of(
                        testData.getLoanDemandWithSwkPreventionTags(true),
                        true,
                        "should return true for customDACData.hasSwkPreventionTags=true as this tag is ignored"),
                Arguments.of(
                        testData.getLoanDemandWithSeizureTags(1),
                        true,
                        "should return true for Seizure equals 1"),
                Arguments.of(
                        testData.getLoanDemandWithSeizureTags(2),
                        false,
                        "should return false for Seizure equals 2"),
                Arguments.of(
                        testData.getLoanDemandWithPAccountTags(1),
                        true,
                        "should return true for P-Account equals 1"),
                Arguments.of(
                        testData.getLoanDemandWithPAccountTags(2),
                        false,
                        "should return false for P-Account equals 2"),
                Arguments.of(
                        testData.getLoanDemandWithChargebackTags(3),
                        false,
                        "should return false for chargeback equals 3"),
                Arguments.of(
                        testData.getLoanDemandWithChargebackTags(2),
                        true,
                        "should return true for chargeback equals 2"),
                Arguments.of(
                        testData.getLoanDemandWithEnchashmentTags(2),
                        false,
                        "should return false for encashment equals 2"),
                Arguments.of(
                        testData.getLoanDemandWithEnchashmentTags(1),
                        true,
                        "should return true for encashment equals 1"),
                Arguments.of(
                        testData.getLoanDemandWithEmploymentSince(null),
                        false,
                        "should return false for employmentDetails.employmentSince=null"),
                Arguments.of(
                        testData.getLoanDemandWithEmploymentSince(LocalDate.now().minusMonths(3)),
                        false,
                        "should return false for employmentSince below limit"),
                Arguments.of(
                        testData.getLoanDemandWithEmploymentSince(LocalDate.now().minusMonths(7)),
                        true,
                        "should return true for employmentSince above limit"),
                Arguments.of(
                        testData.getLoanDemandWithGamblingAmount(null),
                        false,
                        "should return false for customDACData.gamblingAmountInLast90Days=null"),
                Arguments.of(
                        testData.getLoanDemandWithGamblingAmount(1000.0),
                        false,
                        "should return false for gambling amount above limit"),
                Arguments.of(
                        testData.getLoanDemandWithGamblingAmount(250.0),
                        true,
                        "should return true for gambling amount below limit"),
                Arguments.of(
                        testData.getLoanDemandWithEmploymentType(null),
                        false,
                        "should return false for employmentDetails.employmentType=null"),
                Arguments.of(
                        testData.getLoanDemandWithEmploymentType(EmploymentType.OTHER),
                        false,
                        "should return false for employmentType equals other"),
                Arguments.of(
                        testData.getLoanDemandWithEmploymentType(EmploymentType.REGULAR_EMPLOYED),
                        true,
                        "should return true for employmentType equals regular_employed"),
                Arguments.of(
                        testData.getLoanDemandWithAcceptedCashWithdrawalRatio(null, 1100.0),
                        false,
                        "should return false for customDACData.totalIncomeInLast90Days=null"),
                Arguments.of(
                        testData.getLoanDemandWithAcceptedCashWithdrawalRatio(3000.0, null),
                        false,
                        "should return false for customDACData.cashWithdrawalsInLast90Days=null"),
                Arguments.of(
                        testData.getLoanDemandWithAcceptedCashWithdrawalRatio(3000.0, 1100.0),
                        false,
                        "should return false for cash withdrawal ratio above limit"),
                Arguments.of(
                        testData.getLoanDemandWithAcceptedCashWithdrawalRatio(3000.0, 700.0),
                        true,
                        "should return true for cash withdrawal ratio below limit"),
                Arguments.of(
                        testData.getBaseLoanDemand(null),
                        false,
                        "should return false for loanAmount=null"),
                Arguments.of(
                        testData.getBaseLoanDemand(1500),
                        false,
                        "should return false for loanAmount below limit")
        );
    }


}
