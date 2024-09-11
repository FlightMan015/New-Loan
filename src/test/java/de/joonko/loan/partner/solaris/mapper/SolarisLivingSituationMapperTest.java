package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.offer.domain.HousingType;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.solaris.model.LivingSituation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolarisLivingSituationMapperTest extends BaseMapperTest {

    @Autowired
    SolarisLivingSituationMapper solarisLivingSituationMapper;

    @Test
    void shouldMapOwnerToLivingInOwnHouse() {
        LivingSituation livingSituation = solarisLivingSituationMapper.toSolarisLivingSituation(HousingType.OWNER);
        assertEquals(LivingSituation.LIVING_IN_OWN_HOUSE, livingSituation);
    }

    @Test
    void shouldMapRentToLivingInRentedHouse() {
        LivingSituation livingSituation = solarisLivingSituationMapper.toSolarisLivingSituation(HousingType.RENT);
        assertEquals(LivingSituation.LIVING_IN_RENTED_HOUSE, livingSituation);
    }
}
