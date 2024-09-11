package de.joonko.loan.util;


import de.joonko.loan.config.EncryptionConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class EncrDecrService {

    private final EncryptionConfig config;

    private static String encodeHexString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }

    private static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    private static byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    private static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if (digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: " + hexChar);
        }
        return digit;
    }

    public String encrypt(String value) {
        byte[] iv = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        String hexIv = encodeHexString(iv);
        String encrypted = this.encryptValue(value, config.getSecurityEncryptionKey(), hexIv);
        return hexIv + ":" + encrypted;
    }

    public String decrypt(String value) {
        String[] textParts = value.split(":");
        return this.decryptValue(textParts[1], config.getSecurityEncryptionKey(), textParts[0]);
    }

    public String anonymize(String value) {
        return this.encryptValue(value, config.getAnonymizationEncryptionKey(), config.getAnonymizationIv());
    }

    public String deAnonymize(String value) {
        return this.decryptValue(value, config.getAnonymizationEncryptionKey(), config.getAnonymizationIv());
    }

    private String encryptValue(String value, String key, String iv) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(decodeHexString(iv));
            byte[] plainText = value.getBytes(StandardCharsets.UTF_8);
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), ivParameterSpec);
            ByteArrayOutputStream cipherText = new ByteArrayOutputStream();
            cipherText.write(cipher.update(plainText));
            cipherText.write(cipher.doFinal());
            return encodeHexString(cipherText.toByteArray());

        } catch (Exception exc) {
            return null;
        }
    }

    private String decryptValue(String value, String key, String iv) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(decodeHexString(iv));
            final Cipher decipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            decipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), ivParameterSpec);
            ByteArrayOutputStream cipherText = new ByteArrayOutputStream();
            cipherText.write(decipher.update(decodeHexString(value)));
            cipherText.write(decipher.doFinal());
            return cipherText.toString();
        } catch (Exception exc) {
            log.info("exception occured while decrypting {}", value, exc);
            return null;
        }
    }
}
