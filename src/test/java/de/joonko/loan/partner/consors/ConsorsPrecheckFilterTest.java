package de.joonko.loan.partner.consors;

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

class ConsorsPrecheckFilterTest {

    private static ConsorsPrecheckFilter filter;

    private static PrecheckFilterTestData testData;

    @BeforeAll
    static void beforeAll() {
        ConsorsPropertiesConfig config = mock(ConsorsPropertiesConfig.class);
        filter = new ConsorsPrecheckFilter(config);
        testData = new PrecheckFilterTestData();

        when(config.getAcceptedApplicantMinAge()).thenReturn(18);
        when(config.getAcceptedApplicantMaxAge()).thenReturn(74);
        when(config.getAcceptedApplicantMinIncome()).thenReturn(650);
        when(config.getAcceptedApplicantMinProbationInMonths()).thenReturn(6);
        when(config.getAcceptedApplicantMinLoanAmount()).thenReturn(1000);
        when(config.getAcceptedApplicantMaxLoanAmount()).thenReturn(95000);
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void applicationIsPassingPreCheckForConsors(LoanDemand input, boolean expectedFiltered, String errorMsg) {
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
                        testData.getLoanDemandWithJointlyManaged(null),
                        false,
                        "should return false for digitalAccountStatements.isJointlyManaged=null"),
                Arguments.of(
                        testData.getLoanDemandWithJointlyManaged(true),
                        false,
                        "should return false for isJointlyManaged equals true"),
                Arguments.of(
                        testData.getLoanDemandWithJointlyManaged(false),
                        true,
                        "should return true for isJointlyManaged equals false"),
                Arguments.of(
                        testData.getLoanDemandWithHasSalary(null),
                        false,
                        "should return false for customDACData.hasSalary=null"),
                Arguments.of(
                        testData.getLoanDemandWithHasSalary(false),
                        false,
                        "should return false for hasSalary equals false"),
                Arguments.of(
                        testData.getLoanDemandWithHasSalary(true),
                        true,
                        "should return true for hasSalary equals true"),
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
                        testData.getLoanDemandWithNetIncome(BigDecimal.valueOf(850)),
                        true,
                        "should return true for netIncome above min limit"),
                Arguments.of(
                        testData.getLoanDemandWithNetIncome(BigDecimal.valueOf(500)),
                        false,
                        "should return false for netIncome below min limit"),
                Arguments.of(
                        testData.getLoanDemandWithSeizureTags(0),
                        true,
                        "should return true for Seizure equals 0"),
                Arguments.of(
                        testData.getLoanDemandWithSeizureTags(1),
                        false,
                        "should return false for Seizure equals 1"),
                Arguments.of(
                        testData.getLoanDemandWithPAccountTags(1),
                        false,
                        "should return false for Seizure equals 1"),
                Arguments.of(
                        testData.getLoanDemandWithPAccountTags(1),
                        false,
                        "should return false for P-Account equals true"),
                Arguments.of(
                        testData.getLoanDemandWithPAccountTags(0),
                        true,
                        "should return true for P-Account equals false"),
                Arguments.of(
                        testData.getLoanDemandWithChargebackTags(1),
                        false,
                        "should return false for chargeback equals true"),
                Arguments.of(
                        testData.getLoanDemandWithChargebackTags(0),
                        true,
                        "should return true for chargeback equals false"),
                Arguments.of(
                        testData.getLoanDemandWithEnchashmentTags(1),
                        false,
                        "should return false for encashment equals 1"),
                Arguments.of(
                        testData.getLoanDemandWithEnchashmentTags(0),
                        true,
                        "should return true for encashment equals 0"),
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
                        testData.getBaseLoanDemand(900),
                        false,
                        "should return false for loanAmount below min limit"),
                Arguments.of(
                        testData.getBaseLoanDemand(1000),
                        true,
                        "should return true for loanAmount above min limit"),
                Arguments.of(
                        testData.getBaseLoanDemand(95000),
                        true,
                        "should return true for loanAmount below max limit"),
                Arguments.of(
                        testData.getBaseLoanDemand(95500),
                        false,
                        "should return false for loanAmount above max limit")
        );
    }


}
