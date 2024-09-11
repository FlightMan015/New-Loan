package de.joonko.loan.partner.postbank;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.PrecheckFilterTestData;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostbankPrecheckFilterTest {

    private static PostbankPrecheckFilter filter;

    private static PrecheckFilterTestData testData;

    @BeforeAll
    static void beforeAll() {
        PostbankPropertiesConfig config = mock(PostbankPropertiesConfig.class);
        filter = new PostbankPrecheckFilter(config);
        testData = new PrecheckFilterTestData();

        when(config.getMinLoanAmount()).thenReturn(3000);
        when(config.getMaxLoanAmount()).thenReturn(50000);
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

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(
                        testData.getBaseLoanDemand(5000),
                        true,
                        "should return true for passing all preChecks"),
                Arguments.of(
                        testData.getBaseLoanDemand(null),
                        false,
                        "should return false for loanAmount=null"),
                Arguments.of(
                        testData.getBaseLoanDemand(2500),
                        false,
                        "should return false for loanAmount below limit"),
                Arguments.of(
                        testData.getBaseLoanDemand(50500),
                        false,
                        "should return false for loanAmount below limit"),
                Arguments.of(
                        testData.getLoanDemandWithProfessionEndDate(null),
                        true,
                        "should return true when user employment contract professionEndDate is null"),
                Arguments.of(
                        testData.getLoanDemandWithProfessionEndDate(LocalDate.now().plusMonths(12)),
                        false,
                        "should return false when user employment contract professionEndDate has any value")
        );
    }

}