package de.joonko.loan.partner.consors;

import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.partner.consors.model.FinancialCalculation;
import de.joonko.loan.partner.consors.model.FinancialCalculations;
import de.joonko.loan.partner.consors.model.PersonalizedCalculationsResponse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsorsFinancialCalculationServiceTest {

    private static ConsorsFinancialCalculationService service;
    private static final int MIN_CREDIT_AMOUNT = 500;

    @BeforeAll
    static void beforeAll() {
        FinancialCalculationsFilter filter = new FinancialCalculationsFilter(MIN_CREDIT_AMOUNT);
        service = new ConsorsFinancialCalculationService(filter);
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void removeNotValidOffers(List<FinancialCalculation> inputList, List<FinancialCalculation> expectedList) {
        // given
        var givenResponse = PersonalizedCalculationsResponse.builder()
                .financialCalculations(FinancialCalculations.builder()
                        .financialCalculation(inputList)
                        .build())
                .build();
        final var loanDemandRequest = LoanDemandRequest.builder().loanAsked(5000).build();

        // when
        // then
        service.removeNotValidOffers(givenResponse, loanDemandRequest)
                .subscribe(r -> assertEquals(expectedList, r.getFinancialCalculations().getFinancialCalculation()));
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(
                        null,
                        null),
                Arguments.of(
                        List.of(getFinancialCalc(300, 7)),
                        emptyList()),
                Arguments.of(
                        List.of(getFinancialCalc(700, 7), getFinancialCalc(5000, 12)),
                        List.of(getFinancialCalc(5000, 12))),
                Arguments.of(
                        List.of(getFinancialCalc(500, 5), getFinancialCalc(5000, 24), getFinancialCalc(5000, 12), getFinancialCalc(700, 7), getFinancialCalc(300, 3)),
                        List.of(getFinancialCalc(5000, 12), getFinancialCalc(5000, 24))),
                Arguments.of(
                        List.of(getFinancialCalc(700, 7), getFinancialCalc(5000, 24), getFinancialCalc(5000, 12), getFinancialCalc(500, 5), getFinancialCalc(300, 3)),
                        List.of(getFinancialCalc(5000, 12), getFinancialCalc(5000, 24))),
                Arguments.of(
                        List.of(getFinancialCalc(700, 7), getFinancialCalc(5000, 24), getFinancialCalc(5000, 12), getFinancialCalc(500, 5), getFinancialCalc(500, 3)),
                        List.of(getFinancialCalc(5000, 12), getFinancialCalc(5000, 24))),
                Arguments.of(
                        List.of(getFinancialCalc(1000, 7), getFinancialCalc(5000, 12), getFinancialCalc(5000, 24), getFinancialCalc(10000, 12), getFinancialCalc(500, 5), getFinancialCalc(500, 3)),
                        List.of(getFinancialCalc(5000, 12), getFinancialCalc(5000, 24)))
        );
    }

    private static FinancialCalculation getFinancialCalc(int creditAmount, int duration) {
        return FinancialCalculation.builder()
                .creditAmount(creditAmount)
                .duration(duration)
                .build();
    }
}
