package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.offer.domain.HousingType;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuxmoneyHousingTypeMapperTest extends BaseMapperTest {

    @Autowired
    private AuxmoneyHousingTypeMapper auxmoneyHousingTypeMapper;

    @Test
    void ownership() {

        de.joonko.loan.partner.auxmoney.model.HousingType housingType = auxmoneyHousingTypeMapper.toAuxmoneyHousingType(HousingType.OWNER);
        assertEquals(de.joonko.loan.partner.auxmoney.model.HousingType.OWNERSHIP, housingType);
    }

    @Test
    void rent() {

        de.joonko.loan.partner.auxmoney.model.HousingType housingType = auxmoneyHousingTypeMapper.toAuxmoneyHousingType(HousingType.RENT);
        assertEquals(de.joonko.loan.partner.auxmoney.model.HousingType.RENT, housingType);
    }
}
