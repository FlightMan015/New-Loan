package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.partner.auxmoney.model.FamilyStatus;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(componentModel = "spring")
public interface AuxmoneyFamilyStatusMapper {

    @ValueMappings({
            @ValueMapping(source = "WIDOWED", target = "VERWITWET"),
            @ValueMapping(source = "DIVORCED", target = "GESCHIEDEN"),
            @ValueMapping(source = "MARRIED", target = "VERHEIRATET"),
            @ValueMapping(source = "LIVING_SEPARATELY", target = "GETRENNT_LEBEND"),
            @ValueMapping(source = "SINGLE", target = "LEDIG"),
            @ValueMapping(source = "LIVING_IN_LONGTERM_RELATIONSHIP", target = "LEDIG")
    })
    FamilyStatus toAuxmoneyFamilyStatus(de.joonko.loan.offer.domain.FamilyStatus familyStatus);
}
