package de.joonko.loan.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static de.joonko.loan.util.Base64Encoder.decodeFromBase64;
import static de.joonko.loan.util.Base64Encoder.encodeToBase64;
import static de.joonko.loan.util.Base64Encoder.encodeToBase64AsString;

class Base64EncoderTest {

    @Test
    void testBase64EncodingAndDecoding() {
        final String text = "Some random text.";

        byte[] bytes = encodeToBase64(text.getBytes());
        String string = encodeToBase64AsString(text.getBytes());
        byte[] bytes1 = decodeFromBase64(string);
        byte[] bytes2 = decodeFromBase64(new String(bytes));

        Assertions.assertEquals(text, new String(bytes1));
        Assertions.assertEquals(text, new String(bytes2));
    }
}