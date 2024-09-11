package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class CreditPlusDebtorMapperTest extends BaseMapperTest {

    @Autowired
    CreditPlusDebtorMapper mapper;

    @Random
    LoanDemand loanDemand;

    EfinComparerServiceStub.DebtorDac debtorDac = new EfinComparerServiceStub.DebtorDac();

    @BeforeEach
    void setUp() {
        debtorDac = mapper.toDebtor(loanDemand);
    }

    @Test
    void toDebtor() {
        assertNotNull(mapper.toDebtor(loanDemand));
    }

    @Test
    void toWorkdata() {
        assertNotNull(debtorDac.getWorkdata());
    }

    @Test
    void toRsvType() {
        assertEquals(debtorDac.getRsvType(), Integer.valueOf(CreditPlusDefaults.RSV_TYPE));
    }

    @Test
    void toReplacementType() {
        assertEquals(debtorDac.getReplacementType(), Integer.valueOf(CreditPlusDefaults.REPLACEMENT_TYPE));
    }

    @Test
    void toFinancialData() {
        assertNotNull(debtorDac.getFinancialData());
    }

    @Test
    void toPersonalData() {
        assertNotNull(debtorDac.getPersonalData());
    }

    @Test
    void toDacData() {
        assertNotNull(debtorDac.getDacData());
    }

}