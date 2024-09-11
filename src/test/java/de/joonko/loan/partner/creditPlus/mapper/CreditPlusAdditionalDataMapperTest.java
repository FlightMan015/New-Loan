package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.DigitalAccountStatements;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreditPlusAdditionalDataMapperTest extends BaseMapperTest {

    @Autowired
    CreditPlusAdditionalDataMapper mapper;

    @Random
    DigitalAccountStatements digitalAccountStatements;

    private EfinComparerServiceStub.AddionalData addionalData = new EfinComparerServiceStub.AddionalData();

    @BeforeEach
    void setUp() {
        addionalData = mapper.toAdditionalData(digitalAccountStatements);
    }

    @Test
    void toAdditionalData() {
        assertNotNull(addionalData);
    }

    @Test
    void toTransferDay() {
        assertEquals(addionalData.getTransferDay(), Integer.valueOf(CreditPlusDefaults.TRANSFER_DAY));
    }

    @Test
    void toDispatchType() {
        assertEquals(addionalData.getDispatchType(), Integer.valueOf(CreditPlusDefaults.DISPATCH_TYPE));
    }

    @Test
    void toAccount() {
        assertNotNull(addionalData.getAccount());
    }

}