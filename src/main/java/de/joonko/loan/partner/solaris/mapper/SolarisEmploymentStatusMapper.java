package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.offer.domain.EmploymentType;
import de.joonko.loan.partner.solaris.model.EmploymentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(componentModel = "spring")
public interface SolarisEmploymentStatusMapper {

    @ValueMappings({
            @ValueMapping(target = "EMPLOYED", source = "REGULAR_EMPLOYED"),
            @ValueMapping(target = "UNEMPLOYED", source = "OTHER")
    })
    EmploymentStatus toProvidersEmploymentStatus(EmploymentType employmentType);
}
