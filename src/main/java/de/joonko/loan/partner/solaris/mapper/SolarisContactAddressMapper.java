package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.offer.domain.PreviousAddress;
import de.joonko.loan.partner.solaris.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolarisContactAddressMapper {

    @Mapping(target = "city", source = "city")
    @Mapping(target = "line1", source = "street")
    @Mapping(target = "postalCode", source = "postCode")
    Address toSolarisRequest(PreviousAddress previousAddress);
}
