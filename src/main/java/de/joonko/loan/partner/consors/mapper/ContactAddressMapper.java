package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.ContactData;
import de.joonko.loan.partner.consors.model.ContactAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring",uses = {
        StringMapper.class
})
interface ContactAddressMapper {

    @Mapping(target = "zipcode", qualifiedByName = "trimWhiteSpaces",source = "zipCode.code")
    @Mapping(target = "email", source = "email.emailString")
    @Mapping(target = "city", expression = "java(de.joonko.loan.common.utils.CommonUtils.normalizeString(contactData.getCity()))")
    @Mapping(target = "street", qualifiedByName = "trimWhiteSpaces",source = "streetName")
    @Mapping(target = "telephoneLandline", ignore = true)
    @Mapping(target = "telephoneMobile", source = "mobile", qualifiedByName = "convertToConsorsMobileFormat")
    @Mapping(target = "validFrom", source = "livingSince", dateFormat = "YYYY-MM")
    ContactAddress toLoanProviderSubscriber(ContactData contactData);

    @Named("convertToConsorsMobileFormat")
    default String convertToConsorsMobileFormat(String mobile) {
        return new StringBuilder("0").append(mobile)
                .toString();
    }
}
