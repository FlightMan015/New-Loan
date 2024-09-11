package de.joonko.loan.partner.consors;

import de.joonko.loan.partner.consors.model.FinancialCalculation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinancialCalculationsFilterTest {

    private static final int MIN_CREDIT_AMOUNT = 500;
    private final FinancialCalculationsFilter filter = new FinancialCalculationsFilter(MIN_CREDIT_AMOUNT);

    @ParameterizedTest
    @MethodSource("getTestData")
    void getFilteredOffers(FinancialCalculation input, boolean expectedFiltered) {
        // given
        // when
        boolean isFiltered = filter.test(input);

        // then
        assertEquals(expectedFiltered, isFiltered);
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(
                        FinancialCalculation.builder().creditAmount(500).build(),
                        true),
                Arguments.of(
                        FinancialCalculation.builder().creditAmount(400).build(),
                        false),
                Arguments.of(
                        FinancialCalculation.builder().creditAmount(600).build(),
                        true)
        );
    }
}
