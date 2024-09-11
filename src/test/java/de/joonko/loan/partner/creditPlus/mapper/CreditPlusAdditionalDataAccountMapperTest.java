package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.DigitalAccountStatements;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreditPlusAdditionalDataAccountMapperTest extends BaseMapperTest {

    @Autowired
    CreditPlusAdditionalDataAccountMapper mapper;

    @Random
    DigitalAccountStatements digitalAccountStatements;

    @Test
    void toAccount() {
        assertNotNull(mapper.toAccount(digitalAccountStatements));
    }

    @Test
    void toAccountOwner() {
        EfinComparerServiceStub.Account account = mapper.toAccount(digitalAccountStatements);
        assertEquals(account.getAccountOwner(), Integer.valueOf(CreditPlusDefaults.ACCOUNT_OWNER));
    }

    @Test
    void toIban() {
        EfinComparerServiceStub.Account account = mapper.toAccount(digitalAccountStatements);
        assertEquals(account.getIban(), digitalAccountStatements.getIban());
    }

}