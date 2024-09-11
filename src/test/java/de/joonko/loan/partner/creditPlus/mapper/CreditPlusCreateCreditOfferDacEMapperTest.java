package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreditPlusCreateCreditOfferDacEMapperTest extends BaseMapperTest {

    @Autowired
    CreditPlusCreateCreditOfferDacEMapper mapper;

    @Random
    LoanDemand loanDemand;

    @Test
    void toCreateCreditOfferRequest() {
        EfinComparerServiceStub.CreateCreditOfferDac createCreditOfferDac = mapper.toCreateCreditOfferRequest(loanDemand);
        assertNotNull(createCreditOfferDac);
    }
}