package de.joonko.loan.partner.santander;

import de.joonko.loan.config.SantanderConfig;
import de.joonko.loan.offer.domain.Finance;
import de.joonko.loan.offer.domain.Income;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.PrecheckFilterTestData;
import de.joonko.loan.util.DateUtil;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SantanderPrecheckFilterTest {

    private static SantanderPrecheckFilter filter;

    private static PrecheckFilterTestData testData;

    @BeforeAll
    static void beforeAll() {
        SantanderConfig config = mock(SantanderConfig.class);
        filter = new SantanderPrecheckFilter(config);
        testData = new PrecheckFilterTestData();

        when(config.getAcceptedApplicantMinProbationInMonths()).thenReturn(6);
        when(config.getAcceptedApplicantMinLoanAmount()).thenReturn(1000);
        when(config.getAcceptedApplicantMaxLoanAmount()).thenReturn(60000);
        when(config.getAcceptedApplicantMinIncome()).thenReturn(1);
    }

    @ParameterizedTest
    @MethodSource("getTestDataForContractEndDate")
    void applicationIsPassingContractEndDateForSantander(Date professionEndDate, boolean expectedFiltered, String errorMsg) {
        // given
        String applicationId = "applicationId";
        LoanDuration loanDuration = LoanDuration.TWELVE;

        // when
        boolean isFiltered = filter.doesContractEndBeforeRepayment(professionEndDate, loanDuration, applicationId);

        // then
        assertEquals(expectedFiltered, isFiltered, errorMsg);
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void applicationIsPassingPreCheckForSantander(LoanDemand loanDemand, boolean expectedFiltered, String errorMsg) {
        // given
        // when
        boolean isFiltered = filter.test(loanDemand);

        // then
        assertEquals(expectedFiltered, isFiltered, errorMsg);
    }

    private static Stream<Arguments> getTestDataForContractEndDate() {
        return Stream.of(
                Arguments.of(
                        null,
                        false,
                        "should return false for date=null"
                ),
                Arguments.of(
                        DateUtil.toDate(LocalDate.now().plusMonths(15)),
                        false,
                        "should return false for professionEndDate ends after loan duration"
                ),
                Arguments.of(
                        DateUtil.toDate(LocalDate.now().plusMonths(5)),
                        true,
                        "should return true for professionEndDate ends before loan duration"
                )
        );
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(
                        testData.getBaseLoanDemand(5000),
                        true,
                        "should return true for passing all preChecks"),
                Arguments.of(
                        testData.getLoanDemandWithEmploymentSince(null),
                        false,
                        "should return false for employmentDetails.employmentSince=null"),
                Arguments.of(
                        testData.getLoanDemandWithEmploymentSince(LocalDate.now().minusMonths(3)),
                        false,
                        "should return false for employmentSince below limit"),
                Arguments.of(
                        testData.getLoanDemandWithEmploymentSince(LocalDate.now().minusMonths(6)),
                        true,
                        "should return true for employmentSince above limit"),
                Arguments.of(
                        testData.getLoanDemandWithNetIncomeHasNoGovSupport(null),
                        false,
                        "should return false for customDACData.netIncomeHasNoGovSupport=null"),
                Arguments.of(
                        testData.getLoanDemandWithNetIncomeHasNoGovSupport(true),
                        false,
                        "should return false for netIncomeHasNoGovSupport equals true"),
                Arguments.of(
                        testData.getLoanDemandWithNetIncomeHasNoGovSupport(false),
                        true,
                        "should return true for netIncomeHasNoGovSupport equals false"),
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
                        testData.getLoanDemandWithNetIncome(BigDecimal.valueOf(0)),
                        false,
                        "should return false for netIncome below min limit"),
                Arguments.of(
                        testData.getLoanDemandWith3IncomeTagsOr3MSalary(null, null),
                        false,
                        "should return true for customDACData.has3IncomeTags=null and customDACData.hasSalaryEachMonthLast3M=null"),
                Arguments.of(
                        testData.getLoanDemandWith3IncomeTagsOr3MSalary(null, false),
                        false,
                        "should return true for customDACData.has3IncomeTags=null and customDACData.hasSalaryEachMonthLast3M=false"),
                Arguments.of(
                        testData.getLoanDemandWith3IncomeTagsOr3MSalary(null, true),
                        false,
                        "should return true for customDACData.has3IncomeTags=null and customDACData.hasSalaryEachMonthLast3M=true"),
                Arguments.of(
                        testData.getLoanDemandWith3IncomeTagsOr3MSalary(true, null),
                        false,
                        "should return true for customDACData.has3IncomeTags=true and customDACData.hasSalaryEachMonthLast3M=null"),
                Arguments.of(
                        testData.getLoanDemandWith3IncomeTagsOr3MSalary(false, null),
                        false,
                        "should return false for customDACData.has3IncomeTags=false and customDACData.hasSalaryEachMonthLast3M=null"),
                Arguments.of(
                        testData.getLoanDemandWith3IncomeTagsOr3MSalary(false, false),
                        false,
                        "should return false for customDACData.has3IncomeTags=false and customDACData.hasSalaryEachMonthLast3M=false"),
                Arguments.of(
                        testData.getLoanDemandWith3IncomeTagsOr3MSalary(false, true),
                        true,
                        "should return true for customDACData.has3IncomeTags=false and customDACData.hasSalaryEachMonthLast3M=true"),
                Arguments.of(
                        testData.getLoanDemandWith3IncomeTagsOr3MSalary(true, true),
                        true,
                        "should return true for customDACData.has3IncomeTags=true and customDACData.hasSalaryEachMonthLast3M=true"),
                Arguments.of(
                        testData.getLoanDemandWith3IncomeTagsOr3MSalary(true, false),
                        true,
                        "should return true for customDACData.has3IncomeTags=true and customDACData.hasSalaryEachMonthLast3M=false"),
                Arguments.of(
                        testData.getBaseLoanDemand(null),
                        false,
                        "should return false for loanAmount=null"),
                Arguments.of(
                        testData.getBaseLoanDemand(900),
                        false,
                        "should return false for loanAmount below limit"),
                Arguments.of(
                        testData.getBaseLoanDemand(60500),
                        false,
                        "should return false for loanAmount above limit")
        );
    }
}
