package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.partner.consors.model.EmploymentDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {EmployerAddressMapper.class, ConsorsProfessionMapper.class})
interface EmploymentDetailsMapper {
    @Mapping(target = "profession", source = "employmentType")
    @Mapping(target = "professionBeginDate", source = "employmentSince", dateFormat = "yyyy-MM")
    @Mapping(target = "employerAddress", source = ".")
    @Mapping(target = "professionEndDate", source = "professionEndDate", dateFormat = "yyyy-MM")
    EmploymentDetails toEmploymentDetails(de.joonko.loan.offer.domain.EmploymentDetails employmentDetails);
}
