package de.joonko.loan.util;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static de.joonko.loan.util.Util.*;

class UtilTest {


    @DisplayName("Should Convert Euro To Cent")
    @ParameterizedTest()
    @MethodSource("createEuroWithCents")
    public void testToEuroCent(BigDecimal euro, Integer cents) {
        Assert.assertTrue(cents == toEuroCent(euro));
    }

    private static Stream<Arguments> createEuroWithCents() {
        return Stream.of(Arguments.of(BigDecimal.ONE, 100), Arguments.of(BigDecimal.valueOf(2.2), 220),
                Arguments.of(BigDecimal.ZERO, 0), Arguments.of(null, 0));
    }


}
