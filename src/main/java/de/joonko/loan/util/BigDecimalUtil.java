package de.joonko.loan.util;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class BigDecimalUtil {

    public static BigDecimal add(final BigDecimal... addends) {
        if (addends == null) {
            return BigDecimal.ZERO;
        }
        return Arrays.stream(addends)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static Optional<BigDecimal> average(final @NotNull @NotEmpty Collection<BigDecimal> values) {
        final var totalWithCount
                = values.stream()
                .filter(Objects::nonNull)
                .map(bd -> new BigDecimal[]{bd, BigDecimal.ONE})
                .reduce((a, b) -> new BigDecimal[]{a[0].add(b[0]), a[1].add(BigDecimal.ONE)});

        return totalWithCount.map(tc -> tc[0].divide(tc[1], 2, RoundingMode.HALF_UP));
    }
}
