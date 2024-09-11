package de.joonko.loan.util;

import java.util.Base64;

public class Base64Encoder {

    public static byte[] decodeFromBase64(final String base64EncodedString) {
        return Base64.getDecoder().decode(base64EncodedString.getBytes());
    }

    public static byte[] encodeToBase64(final byte[] bytes) {
        return Base64.getEncoder().encode(bytes);
    }

    public static String encodeToBase64AsString(final byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
