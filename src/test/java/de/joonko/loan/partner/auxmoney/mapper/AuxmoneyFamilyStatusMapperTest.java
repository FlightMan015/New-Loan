package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.offer.domain.FamilyStatus;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;


class AuxmoneyFamilyStatusMapperTest extends BaseMapperTest {

    @Autowired
    private AuxmoneyFamilyStatusMapper auxmoneyFamilyStatusMapper;

    @Test
    @DisplayName("should map Widowed to verwitwet")
    void verwitwetMapping() {
        de.joonko.loan.partner.auxmoney.model.FamilyStatus familyStatus = auxmoneyFamilyStatusMapper.toAuxmoneyFamilyStatus(FamilyStatus.WIDOWED);
        assertEquals(de.joonko.loan.partner.auxmoney.model.FamilyStatus.VERWITWET, familyStatus);
    }

    @Test
    @DisplayName("should map divorced to geschieden")
    void geschiedenMapping() {
        de.joonko.loan.partner.auxmoney.model.FamilyStatus familyStatus = auxmoneyFamilyStatusMapper.toAuxmoneyFamilyStatus(FamilyStatus.DIVORCED);
        assertEquals(de.joonko.loan.partner.auxmoney.model.FamilyStatus.GESCHIEDEN, familyStatus);
    }

    @Test
    @DisplayName("should map living_separately to getrennt_lebend")
    void getrenntLebendMapping() {
        de.joonko.loan.partner.auxmoney.model.FamilyStatus familyStatus = auxmoneyFamilyStatusMapper.toAuxmoneyFamilyStatus(FamilyStatus.LIVING_SEPARATELY);
        assertEquals(de.joonko.loan.partner.auxmoney.model.FamilyStatus.GETRENNT_LEBEND, familyStatus);
    }

    @Test
    @DisplayName("should map single to ledig")
    void singleMapping() {
        de.joonko.loan.partner.auxmoney.model.FamilyStatus familyStatus = auxmoneyFamilyStatusMapper.toAuxmoneyFamilyStatus(FamilyStatus.SINGLE);
        assertEquals(de.joonko.loan.partner.auxmoney.model.FamilyStatus.LEDIG, familyStatus);
    }

    @Test
    @DisplayName("should map Married to verheiratet")
    void verheiratetMapping() {
        de.joonko.loan.partner.auxmoney.model.FamilyStatus familyStatus = auxmoneyFamilyStatusMapper.toAuxmoneyFamilyStatus(FamilyStatus.MARRIED);
        assertEquals(de.joonko.loan.partner.auxmoney.model.FamilyStatus.VERHEIRATET, familyStatus);
    }

    @Test
    @DisplayName("should map LIVING_IN_LONGTERM_RELATIONSHIP to LEDIG")
    void livinRelationshipMapping() {
        de.joonko.loan.partner.auxmoney.model.FamilyStatus familyStatus = auxmoneyFamilyStatusMapper.toAuxmoneyFamilyStatus(FamilyStatus.LIVING_IN_LONGTERM_RELATIONSHIP);
        assertEquals(de.joonko.loan.partner.auxmoney.model.FamilyStatus.LEDIG, familyStatus);
    }
}

