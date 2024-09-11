package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.Gender;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsorsGenderMapperTest extends BaseMapperTest {

    @Autowired
    private ConsorsGenderMapper consorsGenderMapper;

    @Test
    @DisplayName("Should convert to consors male")
    void male() {
        de.joonko.loan.partner.consors.model.Gender gender = consorsGenderMapper.toConsorsGender(Gender.MALE);
        assertEquals(de.joonko.loan.partner.consors.model.Gender.MALE, gender);
    }

    @Test
    @DisplayName("Should convert to consors female")
    void female() {
        de.joonko.loan.partner.consors.model.Gender gender = consorsGenderMapper.toConsorsGender(Gender.FEMALE);
        assertEquals(de.joonko.loan.partner.consors.model.Gender.FEMALE, gender);
    }
}
