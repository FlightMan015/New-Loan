package de.joonko.loan.partner.auxmoney.mapper;


import de.joonko.loan.offer.domain.PersonalDetails;
import de.joonko.loan.partner.auxmoney.AuxmoneyDefaults;
import de.joonko.loan.partner.auxmoney.model.MainEarner;
import de.joonko.loan.partner.auxmoney.model.PersonalData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {AuxmoneyAddressMapping.class, AuxmoneyFamilyStatusMapper.class, AuxmoneyHousingTypeMapper.class})
interface AuxmoneyPersonalDataMapper {

    @Mapping(source = "gender", target = "address")
    @Mapping(source = "firstName", target = "forename")
    @Mapping(source = "lastName", target = "surname")
    @Mapping(source = "familyStatus", target = "familyStatus")
    @Mapping(source = "birthDate", target = "birthDate")
    @Mapping(target = "nationality", source = "nationality.countryCode.alpha2")
    @Mapping(source = "numberOfCreditCard", target = "hasCreditCard", qualifiedByName = "creditCardMapper")
    @Mapping(target = "hasEcCard", constant = AuxmoneyDefaults.HAS_EC_CARD)
    @Mapping(source = ".", target = "hasRealEstate", qualifiedByName = "realEstateMapper")
    @Mapping(source = "housingType", target = "housingType")
    @Mapping(source = "mainEarner", target = "mainEarner", qualifiedByName = "mainEarnerMapper")
    @Mapping(target = "taxIdentificationNumber", ignore = true)
    @Mapping(target = "occupation", ignore = true)
    @Mapping(target = "carOwner", ignore = true)
    PersonalData toAuxmoneyPersonalData(PersonalDetails personalDetails);

    @Named("creditCardMapper")
    default Integer creditCardMapper(Integer numberOfCreditCard) {
        return numberOfCreditCard >= 1 ? 1 : 0;
    }

    @Named("realEstateMapper")
    default Integer realEstateMapper(PersonalDetails personalDetails) {
        return personalDetails.hasRealEstate() ? 1 : 0;
    }

    @Named("mainEarnerMapper")
    default MainEarner mainEarnerMapper(boolean mainEarner) {
        return mainEarner ? MainEarner.YES : MainEarner.NO;
    }

}
