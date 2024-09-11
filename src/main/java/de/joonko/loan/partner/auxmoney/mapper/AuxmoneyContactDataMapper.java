package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.offer.domain.ContactData;
import de.joonko.loan.offer.domain.ZipCode;
import de.joonko.loan.partner.auxmoney.model.BorrowerContactData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;

@Mapper(componentModel = "spring")
interface AuxmoneyContactDataMapper {
    @Mapping(target = "livingSince", source = "livingSince")
    @Mapping(source = "streetName", target = "streetName")
    @Mapping(source = "zipCode", target = "zipCode", qualifiedByName = "convertZipCodeToString")
    @Mapping(source = "city", target = "city")
    @Mapping(target = "telephone", source = "mobile")
    @Mapping(target = "mobileTelephone", source = "mobile")
    @Mapping(target = "email", source = "email.emailString")
    BorrowerContactData toAuxmoneyContactData(ContactData contactData);


    @Named("convertZipCodeToString")
    static String convertZipCodeToString(ZipCode zipCode) {
        if (Optional.ofNullable(zipCode)
                .isPresent()) {
            return String.valueOf(zipCode.getCode());
        } else {
            return null;
        }
    }


}
