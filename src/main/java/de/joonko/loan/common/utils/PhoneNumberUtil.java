package de.joonko.loan.common.utils;

import javax.validation.constraints.NotNull;

import java.util.Optional;

public class PhoneNumberUtil {

    private final static String GERMAN_PHONE_PREFIX = "+49";
    private final static String GERMAN_PHONE_PREFIX_ESCAPED = "\\+49";
    private final static String GERMAN_PHONE_PREFIX_WITH_0 = "0049";
    private final static String GERMAN_PHONE_PREFIX_WITHOUT_PLUS = "49";
    private final static String ZERO_PHONE_PREFIX = "0";
    private final static String DOUBLE_ZERO_PHONE_PREFIX = "00";

    public static String extractPrefixFromPhoneNumber(final @NotNull String phoneNumberWithPrefix) {
        return searchForCountryCode(phoneNumberWithPrefix)
                .map(prefix -> phoneNumberWithPrefix.replaceFirst(prefix, ZERO_PHONE_PREFIX))
                .orElse(phoneNumberWithPrefix);
    }

    public static String extractPrefixFromPhoneNumberWithout0(final @NotNull String phoneNumberWithPrefix) {
        return searchForCountryCode(phoneNumberWithPrefix)
                .map(prefix -> phoneNumberWithPrefix.replaceFirst(prefix, ""))
                .orElse(removeLeading0(phoneNumberWithPrefix));
    }

    public static String addGermanPrefixFromPhoneNumber(final @NotNull String phoneNumberWithoutPrefix) {
        if (phoneNumberWithoutPrefix.startsWith(ZERO_PHONE_PREFIX) && !phoneNumberWithoutPrefix.startsWith(DOUBLE_ZERO_PHONE_PREFIX)) {
            return phoneNumberWithoutPrefix.replaceFirst(ZERO_PHONE_PREFIX, GERMAN_PHONE_PREFIX);
        } else if (phoneNumberWithoutPrefix.startsWith("+") || phoneNumberWithoutPrefix.startsWith(DOUBLE_ZERO_PHONE_PREFIX) || phoneNumberWithoutPrefix.startsWith(GERMAN_PHONE_PREFIX_WITHOUT_PLUS)) {
            return phoneNumberWithoutPrefix;
        }
        return GERMAN_PHONE_PREFIX + phoneNumberWithoutPrefix;
    }

    private static Optional<String> searchForCountryCode(final String phoneNumber) {
        if (phoneNumber.startsWith(GERMAN_PHONE_PREFIX)) {
            return Optional.of(GERMAN_PHONE_PREFIX_ESCAPED);
        }
        if (phoneNumber.startsWith(GERMAN_PHONE_PREFIX_WITH_0)) {
            return Optional.of(GERMAN_PHONE_PREFIX_WITH_0);
        }
        if (phoneNumber.startsWith(GERMAN_PHONE_PREFIX_WITHOUT_PLUS)) {
            return Optional.of(GERMAN_PHONE_PREFIX_WITHOUT_PLUS);
        }
        return Optional.empty();
    }

    private static String removeLeading0(final String phoneNumber) {
        return phoneNumber.startsWith(ZERO_PHONE_PREFIX) ? phoneNumber.replaceFirst(ZERO_PHONE_PREFIX, "") : phoneNumber;
    }
}
