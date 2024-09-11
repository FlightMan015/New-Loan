package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.common.utils.CommonUtils;
import de.joonko.loan.offer.domain.PreviousAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ConsorsPreviousAddressMapper {


    @Mapping(target = "city", expression = "java(de.joonko.loan.common.utils.CommonUtils.normalizeString(previousAddress.getCity()))")
    @Mapping(source = "postCode", target = "zipcode")
    @Mapping(source = "street", target = "street")
    de.joonko.loan.partner.consors.model.PreviousAddress toConsorsPreviousMapper(PreviousAddress previousAddress);
}
