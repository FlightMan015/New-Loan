package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.DigitalAccountStatements;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.consors.model.BankAccount;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class ConsorsBankAccountMapperTest extends BaseMapperTest {

    @Autowired
    private ConsorsBankAccountMapper consorsBankAccountMapper;

    @Random
    private DigitalAccountStatements digitalAccountStatements;

    @Test
    @DisplayName("Should map bankAccount.owner to Main")
    void owner() {

        BankAccount bankAccount = consorsBankAccountMapper.fromJoonko(digitalAccountStatements);
        assertEquals("MAIN", bankAccount.getOwner()
                .name());

    }
    @Test
    @DisplayName("Should convert blz  to null")
    void blz(){

        BankAccount bankAccount = consorsBankAccountMapper.fromJoonko(digitalAccountStatements);
        assertNull(bankAccount.getBlz());
    }
    @Test
    @DisplayName("Should map accountSince to YYYY-MM")
    void accountSince(){
        digitalAccountStatements.setAccountSince(LocalDate.of(1998, Month.APRIL, 12));
        BankAccount bankAccount = consorsBankAccountMapper.fromJoonko(digitalAccountStatements);
        assertEquals("1998-04", bankAccount.getAccountSince());
    }

    @Test
    @DisplayName("Should convert DigitalAccountStatements.iban to bankAccount.iban")
    void iban(){

        BankAccount bankAccount = consorsBankAccountMapper.fromJoonko(digitalAccountStatements);
        assertEquals(digitalAccountStatements.getIban(),bankAccount.getIban());
    }
    @Test
    @DisplayName("Should convert accountNumber to null")
    void accountNumber(){
        BankAccount bankAccount = consorsBankAccountMapper.fromJoonko(digitalAccountStatements);
        assertNull(bankAccount.getAccountNumber());
    }
    @Test
    @DisplayName("Should convert DigitalAccountStatements.bic to bankAccount.bic")
    void bic(){
        BankAccount bankAccount = consorsBankAccountMapper.fromJoonko(digitalAccountStatements);
        assertEquals(digitalAccountStatements.getBic(),bankAccount.getBic());
    }
}
