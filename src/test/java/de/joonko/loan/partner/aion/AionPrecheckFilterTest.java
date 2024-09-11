package de.joonko.loan.partner.aion;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.offer.api.model.FundingPurpose;
import de.joonko.loan.offer.domain.EmploymentType;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.PreCheckEnum;
import de.joonko.loan.offer.domain.Precheck;
import de.joonko.loan.partner.PrecheckFilterTestData;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AionPrecheckFilterTest {

    private static AionPrecheckFilter filter;

    private static PrecheckFilterTestData testData;

    @BeforeAll
    static void beforeAll() {
        AionPropertiesConfig config = mock(AionPropertiesConfig.class);
        filter = new AionPrecheckFilter(config);
        testData = new PrecheckFilterTestData();

        when(config.getEnabled()).thenReturn(true);
        when(config.getAcceptedApplicantMinAge()).thenReturn(23);
        when(config.getAcceptedApplicantMaxAge()).thenReturn(70);
        when(config.getMinAverage3MSalary()).thenReturn(BigDecimal.valueOf(1200));
        when(config.getMinLastSalary()).thenReturn(BigDecimal.valueOf(1200));
        when(config.getMinEmploymentMonths()).thenReturn(2);
        when(config.getAcceptedApplicantMaxLoanAmount()).thenReturn(20000);
        when(config.getAcceptedBonimaScore()).thenReturn(90000);
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void applicationIsPassingPreCheckForAion(LoanDemand input, boolean expectedFiltered, String errorMsg) {
        // given
        // when
        boolean isFiltered = filter.test(input);

        // then
        assertEquals(expectedFiltered, isFiltered, errorMsg);
    }

    @ParameterizedTest
    @EnumSource(value = FundingPurpose.class, names = {"LOAN_REPAYMENT", "BALANCING_CURRENT_ACCOUNT", "HOUSE_SHIFT", "REAL_ESTATE"})
    void applicationIsRejectedForPurpose(FundingPurpose fundingPurpose) {
        // given
        final LoanDemand loanDemand = testData.getBaseLoanDemand(5000);
        loanDemand.setFundingPurpose(fundingPurpose.getValue());
        // when
        boolean isFiltered = filter.test(loanDemand);

        // then
        assertFalse(isFiltered);
        assertEquals(false, loanDemand.getPreChecks().stream().filter(precheck ->
                Bank.AION.name().equals(precheck.getProvider()) &&
                        PreCheckEnum.ACCEPTED_LOAN_PURPOSE.equals(precheck.getPreCheck())
        ).map(Precheck::getValue).findFirst().get());
    }


    @ParameterizedTest
    @EnumSource(value = FundingPurpose.class, names = {"LOAN_REPAYMENT", "BALANCING_CURRENT_ACCOUNT", "HOUSE_SHIFT", "REAL_ESTATE"}, mode = EnumSource.Mode.EXCLUDE)
    void applicationIsAcceptedForPurpose(FundingPurpose fundingPurpose) {
        // given
        final LoanDemand loanDemand = testData.getBaseLoanDemand(5000);
        loanDemand.setFundingPurpose(fundingPurpose.getValue());
        // when
        boolean isFiltered = filter.test(loanDemand);

        // then
        assertTrue(isFiltered);
        assertEquals(true, loanDemand.getPreChecks().stream().filter(precheck ->
                Bank.AION.name().equals(precheck.getProvider()) &&
                        PreCheckEnum.ACCEPTED_LOAN_PURPOSE.equals(precheck.getPreCheck())
        ).map(Precheck::getValue).findFirst().get());
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(
                        testData.getBaseLoanDemand(5000),
                        true,
                        "should return true for passing all preChecks"),
                Arguments.of(
                        testData.getLoanDemandWithBirthDate(22),
                        false,
                        "should return false for birthDate below min limit"),
                Arguments.of(
                        testData.getLoanDemandWithBirthDate(71),
                        false,
                        "should return false for birthDate above max limit"),
                Arguments.of(
                        testData.getLoanDemandWithBirthDate(30),
                        true,
                        "should return true for birthDate within limit"),

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
                        testData.getLoanDemandWithEmploymentSince(null),
                        false,
                        "should return false for employmentDetails.employmentSince=null"),
                Arguments.of(
                        testData.getLoanDemandWithEmploymentSince(LocalDate.now().minusDays(39)),
                        false,
                        "should return false for employmentSince below limit"),
                Arguments.of(
                        testData.getLoanDemandWithEmploymentSince(LocalDate.now().minusMonths(7)),
                        true,
                        "should return true for employmentSince above limit"),
                Arguments.of(
                        testData.getLoanDemandWithLast3MSalaries(null, BigDecimal.valueOf(1200), BigDecimal.valueOf(1180)),
                        false,
                        "should return false for last month salary = null"),
                Arguments.of(
                        testData.getLoanDemandWithNetIncome(null),
                        false,
                        "should return false for last month salary = null"),
                Arguments.of(
                        testData.getLoanDemandWithNetIncome(BigDecimal.valueOf(1200)),
                        false,
                        "should return false for last month salary = 1200"),
                Arguments.of(
                        testData.getLoanDemandWithNetIncome(BigDecimal.valueOf(1201)),
                        true,
                        "should return true for last month salary = 1201"),
                Arguments.of(
                        testData.getLoanDemandWithLast3MSalaries(BigDecimal.valueOf(1210), BigDecimal.valueOf(1200), BigDecimal.valueOf(1193)),
                        true,
                        "should return true for 3M average income above min limit"),
                Arguments.of(
                        testData.getLoanDemandWithLast3MSalaries(BigDecimal.valueOf(1190), BigDecimal.valueOf(1200), BigDecimal.valueOf(1210)),
                        false,
                        "should return false for 3M average income below min limit"),
                Arguments.of(
                        testData.getLoanDemandWithDeclaredDelayInInstallments(null),
                        true,
                        "should return true if declared delay is null"),
                Arguments.of(
                        testData.getLoanDemandWithDeclaredDelayInInstallments(true),
                        false,
                        "should return false if declared delay is true"),
                Arguments.of(
                        testData.getLoanDemandWithDeclaredDelayInInstallments(false),
                        true,
                        "should return true if declared delay is false"),
                Arguments.of(
                        testData.getLoanDemandWithBonimaScore(null),
                        true,
                        "should return true if bonimaScore is null"),
                Arguments.of(
                        testData.getLoanDemandWithBonimaScore(90000),
                        false,
                        "should return false if bonimaScore is 90000"),
                Arguments.of(
                        testData.getLoanDemandWithBonimaScore(800),
                        true,
                        "should return true if bonimaScore is 800"),
                Arguments.of(
                        testData.getLoanDemandWithLast3MSalaries(BigDecimal.valueOf(1210), BigDecimal.valueOf(1200), BigDecimal.valueOf(1205)),
                        true,
                        "should return true for 3M average income above min limit"),
                Arguments.of(
                        testData.getLoanDemandWithLast3MSalaries(BigDecimal.valueOf(1190), BigDecimal.valueOf(1192), BigDecimal.valueOf(1195)),
                        false,
                        "should return false for 3M average income below min limit"),
                Arguments.of(
                        testData.getBaseLoanDemand(20500),
                        false,
                        "should return false for loanAmount above limit"),
                Arguments.of(
                        testData.getBaseLoanDemand(20000),
                        true,
                        "should return true for loanAmount below limit")
        );
    }

}