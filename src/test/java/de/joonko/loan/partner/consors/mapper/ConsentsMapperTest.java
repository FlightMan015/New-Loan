package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.consors.ConsorsDefaults;
import de.joonko.loan.partner.consors.model.Consents;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class ConsentsMapperTest extends BaseMapperTest {

    @Autowired
    private ConsentsMapper mapper;

    @Random
    private LoanDemand loanDemand;

    @Test
    void consors_consents_are_valid() {
        // given
        // when
        Consents consents = mapper.toConsent(loanDemand);

        // then
        assertAll(
                () -> assertTrue(consents.isSchufaCallAllowed()),
                () -> assertEquals(ConsorsDefaults.CONSENT_POST_EMAIL_SELL_CONTACT, consents.getCustomerContactedByPhoneAndEmailForPromotions())
        );
    }
}
