package de.joonko.loan.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilTest {

    @Test
    void maskIban() {
        // given
        final var iban = "DE65500000000012305678";

        // when
        var actualMaskedIban = StringUtil.maskIban(iban, "*");


        // then
        assertEquals("DE****************5678", actualMaskedIban);
    }

    @Test
    void maskBic() {
        // given
        final var bic = "TESTDE88185972";

        // when
        var actualMaskedBic = StringUtil.maskLastNCharacters(bic, "*", 4);


        // then
        assertEquals("TESTDE8818****", actualMaskedBic);
    }

    @ParameterizedTest
    @MethodSource("getTestDataToTrim")
    void limitAndTrimLastWord(String input, String expected, int limit) {
        // when
        var actualTrimmed = StringUtil.limitAndTrimLastWord(input, limit);

        // then
        assertEquals(expected, actualTrimmed);
    }

    private static Stream<Arguments> getTestDataToTrim() {
        return Stream.of(
                Arguments.of(
                        null, null, 10
                ),
                Arguments.of(
                        "", "", 10
                ),
                Arguments.of(
                        "equallimit", "equallimit", 10
                ),
                Arguments.of(
                        "belowlim", "belowlim", 10
                ),
                Arguments.of(
                        "above limit", "above", 10
                ),
                Arguments.of(
                        "single_word_above_limit", "single_wor", 10
                ),
                Arguments.of(
                        "      bigspaces     ", "bigspaces", 10
                ),
                Arguments.of(
                        " smallspaces ", "smallspace", 10
                ),
                Arguments.of(
                        "tab  tab  tab", "tab  tab", 10
                ),
                Arguments.of(
                        "Lorem ipsum dolor sit amet consectetur adipiscing", "Lorem ipsum dolor sit amet", 27
                )
        );
    }
}
