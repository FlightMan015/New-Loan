package de.joonko.loan.common.utils;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class NumberParserUtil {

    public static Optional<Long> tryParseLong(final String value) {
        try {
            return of(Long.parseLong(value));
        } catch (final NumberFormatException e) {
            return empty();
        }
    }

    public static Optional<Integer> tryParseInt(final String value) {
        try {
            return of(Integer.parseInt(value));
        } catch (final NumberFormatException e) {
            return empty();
        }
    }
}
