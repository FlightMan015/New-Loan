package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.offer.domain.EmploymentDetails;
import de.joonko.loan.partner.auxmoney.model.EmployerData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface AuxmoneyEmployerDataMapper {

    @Mappings({
            @Mapping(target = "city", source = "city"),
            @Mapping(target = "zip", source = "zipCode.code"),
            @Mapping(target = "street", source = "streetName"),
            @Mapping(target = "company", source = "employerName"),
            @Mapping(target = "since", source = "employmentSince"),
            @Mapping(target = "phone", ignore = true),
            @Mapping(target = "employmentStatus", ignore = true),
    })
    EmployerData toAuxmoneyEmployerData(EmploymentDetails employmentDetails);
}
