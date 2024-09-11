package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.FamilyStatus;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.consors.model.FamilySituation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConsorsFamilySituationMapperTest extends BaseMapperTest {

    @Autowired
    ConsorsFamilySituationMapper consorsFamilySituationMapper;

    @ParameterizedTest(name = "Should  Not Miss any Mapping from FamilyStatus with Value [{arguments}]")
    @EnumSource(FamilyStatus.class)
    void allFamilyStatus(FamilyStatus familyStatus) {
        assertNotNull(consorsFamilySituationMapper.toLoanProviderFamilySituation(familyStatus));
    }

    @Test
    void ledig() {
        assertEquals(FamilySituation.FREE, consorsFamilySituationMapper.toLoanProviderFamilySituation(FamilyStatus.SINGLE));
    }

    @Test
    void verheiratet() {
        assertEquals(FamilySituation.MARRIED, consorsFamilySituationMapper.toLoanProviderFamilySituation(FamilyStatus.MARRIED));
    }

    @Test
    void geschieden() {
        assertEquals(FamilySituation.DIVORCED, consorsFamilySituationMapper.toLoanProviderFamilySituation(FamilyStatus.DIVORCED));
    }

    @Test
    void getrennt_lebend() {
        assertEquals(FamilySituation.SEPARATED, consorsFamilySituationMapper.toLoanProviderFamilySituation(FamilyStatus.LIVING_SEPARATELY));
    }

    @Test
    void verwitwet() {
        assertEquals(FamilySituation.WIDOWED, consorsFamilySituationMapper.toLoanProviderFamilySituation(FamilyStatus.WIDOWED));
    }

    @Test
    void cohabit() {
        assertEquals(FamilySituation.COHABIT, consorsFamilySituationMapper.toLoanProviderFamilySituation(FamilyStatus.LIVING_IN_LONGTERM_RELATIONSHIP));
    }
}

