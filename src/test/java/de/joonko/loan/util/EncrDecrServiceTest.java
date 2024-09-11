package de.joonko.loan.util;

import de.joonko.loan.config.EncryptionConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncrDecrServiceTest {

    private EncrDecrService encrDecrService;

    private EncryptionConfig encryptionConfig;

    private static final String SECURITY_ENCRYPTION_KEY = "frUq0buBom6deF6fabebos9#froch6s2";
    private static final String ANONYMIZATION_ENCRYPTION_KEY = "kuqu$&l1rip5&o2hoT1lcray#w*clkuy";
    private static final String ANONYMIZATION_IV = "67ef92b22f96e9de24134b356445d8fc";

    @BeforeEach
    void setup() {
        encryptionConfig = new EncryptionConfig();
        encryptionConfig.setSecurityEncryptionKey(SECURITY_ENCRYPTION_KEY);
        encryptionConfig.setAnonymizationEncryptionKey(ANONYMIZATION_ENCRYPTION_KEY);
        encryptionConfig.setAnonymizationIv(ANONYMIZATION_IV);
        encrDecrService = new EncrDecrService(encryptionConfig);
    }

    @Test
    @DisplayName("Encryption should return diff cypher for same value and decryption to original text")
    public void encryptDecrypt() {
        String plainText = "11811-xr-y6jr-8klV";
        String firstCypher = encrDecrService.encrypt(plainText);
        String secondCypher = encrDecrService.encrypt(plainText);
        assertNotEquals(firstCypher, secondCypher);
        assertNotNull(firstCypher);
        assertNotNull(secondCypher);
        assertTrue(firstCypher.contains(":") && secondCypher.contains(":"));

        String firstDecypher = encrDecrService.decrypt(firstCypher);
        String secondDecypher = encrDecrService.decrypt(secondCypher);
        assertEquals(firstDecypher, secondDecypher);
    }

    @Test
    @DisplayName("Anonymize should return same cypher for same value and de-anonymize to original")
    public void anonymiseDeanonymise() {
        String plainText = "11811-xr-y6jr-8klV";
        String firstCypher = encrDecrService.anonymize(plainText);
        String secondCypher = encrDecrService.anonymize(plainText);
        assertNotNull(firstCypher);
        assertEquals(firstCypher, secondCypher);

        String firstDecypher = encrDecrService.deAnonymize(firstCypher);
        String secondDecypher = encrDecrService.deAnonymize(secondCypher);
        assertEquals(firstDecypher, secondDecypher);
    }

    @Test
    @DisplayName("Should be decrypted according to AES/CBC algorithm")
    public void decrypt() {
        assertEquals("11811-xr-y6jr-8klV", encrDecrService.decrypt("71dfc6749d29a2bf7cf513c1854381cc:eec8e1ce3c5eecfe60c5bf6a17d7a37f8e0e7af8d8b8b303e03ba22b0849c6ea"));
    }
}