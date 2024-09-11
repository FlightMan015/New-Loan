package de.joonko.loan.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhoneNumberUtilTest {

    @Test
    void extractPrefixFromPhoneNumber_replaces_country_code_with_0_for_german_phoneNumber() {
        // given
        final String phoneNumberWithPrefix = "+4917656879078";
        final String phoneNumberWithoutPrefix = "017656879078";
        final String phoneNumberWithoutPrefixWithout0 = "17656879078";

        // when
        final var result = PhoneNumberUtil.extractPrefixFromPhoneNumber(phoneNumberWithPrefix);
        final var resultWithout0 = PhoneNumberUtil.extractPrefixFromPhoneNumberWithout0(phoneNumberWithPrefix);

        // then
        assertEquals(phoneNumberWithoutPrefix, result);
        assertEquals(phoneNumberWithoutPrefixWithout0, resultWithout0);
    }

    @Test
    void extractPrefixFromPhoneNumber_replaces_country_code_with_49_for_german_phoneNumber_containing_49_inside() {
        // given
        final String phoneNumberWithPrefix = "+4917649494949";
        final String phoneNumberWithoutPrefixWithout0 = "17649494949";

        // when
        final var resultWithout0 = PhoneNumberUtil.extractPrefixFromPhoneNumberWithout0(phoneNumberWithPrefix);

        // then
        assertEquals(phoneNumberWithoutPrefixWithout0, resultWithout0);
    }

    @Test
    void extractPrefixFromPhoneNumber_replaces_country_code_with_0_for_german_phoneNumber_containing_49_inside() {
        // given
        final String phoneNumberWithPrefix = "4917649494949";
        final String phoneNumberWithoutPrefixWithout0 = "17649494949";

        // when
        final var resultWithout0 = PhoneNumberUtil.extractPrefixFromPhoneNumberWithout0(phoneNumberWithPrefix);

        // then
        assertEquals(phoneNumberWithoutPrefixWithout0, resultWithout0);
    }

    @Test
    void extractPrefixFromPhoneNumber_replaces_country_code_with_0_for_german_phoneNumber_without_plus() {
        // given
        final String phoneNumberWithPrefix = "4917656879078";
        final String phoneNumberWithoutPrefix = "017656879078";
        final String phoneNumberWithoutPrefixWithout0 = "17656879078";

        // when
        final var result = PhoneNumberUtil.extractPrefixFromPhoneNumber(phoneNumberWithPrefix);
        final var resultWithout0 = PhoneNumberUtil.extractPrefixFromPhoneNumberWithout0(phoneNumberWithPrefix);

        // then
        assertEquals(phoneNumberWithoutPrefix, result);
        assertEquals(phoneNumberWithoutPrefixWithout0, resultWithout0);
    }

    @Test
    void extractPrefixFromPhoneNumber_replaces_country_code_with_0_for_german_phoneNumber_starting_with_00() {
        // given
        final String phoneNumberWithPrefix = "004917656879078";
        final String phoneNumberWithoutPrefix = "017656879078";
        final String phoneNumberWithoutPrefixWithout0 = "17656879078";

        // when
        final var result = PhoneNumberUtil.extractPrefixFromPhoneNumber(phoneNumberWithPrefix);
        final var resultWithout0 = PhoneNumberUtil.extractPrefixFromPhoneNumberWithout0(phoneNumberWithPrefix);

        // then
        assertEquals(phoneNumberWithoutPrefix, result);
        assertEquals(phoneNumberWithoutPrefixWithout0, resultWithout0);
    }

    @Test
    void extractPrefixFromPhoneNumber_will_not_replace_country_code_with_0_for_non_german_phoneNumber() {
        // given
        final String phoneNumberWithPrefix = "+37495098790";
        final String phoneNumberWithoutPrefix = "+37495098790";

        // when
        final var result = PhoneNumberUtil.extractPrefixFromPhoneNumber(phoneNumberWithPrefix);
        final var resultWithout0 = PhoneNumberUtil.extractPrefixFromPhoneNumberWithout0(phoneNumberWithPrefix);

        // then
        assertEquals(phoneNumberWithoutPrefix, result);
        assertEquals(phoneNumberWithoutPrefix, resultWithout0);
    }

    @Test
    void extractPrefixFromPhoneNumber_will_not_replace_country_code_with_0_if_already_with_0() {
        // given
        final String phoneNumberWithPrefix = "017656879078";
        final String phoneNumberWithoutPrefix = "017656879078";
        final String phoneNumberWithoutPrefixWithout0 = "17656879078";

        // when
        final var result = PhoneNumberUtil.extractPrefixFromPhoneNumber(phoneNumberWithPrefix);
        final var resultWithout0 = PhoneNumberUtil.extractPrefixFromPhoneNumberWithout0(phoneNumberWithPrefix);

        // then
        assertEquals(phoneNumberWithoutPrefix, result);
        assertEquals(phoneNumberWithoutPrefixWithout0, resultWithout0);
    }

    @Test
    void addGermanPrefixFromPhoneNumber_will_replace_0_with_german_prefix() {
        // given
        final String phoneNumberWithPrefix = "017656879078";
        final String phoneNumberWithoutPrefix = "+4917656879078";

        // when
        final var result = PhoneNumberUtil.addGermanPrefixFromPhoneNumber(phoneNumberWithPrefix);

        // then
        assertEquals(phoneNumberWithoutPrefix, result);
    }

    @Test
    void addGermanPrefixFromPhoneNumber_will_add_german_prefix_if_none() {
        // given
        final String phoneNumberWithPrefix = "17656879078";
        final String phoneNumberWithoutPrefix = "+4917656879078";

        // when
        final var result = PhoneNumberUtil.addGermanPrefixFromPhoneNumber(phoneNumberWithPrefix);

        // then
        assertEquals(phoneNumberWithoutPrefix, result);
    }

    @Test
    void addGermanPrefixFromPhoneNumber_will_not_modify_if_already_with_german_prefix() {
        // given
        final String phoneNumberWithPrefix = "+4917656879078";
        final String phoneNumberWithoutPrefix = "+4917656879078";

        // when
        final var result = PhoneNumberUtil.addGermanPrefixFromPhoneNumber(phoneNumberWithPrefix);

        // then
        assertEquals(phoneNumberWithoutPrefix, result);
    }

    @Test
    void addGermanPrefixFromPhoneNumber_will_not_modify_if_already_with_any_country_prefix() {
        // given
        final String phoneNumberWithPrefix = "+37495098790";
        final String phoneNumberWithoutPrefix = "+37495098790";

        // when
        final var result = PhoneNumberUtil.addGermanPrefixFromPhoneNumber(phoneNumberWithPrefix);

        // then
        assertEquals(phoneNumberWithoutPrefix, result);
    }

    @Test
    void addGermanPrefixFromPhoneNumber_will_not_modify_if_already_with_any_country_prefix_with_00() {
        // given
        final String phoneNumberWithPrefix = "0037495098790";
        final String phoneNumberWithoutPrefix = "0037495098790";

        // when
        final var result = PhoneNumberUtil.addGermanPrefixFromPhoneNumber(phoneNumberWithPrefix);

        // then
        assertEquals(phoneNumberWithoutPrefix, result);
    }

    @Test
    void addGermanPrefixFromPhoneNumber_will_not_modify_if_already_with_german_country_code() {
        // given
        final String phoneNumberWithPrefix = "49495098790";
        final String phoneNumberWithoutPrefix = "49495098790";

        // when
        final var result = PhoneNumberUtil.addGermanPrefixFromPhoneNumber(phoneNumberWithPrefix);

        // then
        assertEquals(phoneNumberWithoutPrefix, result);
    }

}
