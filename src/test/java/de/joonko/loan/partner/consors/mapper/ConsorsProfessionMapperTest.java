package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.EmploymentType;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.consors.model.Profession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsorsProfessionMapperTest extends BaseMapperTest {

    @Autowired
    ConsorsProfessionMapper consorsProfessionMapper;

    @Test
    void regular() {
        Profession profession = consorsProfessionMapper.toConsorsProfession(EmploymentType.REGULAR_EMPLOYED);
        assertEquals(Profession.REGULAR_EMPLOYED, profession);
    }

    @Test
    void other() {
        Profession profession = consorsProfessionMapper.toConsorsProfession(EmploymentType.OTHER);
        assertEquals(Profession.UNEMPLOYED, profession);
    }

}
