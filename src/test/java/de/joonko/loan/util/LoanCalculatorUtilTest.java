package de.joonko.loan.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanCalculatorUtilTest {

    @ParameterizedTest
    @MethodSource("getTestData")
    void calculateBestOffers(final Integer amount, Integer duration, BigDecimal effectiveRate, SimpleLoan expectedResult) {


        final var result = LoanCalculatorUtil.calculateSimpleLoan(BigDecimal.valueOf(amount), duration, effectiveRate);

        assertEquals(expectedResult, result);
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(5000, 42, BigDecimal.valueOf(5.99),
                        new SimpleLoan(BigDecimal.valueOf(5000), 42, BigDecimal.valueOf(5.99), BigDecimal.valueOf(5.82), new BigDecimal("132.25"), new BigDecimal("5554.50"))),

                Arguments.of(5000, 24, BigDecimal.valueOf(5.99),
                        new SimpleLoan(BigDecimal.valueOf(5000), 24, BigDecimal.valueOf(5.99), BigDecimal.valueOf(5.82), new BigDecimal("221.58"), new BigDecimal("5317.92"))
                ),
                Arguments.of(5000, 36, BigDecimal.valueOf(5.99),
                        new SimpleLoan(BigDecimal.valueOf(5000), 36, BigDecimal.valueOf(5.99), BigDecimal.valueOf(5.82), new BigDecimal("152.08"), new BigDecimal("5474.88"))
                ),
                Arguments.of(5000, 66, BigDecimal.valueOf(5.99),
                        new SimpleLoan(BigDecimal.valueOf(5000), 66, BigDecimal.valueOf(5.99), BigDecimal.valueOf(5.82), new BigDecimal("89.10"), new BigDecimal("5880.60"))
                ),
                Arguments.of(5000, 18, BigDecimal.valueOf(5.99),
                        new SimpleLoan(BigDecimal.valueOf(5000), 18, BigDecimal.valueOf(5.99), BigDecimal.valueOf(5.83), new BigDecimal("291.13"), new BigDecimal("5240.34"))
                ),
                Arguments.of(5000, 12, BigDecimal.valueOf(5.99),
                        new SimpleLoan(BigDecimal.valueOf(5000), 12, BigDecimal.valueOf(5.99), BigDecimal.valueOf(5.83), new BigDecimal("430.30"), new BigDecimal("5163.60"))
                ),
                Arguments.of(5000, 54, BigDecimal.valueOf(5.99),
                        new SimpleLoan(BigDecimal.valueOf(5000), 54, BigDecimal.valueOf(5.99), BigDecimal.valueOf(5.82), new BigDecimal("105.86"), new BigDecimal("5716.44"))
                ),
                Arguments.of(5000, 60, BigDecimal.valueOf(5.99),
                        new SimpleLoan(BigDecimal.valueOf(5000), 60, BigDecimal.valueOf(5.99), BigDecimal.valueOf(5.82), new BigDecimal("96.64"), new BigDecimal("5798.40"))
                ),
                Arguments.of(5000, 90, BigDecimal.valueOf(5.99),
                        new SimpleLoan(BigDecimal.valueOf(5000), 90, BigDecimal.valueOf(5.99), BigDecimal.valueOf(5.82), new BigDecimal("69.10"), new BigDecimal("6219.00"))
                ),
                Arguments.of(5000, 96, BigDecimal.valueOf(5.99),
                        new SimpleLoan(BigDecimal.valueOf(5000), 96, BigDecimal.valueOf(5.99), BigDecimal.valueOf(5.82), new BigDecimal("65.68"), new BigDecimal("6305.28"))
                ),
                Arguments.of(5000, 72, BigDecimal.valueOf(5.99),
                        new SimpleLoan(BigDecimal.valueOf(5000), 72, BigDecimal.valueOf(5.99), BigDecimal.valueOf(5.82), new BigDecimal("82.84"), new BigDecimal("5964.48"))
                ),
                Arguments.of(5000, 6, BigDecimal.valueOf(5.99),
                        new SimpleLoan(BigDecimal.valueOf(5000), 6, BigDecimal.valueOf(5.99), BigDecimal.valueOf(5.85), new BigDecimal("847.95"), new BigDecimal("5087.70"))
                ),
                Arguments.of(5000, 96, BigDecimal.valueOf(3.29),
                        new SimpleLoan(BigDecimal.valueOf(5000), 96, BigDecimal.valueOf(3.29), BigDecimal.valueOf(3.24), new BigDecimal("59.30"), new BigDecimal("5692.80"))
                ),
                Arguments.of(5000, 48, BigDecimal.valueOf(4.19),
                        new SimpleLoan(BigDecimal.valueOf(5000), 48, BigDecimal.valueOf(4.19), BigDecimal.valueOf(4.11), new BigDecimal("113.32"), new BigDecimal("5439.36"))
                ),
                Arguments.of(5000, 72, BigDecimal.valueOf(2.99),
                        new SimpleLoan(BigDecimal.valueOf(5000), 72, BigDecimal.valueOf(2.99), BigDecimal.valueOf(2.95), new BigDecimal("75.94"), new BigDecimal("5467.68"))
                ),
                Arguments.of(5000, 60, BigDecimal.valueOf(2.99),
                        new SimpleLoan(BigDecimal.valueOf(5000), 60, BigDecimal.valueOf(2.99), BigDecimal.valueOf(2.95), new BigDecimal("89.82"), new BigDecimal("5389.20"))
                ),
                Arguments.of(5000, 120, BigDecimal.valueOf(3.29),
                        new SimpleLoan(BigDecimal.valueOf(5000), 120, BigDecimal.valueOf(3.29), BigDecimal.valueOf(3.24), new BigDecimal("48.95"), new BigDecimal("5874.00"))
                ),
                Arguments.of(5000, 108, BigDecimal.valueOf(3.29),
                        new SimpleLoan(BigDecimal.valueOf(5000), 108, BigDecimal.valueOf(3.29), BigDecimal.valueOf(3.24), new BigDecimal("53.55"), new BigDecimal("5783.40"))
                ),
                Arguments.of(5000, 36, BigDecimal.valueOf(4.55),
                        new SimpleLoan(BigDecimal.valueOf(5000), 36, BigDecimal.valueOf(4.55), BigDecimal.valueOf(4.45), new BigDecimal("148.84"), new BigDecimal("5358.24"))
                ),
                Arguments.of(5000, 24, BigDecimal.valueOf(4.65),
                        new SimpleLoan(BigDecimal.valueOf(5000), 24, BigDecimal.valueOf(4.65), BigDecimal.valueOf(4.55), new BigDecimal("218.59"), new BigDecimal("5246.16"))
                )
        );
    }
}