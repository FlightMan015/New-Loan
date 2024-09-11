package de.joonko.loan.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BigDecimalUtilTest {

    @Test
    void add_adds_all_bigdecimals_excluding_nulls() {
        final var result = BigDecimalUtil.add(BigDecimal.valueOf(200.1), BigDecimal.ZERO, BigDecimal.ONE, null, BigDecimal.valueOf(1500));

        assertEquals(0, result.compareTo(BigDecimal.valueOf(1701.1)));
    }

    @Test
    void add_null_case() {
        final var result = BigDecimalUtil.add(null);

        assertEquals(0, result.compareTo(BigDecimal.ZERO));
    }

    @Test
    void average_all_nulls() {
        final var set = new HashSet<BigDecimal>();
        set.add(null);
        final var result = BigDecimalUtil.average(set);

        assertThat(result).isEmpty();
    }

    @Test
    void average_ignores_nulls() {
        final var set = new HashSet<BigDecimal>();
        set.add(BigDecimal.ONE);
        set.add(BigDecimal.TEN);
        set.add(null);
        final var result = BigDecimalUtil.average(set);

        assertThat(result).isNotEmpty();
        assertEquals(new BigDecimal("5.50"), result.get());
    }
}
