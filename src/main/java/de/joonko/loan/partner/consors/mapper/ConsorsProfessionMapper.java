package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.EmploymentType;
import de.joonko.loan.partner.consors.model.Profession;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(componentModel = "spring")
public interface ConsorsProfessionMapper {

    @ValueMappings({
            @ValueMapping(source = "OTHER", target = "UNEMPLOYED"),
            @ValueMapping(source = "REGULAR_EMPLOYED", target = "REGULAR_EMPLOYED")
    })
    Profession toConsorsProfession(EmploymentType employmentType);

}
