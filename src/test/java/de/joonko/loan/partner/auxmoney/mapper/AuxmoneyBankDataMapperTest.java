package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.offer.domain.DigitalAccountStatements;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.auxmoney.model.BankData;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;


class AuxmoneyBankDataMapperTest extends BaseMapperTest {


    @Autowired
    private AuxmoneyBankDataMapper auxmoneyBankDataMapper;

    @Test
    void toAuxmoneyIban(@Random DigitalAccountStatements digitalAccountStatements) {
        digitalAccountStatements.setIban("DE12500105170648489890");
        BankData bankData = auxmoneyBankDataMapper.toAuxmoneyBankData(digitalAccountStatements);
        assertEquals("DE12500105170648489890", bankData.getIban());
    }

    @Test
    void toAuxmoneyBic(@Random DigitalAccountStatements digitalAccountStatements) {
        digitalAccountStatements.setBic("BYLADEM1001");
        BankData bankData = auxmoneyBankDataMapper.toAuxmoneyBankData(digitalAccountStatements);
        assertEquals("BYLADEM1001", bankData.getBic());
    }
}
