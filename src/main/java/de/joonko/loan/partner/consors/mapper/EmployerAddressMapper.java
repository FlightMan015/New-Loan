package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.partner.consors.model.EmployerAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        StringMapper.class
})
interface EmployerAddressMapper {

    @Mapping(target = "employerName", qualifiedByName = "trimWhiteSpaces", source = "employerName")
    @Mapping(target = "employerStreet", qualifiedByName = "trimWhiteSpaces", source = "streetName")
    @Mapping(target = "employerZipcode", qualifiedByName = "trimWhiteSpaces", source = "zipCode.code")
    @Mapping(target = "employerCity", qualifiedByName = "trimWhiteSpaces", source = "city")
    EmployerAddress toEmployerAddress(de.joonko.loan.offer.domain.EmploymentDetails employmentDetails);
}
