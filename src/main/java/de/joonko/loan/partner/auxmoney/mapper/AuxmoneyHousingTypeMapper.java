package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.partner.auxmoney.model.HousingType;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(componentModel = "spring")
public interface AuxmoneyHousingTypeMapper {

    @ValueMappings({
            @ValueMapping(source = "RENT", target = "RENT"),
            @ValueMapping(source = "OWNER", target = "OWNERSHIP")
    })
    HousingType toAuxmoneyHousingType(de.joonko.loan.offer.domain.HousingType housingType);
}
