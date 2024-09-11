package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.partner.consors.model.Gender;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(componentModel = "spring")
public interface ConsorsGenderMapper {

    @ValueMappings({
            @ValueMapping(source = "MALE", target = "MALE"),
            @ValueMapping(source = "FEMALE", target = "FEMALE")
    })
    Gender toConsorsGender(de.joonko.loan.offer.domain.Gender gender);
}
