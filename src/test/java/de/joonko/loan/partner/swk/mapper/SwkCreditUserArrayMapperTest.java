package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SwkCreditUserArrayMapperTest extends BaseMapperTest {

    @Autowired
    SwkCreditUserArrayMapper swkCreditUserArrayMapper;

    @Random
    LoanDemand loanDemand;

    @Test
    void from() {
        CreditApplicationServiceStub.CreditUser[] creditUsers = swkCreditUserArrayMapper.from(loanDemand);
        assertEquals(1, creditUsers.length);
    }
}