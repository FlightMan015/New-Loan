package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.config.CreditPlusConfig;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreditPlusCreateCreditOfferRequestMapperTest extends BaseMapperTest {

    @Autowired
    CreditPlusCreateCreditOfferRequestMapper mapper;

    @Random
    LoanDemand loanDemand;

    @Autowired
    protected CreditPlusConfig creditPlusConfig;

    EfinComparerServiceStub.CreditOfferDac creditOfferDac = new EfinComparerServiceStub.CreditOfferDac();

    @BeforeEach
    void setUp() {
        creditOfferDac = mapper.toCreditOfferDac(loanDemand);
    }

    @Test
    void toCreditOfferDac() {
        assertNotNull(creditOfferDac);
    }

    @Test
    void toAmount() {
        assertEquals(creditOfferDac.getAmount().intValue(), loanDemand.getLoanAsked());
    }

    @Test
    void toDealerOrderNumber() {
        assertEquals(creditOfferDac.getDealerOrderNumber(), loanDemand.getLoanApplicationId());
    }

    @Test
    void toDuration() {
        assertEquals(creditOfferDac.getDuration(), Integer.valueOf(loanDemand.getDuration().getValue()));
    }

    @Test
    void toProductType() {
        assertEquals(creditOfferDac.getProductType(), creditPlusConfig.getProductType());
    }

    @Test
    void toIpAdress() {
        assertEquals(creditOfferDac.getIpAddress(), loanDemand.getRequestIp());
    }

    @Test
    void toAdditionalData() {
        assertNotNull(creditOfferDac.getAdditionalData());
    }

    @Test
    void toDebtor1() {
        assertNotNull(creditOfferDac.getDebtor1());
    }
}