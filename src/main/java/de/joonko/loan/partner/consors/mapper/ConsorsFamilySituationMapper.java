package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.FamilyStatus;
import de.joonko.loan.partner.consors.model.FamilySituation;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(componentModel = "spring")
public interface ConsorsFamilySituationMapper {

    @ValueMappings({
            @ValueMapping(source = "SINGLE", target = "FREE"),
            @ValueMapping(source = "MARRIED", target = "MARRIED"),
            @ValueMapping(source = "WIDOWED", target = "WIDOWED"),
            @ValueMapping(source = "DIVORCED", target = "DIVORCED"),
            @ValueMapping(source = "LIVING_SEPARATELY", target = "SEPARATED"),
            @ValueMapping(source = "LIVING_IN_LONGTERM_RELATIONSHIP", target = "COHABIT")

    })
    FamilySituation toLoanProviderFamilySituation(FamilyStatus familyStatus);
}
