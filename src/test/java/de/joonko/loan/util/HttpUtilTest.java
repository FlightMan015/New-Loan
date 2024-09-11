package de.joonko.loan.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpUtilTest {

    @Test
    void testEncodingWorks() {
        // given
        final var email = "meri_mirzoyan+10@bonify.de";
        final var expectedEncodedEmail = "meri_mirzoyan%2B10%40bonify.de";

        // when
        final var encodedEmail = HttpUtil.encodeValue(email);

        assertEquals(expectedEncodedEmail, encodedEmail);
    }
}