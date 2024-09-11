package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.Nationality;
import de.joonko.loan.offer.domain.PreviousAddress;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsorsPreviousAddressMapperTest extends BaseMapperTest {

    @Autowired
    private ConsorsPreviousAddressMapper consorsPreviousAddressMapper;

    private PreviousAddress previousAddress;

    @BeforeEach
    public void setup() {
        previousAddress = new PreviousAddress("Berlin Street", "12345", "Berlin", Nationality.DE, null, LocalDate.now());
    }

    @Test
    void city() {
        de.joonko.loan.partner.consors.model.PreviousAddress consorsPreviousAddress = consorsPreviousAddressMapper.toConsorsPreviousMapper(previousAddress);
        assertEquals("Berlin", consorsPreviousAddress.getCity());
    }

    @Test
    void street() {
        de.joonko.loan.partner.consors.model.PreviousAddress consorsPreviousAddress = consorsPreviousAddressMapper.toConsorsPreviousMapper(previousAddress);
        assertEquals("Berlin Street", consorsPreviousAddress.getStreet());
    }

    @Test
    void postCode() {
        de.joonko.loan.partner.consors.model.PreviousAddress consorsPreviousAddress = consorsPreviousAddressMapper.toConsorsPreviousMapper(previousAddress);
        assertEquals("12345", consorsPreviousAddress.getZipcode());
    }


}
