package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.FamilyStatus;
import de.joonko.loan.offer.domain.Gender;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CreditPlusDebtorPersonalDataMapper {

    @Mapping(target = "salutation", source = "personalDetails.gender", qualifiedByName = "toSalutation")
    @Mapping(target = "foreName", source = "personalDetails.firstName")
    @Mapping(target = "lastName", source = "personalDetails.lastName")
    @Mapping(target = "placeOfBirth", source = "personalDetails.placeOfBirth")
    @Mapping(target = "dateOfBirth", source = "personalDetails.birthDate")
    @Mapping(target = "street", source = "contactData.streetName")
    @Mapping(target = "postalCode", source = "contactData.zipCode.code")
    @Mapping(target = "city", source = "contactData.city")
    @Mapping(target = "lastRemoval", source = "contactData.livingSince")
    @Mapping(target = "maritalState", source = "personalDetails.familyStatus", qualifiedByName = "toMaritalState")
    @Mapping(target = "numberOfChildren", source = "personalDetails.numberOfChildren")
    @Mapping(target = "email", source = "contactData.email.emailString")
    @Mapping(target = "identificationType", ignore = true)
    @Mapping(target = "nationality", source = "personalDetails.nationality.countryCode.alpha2")
    @Mapping(target = "preCity", source = "contactData.previousAddress.city")
    @Mapping(target = "prePostalCode", source = "contactData.previousAddress.postCode")
    @Mapping(target = "preStreet", source = "contactData.previousAddress.street")
    @Mapping(target = "preCountry", source = "contactData.previousAddress.country.countryCode.alpha2")
    @Mapping(target = "phoneMobile.areaCode", source = "contactData.mobile", qualifiedByName = "toPhoneMobileAreaCode")
    @Mapping(target = "phoneMobile.localNumber", source = "contactData.mobile", qualifiedByName = "toPhoneMobileLocalNumber")
    @Mapping(target = "residencePermission", ignore = true)
    @Mapping(target = "residencePermissionDate", ignore = true)
    @Mapping(target = "workPermission", ignore = true)
    @Mapping(target = "workPermissionDate", ignore = true)

    EfinComparerServiceStub.PersonalData toPersonalData(LoanDemand loanDemand);

    @Named("toSalutation")
    default String toSalutation(Gender gender) {
        return gender.equals(Gender.MALE) ? CreditPlusDefaults.MALE : CreditPlusDefaults.FEMALE;
    }

    @Named("toMaritalState")
    default Integer toMaritalState(FamilyStatus familyStatus) {
        switch (familyStatus) {
            case SINGLE:
            case LIVING_IN_LONGTERM_RELATIONSHIP:
                return 1;
            case MARRIED:
                return 2;
            case DIVORCED:
                return 3;
            case WIDOWED:
                return 4;
            case LIVING_SEPARATELY:
                return 5;
        }
        return null;
    }

    @Named("toPhoneMobileAreaCode")
    default String toPhoneMobileAreaCode(String phoneNumber) {
        return "0".concat(phoneNumber.substring(2, 5));
    }

    @Named("toPhoneMobileLocalNumber")
    default String toPhoneMobileLocalNumber(String phoneNumber) {
        return phoneNumber.substring(5);
    }
}