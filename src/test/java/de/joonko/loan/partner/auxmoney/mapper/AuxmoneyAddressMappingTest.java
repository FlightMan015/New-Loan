package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.offer.domain.Gender;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.auxmoney.model.Salutation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuxmoneyAddressMappingTest extends BaseMapperTest {

    @Autowired
    AuxmoneyAddressMapping auxmoneyAddressMapping;

    @DisplayName("Should convert Male to Auxmoney HERR")
    @Test
    void herr() {
        Salutation salutation = auxmoneyAddressMapping.toAuxmoneySalutation(Gender.MALE);
        assertEquals(Salutation.HERR, salutation);
    }

    @DisplayName("Should convert Female to Auxmoney FRAU")
    @Test
    void frau() {
        Salutation salutation = auxmoneyAddressMapping.toAuxmoneySalutation(Gender.FEMALE);
        assertEquals(Salutation.FRAU, salutation);
    }
}
