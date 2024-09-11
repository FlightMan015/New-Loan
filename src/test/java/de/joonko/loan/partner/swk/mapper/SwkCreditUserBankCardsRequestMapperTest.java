package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SwkCreditUserBankCardsRequestMapperTest extends BaseMapperTest {

    @Autowired
    SwkCreditUserBankCardsRequestMapper mapper;

    @Test
    void toEcCard() {
        CreditApplicationServiceStub.BankCardsInformation bankCardsInformation = mapper.toBankCardsInformation(3);
        assertEquals(true, bankCardsInformation.getEcCard());
    }

    @Test
    void toCreditCardMoreThanOne() {
        Boolean hasCreditCard = mapper.creditCardMapper(2);
        assertEquals(true, hasCreditCard);
    }

    @Test
    void toCreditCardLessThanOne() {
        Boolean hasCreditCard = mapper.creditCardMapper(0);
        assertEquals(false, hasCreditCard);
    }
}