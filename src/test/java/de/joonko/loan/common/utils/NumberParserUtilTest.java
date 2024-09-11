package de.joonko.loan.common.utils;

import org.junit.jupiter.api.Test;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static de.joonko.loan.common.utils.NumberParserUtil.tryParseInt;
import static de.joonko.loan.common.utils.NumberParserUtil.tryParseLong;
import static org.hamcrest.MatcherAssert.assertThat;

public class NumberParserUtilTest {

    @Test
    void tryParseLong_whenParsable() {
        // when
        final var result = tryParseLong("1234567");

        // then
        assertThat(result, isPresentAndIs(1234567L));
    }

    @Test
    void tryParseLong_whenNotParsable() {
        // when
        final var result = tryParseLong("123.67");

        // then
        assertThat(result, isEmpty());
    }

    @Test
    void tryParseInt_whenParsable() {
        // when
        final var result = tryParseInt("1234567");

        // then
        assertThat(result, isPresentAndIs(1234567));
    }

    @Test
    void tryParseInt_whenNotParsable() {
        // when
        final var result = tryParseInt("123.67");

        // then
        assertThat(result, isEmpty());
    }
}
