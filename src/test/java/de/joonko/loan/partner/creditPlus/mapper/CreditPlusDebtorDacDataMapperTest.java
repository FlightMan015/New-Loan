package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreditPlusDebtorDacDataMapperTest extends BaseMapperTest {

    @Autowired
    CreditPlusDebtorDacDataMapper mapper;

    @Random
    LoanDemand loanDemand;

    EfinComparerServiceStub.DacData dacData = new EfinComparerServiceStub.DacData();

    @BeforeEach
    void setUp() {
        dacData = mapper.toDacData(loanDemand);
    }

    @Test
    void toDacData() {
        assertNotNull(dacData);
    }

    @Test
    void toAccountOwner() {
        assertEquals(dacData.getAccountOwner(), loanDemand.getDigitalAccountStatements().getOwner());
    }

    @Test
    void toIban() {
        assertEquals(dacData.getIban(), loanDemand.getDigitalAccountStatements().getIban());
    }

    @Test
    void toTimestamp() {
        assertEquals(dacData.getTimestamp(), GregorianCalendar.from(loanDemand.getDigitalAccountStatements().getCreatedAt().atZone(ZoneId.systemDefault())));
    }
}