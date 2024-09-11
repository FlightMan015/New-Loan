package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.offer.domain.ContactData;
import de.joonko.loan.offer.domain.ZipCode;
import de.joonko.loan.partner.solaris.model.Address;
import de.joonko.loan.partner.solaris.model.SolarisBankDefaults;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface SolarisAddressMapper {

    @Mapping(target = "city", source = "city")
    @Mapping(target = "line1", source = "streetName")
    @Mapping(target = "line2", source = "streetNumber")
    @Mapping(target = "country", constant = SolarisBankDefaults.COUNTRY_DE)
    @Mapping(target = "postalCode", source = "zipCode", qualifiedByName = "convertZipCodeToString")
    Address toSolarisRequest(ContactData contactData);

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
