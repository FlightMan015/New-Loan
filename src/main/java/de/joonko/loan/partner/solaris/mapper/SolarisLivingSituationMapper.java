package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.offer.domain.HousingType;
import de.joonko.loan.partner.solaris.model.LivingSituation;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(componentModel = "spring")
public interface SolarisLivingSituationMapper {

    @ValueMappings({
            @ValueMapping(target = "LIVING_IN_OWN_HOUSE", source = "OWNER"),
            @ValueMapping(target = "LIVING_IN_RENTED_HOUSE", source = "RENT")
    })
    LivingSituation toSolarisLivingSituation(HousingType housingType);
}
